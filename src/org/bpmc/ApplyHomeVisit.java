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
import java.util.Date;

import org.apache.log4j.Logger;
import org.openbravo.base.exception.OBException;
import org.openbravo.base.provider.OBProvider;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.DalBaseProcess;

public class ApplyHomeVisit extends DalBaseProcess {
  private static final Logger log = Logger.getLogger(ApplyHomeVisit.class);

  @Override
  public void doExecute(ProcessBundle bundle) throws Exception {
    try {
      final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      dateFormat.setLenient(true);
      Application application = OBDal.getInstance().get(Application.class,
          bundle.getParams().get("Bpmc_Application_ID"));
      HomeVisit homeVisit = OBProvider.getInstance().get(HomeVisit.class);
      homeVisit.setApplication(application);
      homeVisit.setDescription((String) bundle.getParams().get("description"));
      homeVisit.setInformation((String) bundle.getParams().get("information"));
      homeVisit.setAdvicedate(new Date());
      homeVisit.setVisitdate(dateFormat.parse((String) bundle.getParams().get("visitdate")));
      final String advice = (String) bundle.getParams().get("visitadvice");
      homeVisit.setAdvice(advice);
      if (advice.equals("BPMC_MedicalAdviceNeeded")) {
        application.setApplicationStatus("BPMC_MedicalAdvice");
      } else if (advice.equals("BPMC_NoMedicalAdviceNeeded")) {
        application.setApplicationStatus("BPMC_Decide");
      }
      OBDal.getInstance().save(homeVisit);
      final OBError msg = new OBError();
      msg.setType("Success");
      msg.setTitle("@Success@");
      msg.setMessage("@Success@");
      bundle.setResult(msg);
    } catch (final OBException e) {
      final OBError msg = new OBError();
      msg.setType("Error");
      msg.setMessage(e.getMessage());
      msg.setTitle("@Error@");
      OBDal.getInstance().rollbackAndClose();
      bundle.setResult(msg);
    }
  }
}
