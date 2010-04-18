package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.model.StudentModel;

/** Perform an operation on an admin's group
 * 
 * 
 * @author casey
 *
 */
public class GroupManagerAction implements Action<RpcData>{
    
    Integer adminId;
    ActionType actionType;
    Integer groupId;
    String groupName;
    StudentModel studentModel;
    Boolean disallowTutoring;
    Boolean showWorkRequired;
    Boolean limitGames;
    Boolean stopAtProgramEnd;
    Integer passPercent;
    Integer isSelfReg;

	public GroupManagerAction(){}

    public GroupManagerAction(ActionType actionType,Integer adminId) {
        this.adminId = adminId;
        this.actionType = actionType;        
    }

    public Boolean getDisallowTutoring() {
        return disallowTutoring;
    }

    public void setDisallowTutoring(Boolean disallowTutoring) {
        this.disallowTutoring = disallowTutoring;
    }

    public Boolean getShowWorkRequired() {
        return showWorkRequired;
    }

    public void setShowWorkRequired(Boolean showWorkRequired) {
        this.showWorkRequired = showWorkRequired;
    }

    public Boolean getLimitGames() {
		return limitGames;
	}

	public void setLimitGames(Boolean limitGames) {
		this.limitGames = limitGames;
	}

	public Boolean getStopAtProgramEnd() {
		return stopAtProgramEnd;
	}

	public void setStopAtProgramEnd(Boolean stopAtProgramEnd) {
		this.stopAtProgramEnd = stopAtProgramEnd;
	}

	public Integer getPassPercent() {
        return passPercent;
    }

    public void setPassPercent(Integer passPercent) {
        this.passPercent = passPercent;
    }

    public StudentModel getStudentModel() {
        return studentModel;
    }

    public void setStudentModel(StudentModel studentModel) {
        this.studentModel = studentModel;
    }
    
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getAdminId() {
        return adminId;
    }


    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
    

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getIsSelfReg() {
		return isSelfReg;
	}

	public void setIsSelfReg(Integer isSelfReg) {
		this.isSelfReg = isSelfReg;
	}


    public enum ActionType{DELETE,CREATE,UNREGISTER_STUDENTS,UPDATE,GROUP_PROGRAM_ASSIGNMENT,GROUP_PROPERTY_SET};    
}
