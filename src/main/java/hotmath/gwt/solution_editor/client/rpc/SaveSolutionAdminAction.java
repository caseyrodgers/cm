package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

public class SaveSolutionAdminAction implements Action<RpcData>{
    String pid,xml;
    public SaveSolutionAdminAction(){}
    public SaveSolutionAdminAction(String pid, String xml) {
        this.pid = pid;
        this.xml = xml;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public String getXml() {
        return xml;
    }
    public void setXml(String xml) {
        this.xml = xml;
    }
}
