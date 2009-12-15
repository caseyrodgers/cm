package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.action.CmList;

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
