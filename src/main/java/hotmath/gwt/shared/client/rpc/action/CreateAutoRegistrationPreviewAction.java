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
    Integer adminId;
    StudentModel studentTemplate;
    Integer numToCreate;

    public CreateAutoRegistrationPreviewAction() {
    }
    
    
    public CreateAutoRegistrationPreviewAction(Integer adminId, StudentModel studentTemplate, Integer numToCreate) {
        this.adminId = adminId;
        this.studentTemplate = studentTemplate;
        this.numToCreate = numToCreate;
    }
    
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }


    public StudentModel getStudentTemplate() {
        return studentTemplate;
    }


    public void setStudentTemplate(StudentModel studentTemplate) {
        this.studentTemplate = studentTemplate;
    }


    public Integer getNumToCreate() {
        return numToCreate;
    }


    public void setNumToCreate(Integer numToCreate) {
        this.numToCreate = numToCreate;
    }
}
