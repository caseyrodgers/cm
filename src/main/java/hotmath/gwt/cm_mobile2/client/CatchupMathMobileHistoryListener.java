package hotmath.gwt.cm_mobile2.client;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class CatchupMathMobileHistoryListener implements ValueChangeHandler<String> {
    public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue();
        
        if(historyToken.startsWith("topic")) {
            String file = historyToken.split(":")[1];
            Controller.navigateToTopicView(file);
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
        else if(historyToken.startsWith("res_object")) {
            /** parse history 
             * TODO: abstract history token
             * 
             */
            String p[] = historyToken.split(":");
            String type = p[0];
            String file =  p[2];

            InmhItemData item = new InmhItemData("review", file,"");
            /** assert(item != null); */
            Controller.navigateToPrescriptionResource(null, item, -1);            
        }                
        else {
            Controller.navigateToTopicList();
        }
    }
}
