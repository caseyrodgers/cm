package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActionBase;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;


/** Create a preview set of Student Entries that could be 
 * saved 'right now'.  No guarantee if after delay a validation
 * error might occur.
 * 
 * 
 * @author casey
 *
 */
public class CreateAutoRegistrationPreviewAction extends ActionBase implements Action<AutoRegistrationSetup> {
    StudentModelI studentTemplate;
    String uploadKey;

    public CreateAutoRegistrationPreviewAction() {
    }
    
    public CreateAutoRegistrationPreviewAction(StudentModelI studentTemplate, String uploadKey) {
        this.studentTemplate = studentTemplate;
        this.uploadKey = uploadKey;
        
        int userId = (studentTemplate.getAdminUid() != null) ? studentTemplate.getAdminUid() : 0;
        getClientInfo().setUserId(userId);
        getClientInfo().setUserType(UserType.ADMIN);
    }


    public StudentModelI getStudentTemplate() {
        return studentTemplate;
    }


    public void setStudentTemplate(StudentModelI studentTemplate) {
        this.studentTemplate = studentTemplate;
    }


    public String getUploadKey() {
        return uploadKey;
    }


    public void setUploadKey(String uploadKey) {
        this.uploadKey = uploadKey;
    }
}
