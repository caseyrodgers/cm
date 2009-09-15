package hotmath.gwt.cm.client.history;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition;
import hotmath.gwt.cm.client.ui.context.PrescriptionContext;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.shared.client.util.UserInfo;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class CatchupMathHistoryListener implements ValueChangeHandler<String> {

    public void onValueChange(ValueChangeEvent<String> event) {
        final String historyToken = event.getValue();
        
        if(historyToken.length() >  0) {
            CmLocation location = new CmLocation(historyToken);
            
            // setup HistoryQueue to allow for asynchronous access
            CmHistoryQueue.getInstance().pushLocation(location);
        }
        
        
        // do default action
        if(UserInfo.getInstance().getRunId() > 0) {
            
            if(ContextController.getInstance().getTheContext() instanceof PrescriptionContext) {
                /** PrescriptionPage is currently in view, simply update its display
                 * 
                 */
                PrescriptionCmGuiDefinition.__instance.getAsyncDataFromServer(UserInfo.getInstance().getSessionNumber());
                
            }
            else {
                /** Load the PrescriptionContext 
                 * 
                 */
                CatchupMath.getThisInstance().showPrescriptionPanel_gwt();
            }
        }
        else {
            CatchupMath.getThisInstance().showQuizPanel_gwt();
        }

    }
}
          