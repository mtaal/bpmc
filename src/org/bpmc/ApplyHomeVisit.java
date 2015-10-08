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

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.erpCommon.utility.OBMessageUtils;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.DalBaseProcess;
import org.openbravo.service.json.JsonUtils;

public class ApplyHomeVisit extends DalBaseProcess {
  private static final Logger log = Logger.getLogger(ApplyHomeVisit.class);
  private static final SimpleDateFormat jsDateFormat = JsonUtils.createDateFormat();
  private static final String ACTION_PROCESS_TRANSACTION = "P";

  @Override
  public void doExecute(ProcessBundle bundle) throws Exception {
    try {
      System.err.println(bundle);
      // {visitadvice=BPMC_MedicalAdviceNeeded, information=tadsda, adOrgId=, visitdate=12-10-2015,
      // description=Test, tabId=AD57101DB8C14A9EA95EC33FFD142B24,
      // Bpmc_Application_ID=B8A0C0639C8341D3B8C05C4407576D92, adClientId=}

      // final JSONObject jsonData = new JSONObject(data);
      // final JSONObject jsonparams = jsonData.getJSONObject("_params");
      // System.err.println(jsonparams);
      //
      // // Success Message
      // return getSuccessMessage("Success!!");

    } catch (Exception e) {
      // OBDal.getInstance().rollbackAndClose();
      // String message = OBMessageUtils.translateError(e.getMessage()).getMessage();
      // return getErrorMessage(message);
    }
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
