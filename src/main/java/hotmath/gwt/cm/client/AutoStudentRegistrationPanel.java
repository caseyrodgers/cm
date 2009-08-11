package hotmath.gwt.cm.client;

import java.util.List;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.ResourceContainer;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountAction;
import hotmath.gwt.shared.client.util.CmInfoConfig;
import hotmath.gwt.shared.client.util.UserInfo;
import hotmath.gwt.shared.client.util.ValdationTypeValidator;
import hotmath.gwt.shared.client.util.ValidationType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class AutoStudentRegistrationPanel extends ResourceContainer {

    FormPanel _formPanel;
    
    TextField<String> userName;
    TextField<String> password;
    TextField<String> email;
    TextField<String> passwordVerify;
    
    public AutoStudentRegistrationPanel() {
        
        _formPanel = new FormPanel();
        _formPanel.setStyleName("register-student-form-panel");
        _formPanel.setLabelWidth(100);
        _formPanel.setHeight(300);
        _formPanel.setWidth(380);
        _formPanel.setFooter(true);
        _formPanel.setFrame(true);
        _formPanel.setHeaderVisible(true);
        _formPanel.setBodyBorder(false);
        _formPanel.setIconStyle("icon-form");
        _formPanel.setButtonAlign(HorizontalAlignment.CENTER);
        _formPanel.setLayout(new FormLayout());
        
        _formPanel.setHeading("Create your own personal login");
        
        FieldSet fsProfile = new FieldSet();
        FormLayout fL = new FormLayout();
        fL.setLabelWidth(_formPanel.getLabelWidth());
        fL.setDefaultWidth(175);
        fsProfile.setLayout(fL);
        
        fsProfile.setHeading("Name and password are required");
        userName = new TextField<String>();  
        userName.setFieldLabel("Name");
        userName.setAllowBlank(false);
        userName.setId("name");
        userName.setEmptyText("-- enter name --");
        fsProfile.add(userName);
        
        _formPanel.add(fsProfile);
        
        password = new TextField<String>();  
        password.setFieldLabel("Password");
        password.setAllowBlank(false);
        password.setId("password");
        password.setEmptyText("-- enter password --");
        fsProfile.add(password);
        
        passwordVerify = new TextField<String>();  
        passwordVerify.setFieldLabel("Verify password");
        passwordVerify.setAllowBlank(false);
        passwordVerify.setId("passwordVerify");
        passwordVerify.setEmptyText("-- verify password --");
        passwordVerify.setValidator(new Validator() {
            
            @Override
            public String validate(Field<?> field, String value) {
                if(value == null || value.length() == 0)
                    return "The verify password field must be specified";
                else if(!value.equals(password.getValue())) {
                    return "This field must match the password field";
                }
                
                return null;
            }
        });
        
        fsProfile.add(passwordVerify);

        
        email = new TextField<String>();  
        email.setFieldLabel("Email");
        email.setAllowBlank(true);
        email.setValidator(new ValdationTypeValidator(ValidationType.EMAIL));
        
        email.setId("email");
        email.setEmptyText("-- enter email (optional) --");
        fsProfile.add(email);
        
        _formPanel.add(fsProfile);
        
        _formPanel.add(new Html("<p>If you have forgotten your password just create a new one.</p>"));
        
        
        Button saveButton = new Button("Create Password");
        
        
        saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                doCreatePassword();
            }
        });
        
        _formPanel.addButton(saveButton);
        FormButtonBinding binding = new FormButtonBinding(_formPanel);  

        setStyleAttribute("text-align", "center");
        _formPanel.setStyleAttribute("margin-top", "20px");
        add(_formPanel);
        
        
        ContextController.getInstance().setCurrentContext(new CmContext() {
            
            @Override
            public void runAutoTest() {
            }
            
            @Override
            public void resetContext() {
            }
            
            @Override
            public List<Component> getTools() {
                return null;
            }
            
            @Override
            public String getStatusMessage() {
                return "Create your own personal Catchup Math password.";
            }
            
            @Override
            public String getContextTitle() {
                // TODO Auto-generated method stub
                return "Create Auto Registration Account";
            }
            
            @Override
            public String getContextSubTitle() {
                return "";
            }
            
            @Override
            public String getContextHelp() {
                return getStatusMessage();
            }
            
            @Override
            public int getContextCompletionPercent() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public void doPrevious() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void doNext() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    private void doCreatePassword() {

        
        if(!_formPanel.isValid()) {
            InfoPopupBox.display(new CmInfoConfig("Validation problems", "Please correct any problems on the form."));
            return;
        }

        
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new CreateAutoRegistrationAccountAction(UserInfo.getInstance().getUid(), userName.getValue(), password.getValue()), new AsyncCallback<UserInfo>() {
            @Override
            public void onSuccess(final UserInfo result) {
                
                
                final CmWindow win = new CmWindow();
                win.setSize(320,200);
                win.setModal(true);
                win.setClosable(false);
                String html = "<div style='margin: 10px;'>" +
                              "<p>You have succesfully created the password: <br/><b>" + password.getValue() + "</b>.</p>" +
                              "<p style='margin-top: 15px'>You can use this password along with your school's passcode to log back into your account at any time.</p>" +
                              "</div>";
                
                win.add(new Html(html));
                
                win.setHeading("Password Created Successsfully!");
                
                Button close = new Button("Begin Catchup Math");
                close.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        UserInfo.setInstance(result);
                        UserInfo.getInstance().setActiveUser(true);
                        EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_USERCHANGED, result));
                        CatchupMath.getThisInstance().showQuizPanel();
                        
                        
                        win.close();
                    }
                });
                
                win.getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
                win.addButton(close);
                
                win.setVisible(true);
            }

            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                String msg = caught.getMessage();
                if(msg.indexOf("already in use") > -1)
                    msg = "The password '" + password.getValue() + "' is already in use.";
                
                CatchupMathTools.showAlert("Problem occurred while creating account: " + caught.getMessage());
            }
        });        
    }
 
}
