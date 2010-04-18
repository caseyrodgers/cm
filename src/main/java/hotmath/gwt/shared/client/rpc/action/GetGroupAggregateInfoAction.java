package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;

/** Return list of groups and the count of students in each
 * 
 * @author casey
 *
 */
public class GetGroupAggregateInfoAction implements Action<CmList<GroupInfoModel>>{
    
    Integer adminId;
    public GetGroupAggregateInfoAction(){}
    
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public GetGroupAggregateInfoAction(Integer adminId) {
        this.adminId = adminId;
    }

}
