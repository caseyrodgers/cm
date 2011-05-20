package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.SaveAutoRegistrationAction;
import hotmath.gwt.shared.client.util.CmUserException;

import java.sql.Connection;
import java.util.List;

/** Save Auto Account Creation setup for this Admin/Group
 * 
 *  Information will be saved in ..HA_USER.is_auto_create_template.. to allow identification
 *  as a 'AutoCreationSetup' (ACS).  An ACS sets up a special
 *  group that allows auto creating student accounts when they enter
 *  the group name as the password along with the school's passcode.
 *  
 *  This will trigger a routine to allow the student to create their own 
 *  account/password.    The account is created using the supplied values
 *  from the admin user.
 *  
 *  
 *  To implement this, for the first pass we will create a special template Student 
 *  
 *  
 * @author casey
 *
 */
public class SaveAutoRegistrationCommand implements ActionHandler<SaveAutoRegistrationAction, RpcData> {

    @Override
    public RpcData execute(Connection conn, SaveAutoRegistrationAction action) throws Exception {
    
        StudentModelI student = action.getStudent();
        String groupName = student.getGroup();
        GroupInfoModel groupModel=null;
        
        // Group name cannot be the same as a student password for the current Admin
        student.setPasscode(groupName.trim());
        Boolean isPasscodeTaken = CmStudentDao.getInstance().checkForDuplicatePasscode(conn, student);
        if (isPasscodeTaken == true) {
            throw new CmUserException("Group name cannot be the same as the password of an existing student.");
        }
        
        // first make sure this group exists
        // If the group already exists then use it, but first
        // remove any existing auto_create_template account based on this group.
        List<GroupInfoModel> groups = CmAdminDao.getInstance().getActiveGroups(student.getAdminUid());
        for(GroupInfoModel gm: groups) {
            String gname = gm.getName();  // contains null for default (NONE) user 
            if(gname != null && gname.equals(groupName)) {
                groupModel = gm;

                CmAdminDao.getInstance().removeAutoRegistrationSetupFor(conn, student.getAdminUid(), groupName);
                
                break;
            }
        }
        if(groupModel == null) {
            groupModel = new GroupInfoModel();
            groupModel.setGroupName(student.getGroup());
            groupModel = CmAdminDao.getInstance().addGroup(conn, action.getAdminId(),groupModel);
        }
        
        student.setGroupId(groupModel.getId().toString());
        
        student.setPasscode(student.getGroup() + "_" + System.currentTimeMillis());  // make unique
        
        StudentModelI sm = CmStudentDao.getInstance().addStudent(conn, student);
        
        
        CmAdminDao.getInstance().markAccountAsAutoRegistrationSetup(conn, sm.getUid());
        
        RpcData rdata = new RpcData("status=OK");
        return rdata;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveAutoRegistrationAction.class;
    }
}
