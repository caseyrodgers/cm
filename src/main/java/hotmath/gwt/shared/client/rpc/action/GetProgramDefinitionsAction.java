package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;

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

