package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class SolutionAdminResponse implements Response {
    String xml;
    String pid;
    
    public SolutionAdminResponse() {}
    public SolutionAdminResponse(String xml) {
        this.xml = xml;
    }
    public String getXml() {
        return xml;
    }
    public void setXml(String xml) {
        this.xml = xml;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    
}
