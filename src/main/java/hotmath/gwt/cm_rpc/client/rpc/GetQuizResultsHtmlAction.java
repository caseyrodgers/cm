package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CmShared;

import com.google.gwt.user.client.rpc.AsyncCallback;


public class GetQuizResultsHtmlAction implements Action<RpcData>{
    
    int runId;
    
    public GetQuizResultsHtmlAction(){}
    
    public GetQuizResultsHtmlAction(int runId) {
        this.runId = runId; 
       
        getRunId();
    }

    public int getRunId() {
        
        if(runId == 0) {
            SaveFeedbackAction action = new SaveFeedbackAction();
            action.setStateInfo("GetQuizResultsHtmlAction: bug with runId=0");
            action.setComments(UserInfo.getInstance().toString());
            CmShared.getCmService().execute(action,new AsyncCallback<RpcData>() {
    
                @Override
                public void onFailure(Throwable caught) {
                    CmLogger.info(caught.getMessage());
                }
    
                @Override
                public void onSuccess(RpcData result) {
                    CmLogger.info("Feedback saved!");
                }
            });
        }
        
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

}
