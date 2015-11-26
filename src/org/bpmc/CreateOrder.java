/*
 *************************************************************************
 * The contents of this file are subject to the Openbravo  Public  License
 * Version  1.1  (the  "License"),  being   the  Mozilla   Public  License
 * Version 1.1  with a permitted attribution clause; you may not  use this
 * file except in compliance with the License. You  may  obtain  a copy of
 * the License at http://www.openbravo.com/legal/license.html
 * Software distributed under the License  is  distributed  on  an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific  language  governing  rights  and  limitations
 * under the License.
 * The Original Code is Openbravo ERP.
 * The Initial Developer of the Original Code is Openbravo SLU
 * All portions are Copyright (C) 2015 Openbravo SLU
 * All Rights Reserved.
 * Contributor(s):  ______________________________________.
 ************************************************************************
 */
package org.bpmc;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.base.provider.OBProvider;
import org.openbravo.client.application.process.BaseProcessActionHandler;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.utility.OBMessageUtils;
import org.openbravo.service.db.DbUtility;

public class CreateOrder extends BaseProcessActionHandler {
  private static final Logger log = Logger.getLogger(CreateOrder.class);

  @Override
  protected JSONObject doExecute(Map<String, Object> parameters, String data) {
    try {
      final JSONObject jsonData = new JSONObject(data);
      final Application application = OBDal.getInstance().get(Application.class,
          jsonData.getString("Bpmc_Application_ID"));
      final BPMCOrder order = OBProvider.getInstance().get(BPMCOrder.class);
      if (jsonData.has("inpbpmcTenderOfferId")) {
        final TenderOffer tenderOffer = OBDal.getInstance().get(TenderOffer.class,
            jsonData.getString("inpbpmcTenderOfferId"));
        order.setBpmcTenderOffer(tenderOffer);
        order.setBusinessPartner(tenderOffer.getBusinessPartner());
        order.setMaxAmount((long) (tenderOffer.getOfferamount() * 1.1));
      } else if (jsonData.has("inpbpmcSupplierSelectionId")) {
        final SupplierSelection selection = OBDal.getInstance().get(SupplierSelection.class,
            jsonData.getString("inpbpmcSupplierSelectionId"));
        order.setBusinessPartner(selection.getBusinessPartner());
        selection.setOrdercreated(true);
      }
      order.setApplication(application);
      order.setOrderdate(new Date());
      order.setDescription(application.getDescription());
      order.setOrderstart(new Date());
      order.setOrderend(new Date());
      order.setOrderstatus("Created");
      String systemTimeMillis = "" + System.currentTimeMillis();
      order
          .setOrdernumber("WMO" + ("" + systemTimeMillis).substring(systemTimeMillis.length() - 6));
      OBDal.getInstance().save(order);
      return getSuccessMessage("Success");
    } catch (Exception e) {
      OBDal.getInstance().rollbackAndClose();
      try {
        Throwable ex = DbUtility.getUnderlyingSQLException(e);
        String message = OBMessageUtils.translateError(ex.getMessage()).getMessage();
        return getErrorMessage(message);
      } catch (Exception ignore) {
      }
    }

    return new JSONObject();
  }

  /**
   * Returns a JSONObject with the success message to be printed
   */
  private static JSONObject getSuccessMessage(final String msgText) {
    final JSONObject result = new JSONObject();
    try {
      final JSONArray actions = new JSONArray();
      final JSONObject msgInBPTab = new JSONObject();
      msgInBPTab.put("msgType", "success");
      msgInBPTab.put("msgTitle", OBMessageUtils.messageBD("success"));
      msgInBPTab.put("msgText", msgText);
      final JSONObject msgInBPTabAction = new JSONObject();
      msgInBPTabAction.put("showMsgInProcessView", msgInBPTab);
      actions.put(msgInBPTabAction);
      result.put("responseActions", actions);
    } catch (Exception e) {
      log.error(e);
    }

    return result;
  }

  /**
   * Returns a JSONObject with the error message to be printed and retry execution
   */
  private static JSONObject getErrorMessage(final String msgText) {
    final JSONObject result = new JSONObject();
    try {
      final JSONObject msg = new JSONObject();
      msg.put("severity", "error");
      msg.put("text", msgText);
      result.put("message", msg);
      result.put("retryExecution", true);
    } catch (Exception e) {
      log.error(e);
    }
    return result;
  }

}
