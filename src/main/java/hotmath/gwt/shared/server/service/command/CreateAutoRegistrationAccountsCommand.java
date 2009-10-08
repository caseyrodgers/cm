package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountsAction;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;

/** Create Student accounts based on StudentModel passed in (as template)
 *  and list of name/passwords.
 *  
 *  Either create all ... or none of the accounts.
 **/
public class CreateAutoRegistrationAccountsCommand implements ActionHandler<CreateAutoRegistrationAccountsAction, AutoRegistrationSetup> {

    
    @Override
    public AutoRegistrationSetup execute(Connection conn, CreateAutoRegistrationAccountsAction action) throws Exception {
        
        StudentModel studentTemplate = action.getStudentTemplate();
        AutoRegistrationSetup preview = new AutoRegistrationSetup();

        // first, create the unique group
        GroupModel groupModel = new GroupModel();
        groupModel.setName(studentTemplate.getGroup());
        
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
                StudentModel studentToCreate = createStudentFromTemplate(entry, studentTemplate);
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
    
    private StudentModel createStudentFromTemplate(AutoRegistrationEntry entry, StudentModel t) {
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
        sm.setProgId(t.getProgId());
        sm.setProgramChanged(t.getProgramChanged());
        sm.setProgramDescr(t.getProgramDescr());
        sm.setSectionNum(t.getSectionNum());
        sm.setShowWorkRequired(t.getShowWorkRequired());
        sm.setShowWorkState(t.getShowWorkState());
        sm.setStatus(t.getStatus());
        sm.setSubjId(t.getSubjId());
        sm.setTotalUsage(t.getTotalUsage());
        sm.setTutoringAvail(t.getTutoringAvail());
        sm.setTutoringState(t.getTutoringState());
        sm.setUserProgramId(t.getUserProgramId());
        
        return sm;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateAutoRegistrationAccountsAction.class;
    }
}
