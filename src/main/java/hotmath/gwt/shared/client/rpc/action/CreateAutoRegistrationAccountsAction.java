package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;

import java.util.List;



/** Create Student accounts based on StudentModel passed in (as template)
 *  and list of name/passwords.
 *  
 *  Either create all ... or none of the accounts.
 **/
 public class CreateAutoRegistrationAccountsAction implements Action<AutoRegistrationSetup> {
 
    Integer adminId;
    StudentModel studentTemplate;
    List<AutoRegistrationEntry> accountsToCreate;
    
    public CreateAutoRegistrationAccountsAction() {
    }
    
    
    public CreateAutoRegistrationAccountsAction(Integer adminId, StudentModel studentTemplate, List<AutoRegistrationEntry> accountsToCreate) {
        this.adminId = adminId;
        this.studentTemplate = studentTemplate;
        this.accountsToCreate = accountsToCreate;
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


    public List<AutoRegistrationEntry> getAccountsToCreate() {
        return accountsToCreate;
    }


    public void setAccountsToCreate(List<AutoRegistrationEntry> accountsToCreate) {
        this.accountsToCreate = accountsToCreate;
    }
}
