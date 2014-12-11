package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
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
    Boolean disableCalcAlways;
    Boolean disableCalcQuizzes;
    boolean noPublicWebLinks;
    Integer passPercent;
    Integer isSelfReg;
    private boolean disableSearch;

	public GroupManagerAction(){}

    public GroupManagerAction(ActionType actionType,Integer adminId) {
        this.adminId = adminId;
        this.actionType = actionType;        
    }

    public boolean isNoPublicWebLinks() {
        return noPublicWebLinks;
    }

    public void setNoPublicWebLinks(boolean noPublicWebLinks) {
        this.noPublicWebLinks = noPublicWebLinks;
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

	public Boolean getDisableCalcAlways() {
		return disableCalcAlways;
	}

	public void setDisableCalcAlways(Boolean disableCalcAlways) {
		this.disableCalcAlways = disableCalcAlways;
	}

	public Boolean getDisableCalcQuizzes() {
		return disableCalcQuizzes;
	}

	public void setDisableCalcQuizzes(Boolean disableCalcQuizzes) {
		this.disableCalcQuizzes = disableCalcQuizzes;
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


    public enum ActionType{DELETE,CREATE,UNREGISTER_STUDENTS,UPDATE,GROUP_PROGRAM_ASSIGNMENT,GROUP_PROPERTY_SET}


    public void setDisableSearch(boolean disableSearch) {
        this.disableSearch = disableSearch;
    }

    public boolean isDisableSearch() {
        return disableSearch;
    };    
    
}
