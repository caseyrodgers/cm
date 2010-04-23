package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountsAction;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;

import java.sql.Connection;

/** Create Student accounts based on StudentModel passed in (as template)
 *  and list of name/passwords.
 *  
 *  Either create all ... or none of the accounts.
 **/
public class CreateAutoRegistrationAccountsCommand implements ActionHandler<CreateAutoRegistrationAccountsAction, AutoRegistrationSetup> {

    
    @Override
    public AutoRegistrationSetup execute(Connection conn, CreateAutoRegistrationAccountsAction action) throws Exception {
        
        StudentModelI studentTemplate = action.getStudentTemplate();
        AutoRegistrationSetup preview = new AutoRegistrationSetup();

        // first, create the unique group
        GroupInfoModel groupModel = new GroupInfoModel();
        groupModel.setGroupName(studentTemplate.getGroup());
        
        //groupModel = new CmAdminDao().addGroup(conn, action.getAdminId(),groupModel);
        //studentTemplate.setGroupId(groupModel.getId());
        
        
        CmStudentDao dao = new CmStudentDao();
        int errorCount=0;
        
        for(AutoRegistrationEntry entry: action.getAccountsToCreate()) {
            
            preview.getEntries().add(entry);
            /** Try each one, collecting error messages as you move along
             * 
             */
            try {
                StudentModelI studentToCreate = createStudentFromTemplate(entry, studentTemplate);
                dao.addStudent(conn, studentToCreate);
                entry.setMessage("Created successfully");
                entry.setIsError(false);
            }
            catch(Exception ex) {
                errorCount++;
                entry.setIsError(true);
                entry.setMessage(ex.getMessage());
            }
        }
        
        preview.setErrorCount(errorCount);
        
        return preview;
    }
    
    private StudentModelI createStudentFromTemplate(AutoRegistrationEntry entry, StudentModelI t) {
        StudentModel sm = new StudentModel();
        sm.setAdminUid(t.getAdminUid());
        sm.setName(entry.getName());
        sm.setPasscode(entry.getPassword());
        sm.setGroup(t.getGroup());
        sm.setChapter(t.getChapter());
        sm.setEmail(t.getEmail());
        sm.setGroupId(t.getGroupId());
        sm.setJson(t.getJson());
        sm.setLastLogin(t.getLastLogin());
        sm.setLastQuiz(t.getLastQuiz());
        sm.setPassPercent(t.getPassPercent());
        sm.setProgramChanged(t.getProgramChanged());
        
        sm.getProgram().setProgramType(t.getProgram().getProgramType());
        sm.getProgram().setProgramDescription(t.getProgram().getProgramDescription());
        sm.getProgram().setProgramId(t.getProgram().getProgramId());
        sm.getProgram().setSubjectId(t.getProgram().getSubjectId());
        
        sm.setSectionNum(t.getSectionNum());
        sm.getSettings().setShowWorkRequired(t.getSettings().getShowWorkRequired());
        sm.setShowWorkState(t.getShowWorkState());
        sm.setStatus(t.getStatus());
        
        sm.setTotalUsage(t.getTotalUsage());
        sm.getSettings().setTutoringAvailable(t.getSettings().getTutoringAvailable());
        sm.setTutoringState(t.getTutoringState());
        
        
        return sm;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateAutoRegistrationAccountsAction.class;
    }
}
