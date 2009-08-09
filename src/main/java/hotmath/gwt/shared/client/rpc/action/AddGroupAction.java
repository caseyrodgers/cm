package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.CmRpcException;



/** Add a new Group to an admin account
 * 
 * @author casey
 *
 */
 public class AddGroupAction implements Action<GroupModel> {
 
    Integer adminId;
    GroupModel group;
    
    public AddGroupAction() {
    }
    
    
    public  AddGroupAction(Integer adminId, GroupModel gm) throws CmRpcException {
        this.adminId = adminId;
        this.group = gm;
    }
    
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }


    public GroupModel getGroup() {
        return group;
    }


    public void setGroup(GroupModel group) {
        this.group = group;
    }
}
