package hotmath.gwt.cm_mobile2.client;

import hotmath.gwt.cm_mobile2.client.CatchupMathMobile2.Callback;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class CatchupMathMobileHistoryListener implements ValueChangeHandler<String> {
    public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue();

        final TokenParser token = new TokenParser(historyToken);

        final String type = token.getType();
        if (type == null || type.equals("list")) {
            Controller.navigateToTopicList();
        } else if (type.equals("lesson")) {
            String file = token.getLesson();
            Controller.navigateToTopicView(file);
        } else if (type.equals("res_object")) {
            // /** parse history
            // * TODO: abstract history token
            // *
            // */
             String file = token.getLesson();
            
             InmhItemData item = new InmhItemData("review", file,"");
             Controller.navigateToPrescriptionResource(null, item, -1);
        } else {
            /**
             * find the item referenced via historyToken
             * 
             */
            CatchupMathMobile2.loadLessonData(token.getLesson(), new Callback() {
                
                @Override
                public void isComplete(Object data) {
                    InmhItemData item = null;
                    for (PrescriptionSessionDataResource r : CatchupMathMobileShared.getUser().getPrescripion()
                            .getCurrSession().getInmhResources()) {
                        if (r.getType().equals(type)) {
                            item = r.getItems().get(token.getOrdinal());
                            break;
                        }
                    }
                    /** assert(item != null); */
                    Controller.navigateToPrescriptionResource(null, item, token.getOrdinal());
                }
            });
        }
    }
}