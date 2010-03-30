package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.shared.client.rpc.Action;

public class GetProgramDefinitionsAction implements Action<CmList<StudyProgramModel>>{

	private static final long serialVersionUID = -4872888321062722916L;

	Integer adminId;

    public GetProgramDefinitionsAction() {}
    
    public GetProgramDefinitionsAction(Integer adminId) {
        this.adminId = adminId;
    }

    
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }    

    @Override
    public String toString() {
        return "GetProgramDefinitionsAction [adminId=" + adminId + "]";
    }
}

