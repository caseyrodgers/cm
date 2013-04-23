package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;


public class SaveFeedbackAction implements Action<RpcData> {
    String comments;
    String commentsUrl;
    String stateInfo;
    
    public SaveFeedbackAction() {}
    
    public SaveFeedbackAction(String comments, String commentsUrl, String stateInfo){
        this.comments = comments;
        this.commentsUrl = commentsUrl;
        this.stateInfo = stateInfo;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public void setCommentsUrl(String commentsUrl) {
        this.commentsUrl = commentsUrl;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    @Override
    public String toString() {
        return "SaveFeedbackAction [comments=" + comments + ", commentsUrl=" + commentsUrl + ", stateInfo=" + stateInfo
                + "]";
    }
}
