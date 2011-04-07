package hotmath.gwt.cm_rpc.client.rpc;

public class GetCmProgramFlowAction implements Action<CmProgramFlowAction>{
    
    int userId;
    
    public enum FlowType {ACTIVE,NEXT,RETAKE_SEGMENT};
    
    FlowType flowType;
    
    public GetCmProgramFlowAction() {}
    
    public GetCmProgramFlowAction(int userId,FlowType flowType) {
        this.userId = userId;
        this.flowType = flowType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public FlowType getFlowType() {
        return flowType;
    }

    public void setFlowType(FlowType flowType) {
        this.flowType = flowType;
    }

    @Override
    public String toString() {
        return "GetCmProgramFlowAction [userId=" + userId + ", flowType=" + flowType + "]";
    }
}
