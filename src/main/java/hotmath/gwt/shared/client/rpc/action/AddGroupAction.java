package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;

/** Add a new Group to an admin account
 * 
 * @author casey
 *
 */
 public class AddGroupAction implements Action<GroupInfoModel> {
 
	private static final long serialVersionUID = -8115618945074554354L;
	
	Integer adminId;
	GroupInfoModel group;
    
    public AddGroupAction() {
    }
    
    public  AddGroupAction(Integer adminId, GroupInfoModel gm) {
        this.adminId = adminId;
        this.group = gm;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public GroupInfoModel getGroup() {
        return group;
    }

    public void setGroup(GroupInfoModel group) {
        this.group = group;
    }
}
