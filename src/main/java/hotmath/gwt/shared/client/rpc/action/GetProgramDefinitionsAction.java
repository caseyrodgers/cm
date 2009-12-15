package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.shared.client.rpc.Action;

public class GetProgramDefinitionsAction implements Action<CmList<StudyProgramModel>>{

	private static final long serialVersionUID = -4872888321062722916L;

	String progId;
    
    public GetProgramDefinitionsAction() {}
    
    public GetProgramDefinitionsAction(String progId) {
        this.progId = progId;
    }

    public String getProgId() {
        return progId;
    }

    public void setProgId(String progId) {
        this.progId = progId;
    }

    @Override
    public String toString() {
        return "GetProgramDefinitionsAction [progId=" + progId + "]";
    }
}

