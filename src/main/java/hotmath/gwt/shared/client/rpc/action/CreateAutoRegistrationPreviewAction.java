package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;


/** Create a preview set of Student Entires that could be 
 * saved 'right now'.  No guarantee if after delay a validation
 * error might occur.
 * 
 * 
 * @author casey
 *
 */
public class CreateAutoRegistrationPreviewAction implements Action<AutoRegistrationSetup> {
    StudentModel studentTemplate;
    String uploadKey;

    public CreateAutoRegistrationPreviewAction() {
    }
    
    
    public CreateAutoRegistrationPreviewAction(StudentModel studentTemplate, String uploadKey) {
        this.studentTemplate = studentTemplate;
        this.uploadKey = uploadKey;
    }


    public StudentModel getStudentTemplate() {
        return studentTemplate;
    }


    public void setStudentTemplate(StudentModel studentTemplate) {
        this.studentTemplate = studentTemplate;
    }


    public String getUploadKey() {
        return uploadKey;
    }


    public void setUploadKey(String uploadKey) {
        this.uploadKey = uploadKey;
    }
}
