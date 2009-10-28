package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

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

    public GroupManagerAction(){}

    public GroupManagerAction(ActionType actionType,Integer adminId) {
        this.adminId = adminId;
        this.actionType = actionType;        
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


    public enum ActionType{DELETE,CREATE,UNREGISTER_STUDENTS,UPDATE,GROUP_PROGRAM_ASSIGNMENT};    
}
