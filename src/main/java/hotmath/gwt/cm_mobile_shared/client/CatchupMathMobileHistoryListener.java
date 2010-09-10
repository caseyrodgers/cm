package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CatchupMathMobileHistoryListener implements ValueChangeHandler<String> {
    public void onValueChange(ValueChangeEvent<String> event) {

        String historyToken = event.getValue();
        // do default action
        if(historyToken.equals("quiz")) {
            Controller.navigateToQuiz(null);
        }
        else if(historyToken.startsWith("lesson")) {
            CmMobileUser user = CatchupMathMobileShared.getUser();
            
            int sessionNum = Integer.parseInt(historyToken.split(":")[1]);
            if(sessionNum == user.getPrescripion().getCurrSession().getSessionNumber()) {
                Controller.navigateToPrescription(null, user.getPrescripion().getCurrSession());
                return;
            }
            else {
                CatchupMathMobileShared.__instance.getControlPanel().showBusy(true);
            	GetPrescriptionAction action = new GetPrescriptionAction(user.getRunId(), sessionNum, true);
            	CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<PrescriptionSessionResponse>() {
                    public void onSuccess(PrescriptionSessionResponse prescriptionSession) {
                        CatchupMathMobileShared.getUser().setPrescripion(prescriptionSession.getPrescriptionData());
                        Controller.navigateToPrescription(null, prescriptionSession.getPrescriptionData().getCurrSession());
                        CatchupMathMobileShared.__instance.getControlPanel().showBusy(false);
                    }
                    @Override
                    public void onFailure(Throwable arg0) {
                        CatchupMathMobileShared.__instance.getControlPanel().showBusy(false);
                        Window.alert(arg0.getMessage());
                    }
                });
            }
        }
        else if(historyToken.startsWith("resource")) {
            /** parse history 
             * TODO: abstract history token
             * 
             */
            String p[] = historyToken.split(":");
            String type = p[1];
            int ordinal = Integer.parseInt(p[2]);
            
            /** find the item referenced via historyToken
             * 
             */
            InmhItemData item = null;
            for (PrescriptionSessionDataResource  r: CatchupMathMobileShared.getUser().getPrescripion().getCurrSession().getInmhResources()) {
                if(r.getType().equals(type)) {
                    item = r.getItems().get(ordinal);
                    break;
                }
            }
            /** assert(item != null); */
            Controller.navigateToPrescriptionResource(null, item, ordinal);
        }
        else {
            Controller.navigateToLogin();
        }
    }
}
