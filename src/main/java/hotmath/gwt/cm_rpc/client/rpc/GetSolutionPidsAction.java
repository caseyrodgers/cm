package hotmath.gwt.cm_rpc.client.rpc;

public class GetSolutionPidsAction implements Action<CmList<String>> {
    
    private String pids;
    public GetSolutionPidsAction(){}
    public GetSolutionPidsAction(String pids) {
        this.pids = pids;
    }
    public String getPids() {
        return pids;
    }
    public void setPids(String pids) {
        this.pids = pids;
    }
}
