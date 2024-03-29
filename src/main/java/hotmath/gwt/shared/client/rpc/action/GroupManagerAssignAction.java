package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.model.GroupInfoModel;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import java.util.List;

/** Perform an operation on an admin's group
 * 
 * 
 * @author casey
 *
 */
public class GroupManagerAssignAction implements Action<GroupManagerAssignResponse>{
    
    GroupInfoModel group;
    CmList<StudentModelI> groupStudents;
    ActionType type;

	public GroupManagerAssignAction(){}

    public GroupManagerAssignAction(ActionType type, GroupInfoModel gmi) {
    	this.type = type;
        this.group = gmi;        
    }

	public GroupInfoModel getGroup() {
		return group;
	}

	public void setGroup(GroupInfoModel group) {
		this.group = group;
	}

	public List<StudentModelI> getGroupStudents() {
		return groupStudents;
	}

	public void setGroupStudents(CmList<StudentModelI> groupStudents) {
		this.groupStudents = groupStudents;
	}
	public ActionType getType() {
		return type;
	}

	public void setType(ActionType type) {
		this.type = type;
	}
	public static enum ActionType{GET_STUDENTS,SAVE_STUDENTS};
}
