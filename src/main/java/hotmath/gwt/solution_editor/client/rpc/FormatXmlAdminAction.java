package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.model.SolutionAdminResponse;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class FormatXmlAdminAction implements Action<SolutionAdminResponse> {

    public String getXml() {
        return xml;
    }
    public void setXml(String xml) {
        this.xml = xml;
    }
    String xml;

    public FormatXmlAdminAction(){}
    public FormatXmlAdminAction(String xml) {
        this.xml = xml;
    }
}
