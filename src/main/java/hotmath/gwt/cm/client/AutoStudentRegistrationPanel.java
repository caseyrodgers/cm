package hotmath.gwt.cm.client;

import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceContainer;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CheckUserAccountStatusAction;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountAction;
import hotmath.gwt.shared.client.rpc.action.LogUserInAction;
import hotmath.gwt.shared.client.util.CmInfoConfig;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
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
import com.google.gwt.user.client.ui.Label;


/** Provides Self Registration login screen and validation
 * 
 * @author casey
 *
 */
public class AutoStudentRegistrationPanel extends CmMainResourceContainer {

    FormPanel _formPanel;
    
    TextField<String> firstName;
    TextField<String> lastName;
    TextField<String> birthDate;
    
    public AutoStudentRegistrationPanel() {
        
        _formPanel = new FormPanel();
        _formPanel.setStyleName("register-student-form-panel");
        _formPanel.setLabelWidth(110);
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
        HorizontalPanel hp = new HorizontalPanel();
        fsAlready.setHeading("Have you already registered?");
        Button returnToHome = new Button("Return to Home Page");
        returnToHome.setToolTip("If you should have logged in with your personal password, click to retry from the Home page.");
        returnToHome.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                Window.Location.replace(CmShared.CM_HOME_URL);
            }
        });
        hp.add(returnToHome);
        
        Button alreadyRegistered = new Button("Forgot Password?");
        alreadyRegistered.setToolTip("Click here for information about your existing password");
        alreadyRegistered.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                showForgotPassword();
            }
        });
        hp.add(new Label(" "));
        hp.add(alreadyRegistered);
        fsAlready.add(hp);
        _formPanel.add(fsAlready);
        
        FieldSet fsProfile = new FieldSet();
        FormLayout fL = new FormLayout();
        fL.setLabelWidth(_formPanel.getLabelWidth());
        fL.setDefaultWidth(210);
        fsProfile.setLayout(fL);
        
        fsProfile.setHeading("Register");
        
        firstName = new TextField<String>();  
        firstName.setFieldLabel("First name");
        firstName.setAllowBlank(false);
        firstName.setValidator(new MyFieldValidator());
        firstName.setId("firstName");
        firstName.setEmptyText("-- enter first name --");
        fsProfile.add(firstName);
        
        lastName = new TextField<String>();  
        lastName.setFieldLabel("Last name");
        lastName.setAllowBlank(false);
        lastName.setValidator(new MyFieldValidator());
        lastName.setId("lastName");
        lastName.setEmptyText("-- enter last name --");
        fsProfile.add(lastName);
        
        birthDate = new TextField<String>();  
        birthDate.setFieldLabel("Birth date (mmdd)");
        birthDate.setAllowBlank(false);
        birthDate.setValidator(new MyFieldValidator());
        birthDate.setId("birthDate");
        birthDate.setEmptyText("-- birth month and day (mmdd) --");
        birthDate.setValidator(new Validator() {
            //@Override
            public String validate(Field<?> field, String value) {
                if(value == null || value.length() == 0)
                    return "The birth date field must be specified";
                else {
                    if(value.length() != 4)
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

        final String password = (lastName.getValue() + "-" + firstName.getValue() + "-" + birthDate.getValue()).toLowerCase();
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CreateAutoRegistrationAccountAction action = new CreateAutoRegistrationAccountAction(UserInfo.getInstance().getUid(), lastName.getValue() + ", " + firstName.getValue().trim(), password);
                setAction(action);
                CmShared.getCmService().execute(action,this );
            }
            //@Override
            public void oncapture(final RpcData rdata) {
                CmBusyManager.setBusy(false);
                String key = rdata.getDataAsString("key");
                showPasswordAssignment(password,key);
            }

            //@Override
            public void onFailure(Throwable caught) {
                CmLogger.error(caught.getMessage(), caught);
                String msg = caught.getMessage();
                if(msg.indexOf("passcode you entered") > -1) {
                    showAlreadyMsg(password);
                }
                else if(msg.indexOf("name you entered") > -1) {
                    checkIfPasswordMatches(password);
                }
                else
                    super.onFailure(caught);
            }
        }.register();
    }
    
    private void showPasswordAssignment(String password, final String key) {
        
        final CmWindow win = new CmWindow();
        win.setSize(320,200);
        win.setModal(true);
        win.setClosable(false);
        
        String ln = UserInfo.getInstance().getLoginName();
        String html = "<div style='margin: 10px;'>" +
                      "<p>Your personal password is: <br/><b>" + password + "</b></p>" +
                      "<p style='margin-top: 10px;'>In the future, your Login Name will be <b>" + ln + "</b> along with the above password, so please write them both down!</p>" +
                      "</div>";
        
        win.add(new Html(html));
        
        win.setHeading("Your personal password for Catchup Math");
        
        Button close = new Button("Begin Catchup Math");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                String userKey = key;
                String url = Window.Location.getPath();
                
                url += "?key=" + userKey;
                Window.Location.replace(url);
            }
        });
        
        win.getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
        win.addButton(close);
        
        win.setVisible(true);
    }
    
    private void showAlreadyMsg(final String password) {
        String msg = "You are already registered with this name.";
        CatchupMathTools.showAlert("Already Registered", msg,new CmAsyncRequestImplDefault() {
            public void requestComplete(String requestData) {
                /** create a login key for this user
                 * 
                 */
                new RetryAction<RpcData>() {
                    @Override
                    public void attempt() {
                        CmBusyManager.setBusy(true);
                        LogUserInAction action = new LogUserInAction(null,password);
                        setAction(action);
                        CmShared.getCmService().execute(action,this);
                    }
                    public void oncapture(RpcData result) {
                        CmBusyManager.setBusy(false);
                        String key = result.getDataAsString("key");
                        showPasswordAssignment(password, key);
                    }
                }.register();
            }
        });
    }

    
    /** Check to see if only password is in use
     * 
     * note: userName is a match, so we are checking for message
     * 
     */
    private void checkIfPasswordMatches(final String password) {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CheckUserAccountStatusAction action = new CheckUserAccountStatusAction(password);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            //@Override
            public void oncapture(final RpcData rdata) {
                CmBusyManager.setBusy(false);
                String msg = rdata.getDataAsString("message");
                if(msg.indexOf("duplicate") > -1) {
                    showAlreadyMsg(password);
                }
                else {
                    msg = "There is another registration with that name, so please add your middle name to the first-name box (e.g., Jim Bob).";
                    // this means the password including the date portion is unique
                    CatchupMathTools.showAlert("Already Registered", msg);
                }
            }
        }.register();
    }
    
    
    private void showForgotPassword() {
        
        final CmWindow w = new CmWindow();
        w.setHeading("Forgot Self-Registration password");
        w.setStyleName("auto-student-registration-forgot-password");
        w.setLayout(new FitLayout());
        
        
        String html = "<p>If you have already registered, use your personal password, not the self-registration password "+
                      "when you log in. Perhaps your password was something like Smith-Susie-0705.";
        
        w.setModal(true);
        w.add(new Html(html));
        w.setSize(300,170);
        
        Button close = new Button("Go");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Window.Location.replace(CmShared.CM_HOME_URL);
            }
        });
        
        w.getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
        w.addButton(close);        
        w.setVisible(true);
    }
 
}


class MyFieldValidator implements Validator {

    public String validate(Field<?> field, String value) {
        if(value.trim().length() == 0)
            return "This field is required";
        
        if(value.contains(" "))
            return "No spaces allowed";
        
        return null;
    }
}