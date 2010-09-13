package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActionBase;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;

import java.util.List;



/** Create Student accounts based on StudentModel passed in (as template)
 *  and list of name/passwords.
 *  
 *  Either create all ... or none of the accounts.
 **/
 public class CreateAutoRegistrationAccountsAction extends ActionBase implements Action<AutoRegistrationSetup> {
 
    Integer adminId;
    StudentModelI studentTemplate;
    List<AutoRegistrationEntry> accountsToCreate;
    
    public CreateAutoRegistrationAccountsAction() {
        getClientInfo().setUserType(UserType.ADMIN);
    }
    
    public CreateAutoRegistrationAccountsAction(Integer adminId, StudentModelI studentTemplate, List<AutoRegistrationEntry> accountsToCreate) {
        this.adminId = adminId;
        this.studentTemplate = studentTemplate;
        this.accountsToCreate = accountsToCreate;
        
        getClientInfo().setUserId(adminId);
        getClientInfo().setUserType(UserType.ADMIN);
    }
    
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }


    public StudentModelI getStudentTemplate() {
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
