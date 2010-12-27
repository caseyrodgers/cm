package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;

public class GetSolutionAdminAction implements Action<SolutionAdminResponse> {
    String pid;
    Type type=Type.GET;
    String solutionXml;
    
    public String getSolutionXml() {
        return solutionXml;
    }
    public void setSolutionXml(String solutionXml) {
        this.solutionXml = solutionXml;
    }
    public GetSolutionAdminAction(){}
    public GetSolutionAdminAction(Type type, String pid) {
        this.type = type;
        this.pid = pid;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    static public enum Type {GET,FORMAT,CREATE};
}
