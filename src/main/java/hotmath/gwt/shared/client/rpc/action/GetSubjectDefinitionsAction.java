package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.SubjectModel;

public class GetSubjectDefinitionsAction implements Action<CmList<SubjectModel>>{
    
    String progId;
    
    public GetSubjectDefinitionsAction() {}
    public GetSubjectDefinitionsAction(String progId) {
        this.progId = progId;
    }
    public String getProgId() {
        return progId;
    }
    public void setProgId(String progId) {
        this.progId = progId;
    }
}
