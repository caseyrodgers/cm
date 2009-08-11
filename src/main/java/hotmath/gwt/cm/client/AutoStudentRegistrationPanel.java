package hotmath.gwt.cm.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;


public class AutoStudentRegistrationPanel extends ContentPanel {

    FormPanel _formPanel;
    FieldSet _fsProfile;
    TextField<String> userName;
    TextField<String> password;
    TextField<String> email;
    TextField<String> passwordVerify;
    
    public AutoStudentRegistrationPanel() {
        
        
        _formPanel = new FormPanel();
        _formPanel.setStyleName("register-student-form-panel");
        _formPanel.setLabelWidth(100);
        _formPanel.setHeight(500);
        _formPanel.setWidth(380);
        _formPanel.setFooter(true);
        _formPanel.setFrame(false);
        _formPanel.setHeaderVisible(false);
        _formPanel.setBodyBorder(false);
        _formPanel.setIconStyle("icon-form");
        _formPanel.setButtonAlign(HorizontalAlignment.CENTER);
        _formPanel.setLayout(new FormLayout());
        
        _fsProfile = new FieldSet();
        FormLayout fL = new FormLayout();
        fL.setLabelWidth(_formPanel.getLabelWidth());
        fL.setDefaultWidth(175);
        _fsProfile.setLayout(fL);
        
        _fsProfile.setHeading("Create your own password");
        userName = new TextField<String>();  
        userName.setFieldLabel("Name");
        userName.setAllowBlank(false);
        userName.setId("name");
        userName.setEmptyText("-- enter name --");
        _fsProfile.add(userName);
        
        password = new TextField<String>();  
        password.setFieldLabel("Password");
        password.setAllowBlank(false);
        password.setId("password");
        password.setEmptyText("-- enter password --");
        _fsProfile.add(password);
        
        passwordVerify = new TextField<String>();  
        passwordVerify.setFieldLabel("Verify password");
        passwordVerify.setAllowBlank(false);
        passwordVerify.setId("passwordVerify");
        passwordVerify.setEmptyText("-- verify password --");
        _fsProfile.add(passwordVerify);
        
        
        email = new TextField<String>();  
        email.setFieldLabel("Email");
        email.setAllowBlank(false);
        email.setId("email");
        email.setEmptyText("-- enter email (optional) --");
        _fsProfile.add(email);
        
        _formPanel.add(_fsProfile);
        
        setStyleAttribute("text-align", "center");
        setStyleAttribute("background", "transparent");
        add(_formPanel);
    }
        
}
