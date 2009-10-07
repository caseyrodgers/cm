package hotmath.gwt.cm.client;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.ResourceContainer;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountAction;
import hotmath.gwt.shared.client.util.CmInfoConfig;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.List;

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
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


/** Provides Self Registration login screen and validation
 * 
 * @author casey
 *
 */
public class AutoStudentRegistrationPanel extends ResourceContainer {

    FormPanel _formPanel;
    
    TextField<String> firstName;
    TextField<String> lastName;
    TextField<String> birthDate;
    
    public AutoStudentRegistrationPanel() {
        
        _formPanel = new FormPanel();
        _formPanel.setStyleName("register-student-form-panel");
        _formPanel.setLabelWidth(100);
        //_formPanel.setHeight(250);
        _formPanel.setWidth(395);
        _formPanel.setFooter(true);
        _formPanel.setFrame(true);
        _formPanel.setHeaderVisible(true);
        _formPanel.setBodyBorder(false);
        _formPanel.setIconStyle("icon-form");
        _formPanel.setButtonAlign(HorizontalAlignment.CENTER);
        _formPanel.setLayout(new FormLayout());
        
        _formPanel.setHeading("Self Registration");
        
        FieldSet fsAlready = new FieldSet();
        fsAlready.setHeading("Already Registered");
        Button alreadyRegistered = new Button("Forgot Password?");
        alreadyRegistered.setToolTip("Click here for information about your existing password");
        alreadyRegistered.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                showForgotPassword();
            }
        });
        fsAlready.add(alreadyRegistered);
        _formPanel.add(fsAlready);
        
        FieldSet fsProfile = new FieldSet();
        FormLayout fL = new FormLayout();
        fL.setLabelWidth(_formPanel.getLabelWidth());
        fL.setDefaultWidth(210);
        fsProfile.setLayout(fL);
        
        fsProfile.setHeading("Register");
        
        firstName = new TextField<String>();  
        firstName.setFieldLabel("Your first name");
        firstName.setAllowBlank(false);
        firstName.setValidator(new MyFieldValidator());
        firstName.setId("firstName");
        firstName.setEmptyText("-- enter first name --");
        fsProfile.add(firstName);
        
        lastName = new TextField<String>();  
        lastName.setFieldLabel("Your last name");
        lastName.setAllowBlank(false);
        lastName.setValidator(new MyFieldValidator());
        lastName.setId("lastName");
        lastName.setEmptyText("-- enter last name --");
        fsProfile.add(lastName);
        
        birthDate = new TextField<String>();  
        birthDate.setFieldLabel("Your birth day");
        birthDate.setAllowBlank(false);
        birthDate.setValidator(new MyFieldValidator());
        birthDate.setId("birthDate");
        birthDate.setEmptyText("-- birth day and month (ddmm) --");
        birthDate.setValidator(new Validator() {
            //@Override
            public String validate(Field<?> field, String value) {
                if(value == null || value.length() == 0)
                    return "The birth date field must be specified";
                else {
                    if(value.length() < 4)
                        return "The birth date field must be four digits, such as 0912";
                    try {
                        Integer.parseInt(value);
                    }
                    catch(NumberFormatException nfe) {
                        return "This birth date field must be only four numbers, such as 0703";
                    }
                    
                    
                    Integer mon = Integer.parseInt(value.substring(0,2));
                    if(mon < 0 || mon > 12)
                        return "The month " + mon + " is not a valid month";
                    
                    Integer day = Integer.parseInt(value.substring(2,4));
                    if(day < 0 || day > 31)
                        return "The day " + day + " is not a valid month day";
                }
                return null;
            }
        });
        fsProfile.add(birthDate);
        
        _formPanel.add(fsProfile);
        
        
        _formPanel.add(new Html("<p>Once you have created your account, next time you will use the school code and your own password to log in.</p>"));
        
        
        Button saveButton = new Button("Register");
        
        
        saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                doCreatePassword();
            }
        });
        
        _formPanel.addButton(saveButton);
        FormButtonBinding binding = new FormButtonBinding(_formPanel);  

        _formPanel.setStyleAttribute("margin-top", "20px");
        setLayout(new CenterLayout());
        add(_formPanel);
        
        
        ContextController.getInstance().setCurrentContext(new CmContext() {
            
            //@Override
            public void runAutoTest() {
            }
            
            //@Override
            public void resetContext() {
            }
            
            //@Override
            public List<Component> getTools() {
                return null;
            }
            
            //@Override
            public String getStatusMessage() {
                return "Create your own personal Catchup Math password.";
            }
            
            //@Override
            public String getContextTitle() {
                // TODO Auto-generated method stub
                return "Create Self Registration Account";
            }
            
            //@Override
            public String getContextSubTitle() {
                return "";
            }
            
            //@Override
            public String getContextHelp() {
                return getStatusMessage();
            }
            
            //@Override
            public int getContextCompletionPercent() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            //@Override
            public void doPrevious() {
                // TODO Auto-generated method stub
                
            }
            
            //@Override
            public void doNext() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    
    /** Create new password
     *  If successful, then load new user by replacing
     *  current page.  This allows for refresh of page 
     *  to no re-initiate the self-registration process.
     *  
     */
    private void doCreatePassword() {

        
        if(!_formPanel.isValid()) {
            InfoPopupBox.display(new CmInfoConfig("Validation problems", "Please correct any problems on the form."));
            return;
        }

        final String password = (lastName.getValue() + "-" + firstName.getValue() + "-" + birthDate.getValue());
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new CreateAutoRegistrationAccountAction(UserInfo.getInstance().getUid(), lastName.getValue() + ", " + firstName.getValue().trim(), password), new AsyncCallback<RpcData>() {
            //@Override
            public void onSuccess(final RpcData rdata) {
                
                final CmWindow win = new CmWindow();
                win.setSize(320,200);
                win.setModal(true);
                win.setClosable(false);
                String html = "<div style='margin: 10px;'>" +
                              "<p>Your personal password is: <br/><b>" + password + "</b></p>" +
                              "<p style='margin-top: 10px;'>Please log in using Login Name of <b>" + CmShared.__loginName + "</b> and the above password.</p>" +
                              "</div>";
                
                win.add(new Html(html));
                
                win.setHeading("Your personal password for Catchup Math");
                
                Button close = new Button("Begin Catchup Math");
                close.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        String userKey = rdata.getDataAsString("key");
                        String url = Window.Location.getPath();
                        
                        url += "?key=" + userKey;
                        Window.Location.replace(url);
                    }
                });
                
                win.getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
                win.addButton(close);
                
                win.setVisible(true);
            }

            //@Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                String msg = caught.getMessage();
                if(msg.indexOf("passcode you entered") > -1 || msg.indexOf("name you entered") > -1)
                    msg = "This name is taken by you or another person, if it was you login with your current password.";
                
                CatchupMathTools.showAlert(msg);
            }
        });        
    }
    
    
    private void showForgotPassword() {
        
        CmWindow w = new CmWindow();
        w.setTitle("Forgot Self-Registration password");
        w.setStyleName("auto-student-registration-forgot-password");
        w.setLayout(new FitLayout());
        
        
        String html = "<p>If you have already registered, then use the personal password you were previously assigned, " +
        		      "not the self-registartion password. If you forgot your personal password, please check with your teacher.</p>";
        
        
        w.setModal(true);
        w.add(new Html(html));
        w.setVisible(true);
    }
 
}


class MyFieldValidator implements Validator {

    public String validate(Field<?> field, String value) {
        if(value.trim().length() == 0)
            return "This field is required";
        
        return null;
    }
}