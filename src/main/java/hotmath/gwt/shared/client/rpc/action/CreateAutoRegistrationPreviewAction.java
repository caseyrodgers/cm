package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
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
public class CreateAutoRegistrationPreviewAction implements Action<AutoRegistrationSetup> {
    StudentModelI studentTemplate;
    String uploadKey;

    public CreateAutoRegistrationPreviewAction() {
    }
    
    
    public CreateAutoRegistrationPreviewAction(StudentModelI studentTemplate, String uploadKey) {
        this.studentTemplate = studentTemplate;
        this.uploadKey = uploadKey;
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
