package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class CatchupMathMobileHistoryListener implements ValueChangeHandler<String> {
    public void onValueChange(ValueChangeEvent<String> event) {

        String historyToken = event.getValue();
        // do default action
        if(historyToken.equals("quiz")) {
            CatchupMathMobile.__instance.showQuizPanel();
        }
        else if(historyToken.startsWith("lesson")) {
            CmMobileUser user = CatchupMathMobile.getUser();
            
            int sessionNum = Integer.parseInt(historyToken.split(":")[1]);
            if(sessionNum == user.getPrescripion().getCurrSession().getSessionNumber()) {
                CatchupMathMobile.__instance.showPrescriptionPanel();
                return;
            }
            else {
//                GetPrescriptionAction action = new GetPrescriptionAction(user.getRunId(), sessionNum, true);
//                CatchupMathMobile.getCmService().execute(action,new AsyncCallback<PrescriptionSessionResponse>() {
//                    public void onSuccess(PrescriptionSessionResponse prescriptionSession) {
//                        CatchupMathMobile.getUser().setPrescripion(prescriptionSession.getPrescriptionData());
//                        CatchupMathMobile.__instance.showPrescriptionPanel();                
//                    }
//                    @Override
//                    public void onFailure(Throwable arg0) {
//                        Window.alert(arg0.getMessage());
//                    }
//                });
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
            for (PrescriptionSessionDataResource  r: CatchupMathMobile.getUser().getPrescripion().getCurrSession().getInmhResources()) {
                if(r.getType().equals(type)) {
                    item = r.getItems().get(ordinal);
                    break;
                }
            }
            /** assert(item != null); */
            
            CatchupMathMobile.__instance.showResourcePanel(item);
        }
        else {
            CatchupMathMobile.__instance.showLoginForm();
        }
    }
}
