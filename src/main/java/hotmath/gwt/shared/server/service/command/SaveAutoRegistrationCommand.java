package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.SaveAutoRegistrationAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;

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
 *  To implement this, for the first pass we will create a special Student 
 *  named '
 *  
 *  
 * @author casey
 *
 */
public class SaveAutoRegistrationCommand implements ActionHandler<SaveAutoRegistrationAction, RpcData> {


    CmAdminDao daoa = new CmAdminDao();
    CmStudentDao dao = new CmStudentDao();
    
    @Override
    public RpcData execute(Connection conn, SaveAutoRegistrationAction action) throws Exception {
    
        StudentModel student = action.getStudent();
        String groupName = student.getGroup();
        GroupModel groupModel=null;
        
        // first make sure this group exists
        // If the group already exists the use it, but first
        // remove any existing auto_create_template account based on this group.
        List<GroupModel> groups = daoa.getActiveGroups(conn, student.getAdminUid());
        for(GroupModel gm: groups) {
            String gname = gm.getName();  // contains null for default (NONE) user 
            if(gname != null && gname.equals(groupName)) {
                groupModel = gm;

                daoa.removeAutoRegistrationSetupFor(conn, student.getAdminUid(), groupName);
                
                break;
            }
        }
        if(groupModel == null) {
            groupModel = new GroupModel();
            groupModel.setName(student.getGroup());
            groupModel = daoa.addGroup(conn, action.getAdminId(),groupModel);
        }
        
        student.setGroupId(groupModel.getId());
        
        student.setPasscode(student.getGroup() + "_" + System.currentTimeMillis());  // make unique
        
        StudentModel sm = dao.addStudent(conn, student);
        
        
        daoa.markAccountAsAutoRegistrationSetup(conn, sm.getUid());
        
        RpcData rdata = new RpcData("status=OK");
        return rdata;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveAutoRegistrationAction.class;
    }
}
