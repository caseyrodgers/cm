package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationPreviewAction;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;

/** Create a preview set of Student Entries that could be 
 * saved 'right now'.  No guarantee if after delay a validation
 * error might occur.
 * 
 * 
 * @author casey
 *
 */
public class CreateAutoRegistrationPreviewCommand implements ActionHandler<CreateAutoRegistrationPreviewAction, AutoRegistrationSetup> {

    @Override
    public AutoRegistrationSetup execute(Connection conn, CreateAutoRegistrationPreviewAction action) throws Exception {
        
        StudentModel studentTemplate = action.getStudentTemplate();
        AutoRegistrationSetup preview = new AutoRegistrationSetup();
        
        /** first,  make sure the group name is not in use
         * 
         */
        GroupModel groupModel = new GroupModel();
        groupModel.setName(studentTemplate.getGroup());
        
        if(new CmAdminDao().checkForDuplicateGroup(conn, action.getAdminId(),groupModel) )
            throw new CmException("Group '" + studentTemplate.getGroup() + "' is already in use.");
        
        
        /** Create a series of Student records using student model as source for template values
         * 
         */
        for(int numCreated=0;numCreated < action.getNumToCreate();numCreated++) {
            preview.getEntries().add(createNewEntry((numCreated+1), action.getStudentTemplate()));
        }
        
        
        return preview;
    }
    
    
    private AutoRegistrationEntry createNewEntry(Integer key, StudentModel template) throws Exception {
        AutoRegistrationEntry entry = new AutoRegistrationEntry();
        entry.setName(template.getName() + "-" + key);
        entry.setPassword(template.getPasscode() + "-" + key);
        return entry;
    }

    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateAutoRegistrationPreviewAction.class;
    }
}
