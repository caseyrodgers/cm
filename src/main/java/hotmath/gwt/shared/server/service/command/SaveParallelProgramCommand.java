package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.CmUserException;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.SaveParallelProgramAction;

import java.sql.Connection;
import java.util.List;

/** Save Parallel Program setup for this Admin / Group
 * 
 *  Information will be saved in HA_USER_TEMPLATE
 *  The student will login using the usual username (school passcode) and the password,
 *  which is the Parallel Program Group Name.
 *  
 * @author bob
 *
 */
public class SaveParallelProgramCommand implements ActionHandler<SaveParallelProgramAction, RpcData> {

    @Override
    public RpcData execute(Connection conn, SaveParallelProgramAction action) throws Exception {
    
        StudentModelI student = action.getStudent();
        String groupName = student.getGroup();
        GroupInfoModel groupModel = null;
        
        // Group name cannot be the same as a student password for the current Admin
        student.setPasscode(groupName.trim());
        Boolean isPasscodeTaken = CmStudentDao.getInstance().checkForDuplicatePasscode(conn, student);
        if (isPasscodeTaken == true) {
            throw new CmUserException("Group name cannot be the same as the password of a student, the name of a Self Registration Group, or the name of a Parallel Program Group.");
        }
        
        // Parallel Program Groups must be unique for the current Admin 
        // (TODO may relax this to must be unique if not active)
        List<GroupInfoModel> groups = CmAdminDao.getInstance().getActiveGroups(student.getAdminUid());
        for(GroupInfoModel gm: groups) {
            String gname = gm.getName();  // contains null for default (NONE) user 
            if(gname != null && gname.equals(groupName)) {

                boolean isParallelProgramGroup = ParallelProgramDao.getInstance().isParallelProgramGroup(student.getAdminUid(), groupName);

                if (isParallelProgramGroup == true) {
                    throw new CmUserException("Parallel Program Group names must be unique.");
                }

            	groupModel = gm;
                
                break;
            }
        }
        if(groupModel == null) {
            groupModel = new GroupInfoModel();
            groupModel.setGroupName(student.getGroup());
            groupModel = CmAdminDao.getInstance().addGroup(conn, action.getAdminId(), groupModel);
        }
        
        student.setGroupId(groupModel.getId().toString());
        
        student.setPasscode(student.getGroup() + "_" + System.currentTimeMillis());  // make unique
        
        if (student.getSectionNum() == null) student.setSectionNum(0);
        
        CmStudentDao.getInstance().addStudentTemplate(student, "parallel-program");
        
        RpcData rdata = new RpcData("status=OK");
        return rdata;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveParallelProgramAction.class;
    }
}
