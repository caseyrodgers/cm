package hotmath.gwt.cm.client;

import hotmath.gwt.cm_rpc.client.UserInfo;
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
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CheckUserAccountStatusAction;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountAction;
import hotmath.gwt.shared.client.rpc.action.LogUserInAction;
import hotmath.gwt.shared.client.rpc.action.ParallelProgramLoginAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;
import hotmath.gwt.shared.client.util.CmInfoConfig;

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


/** 
 * Student login panel for Parallel Programs
 * 
 * @author bob
 *
 */
public class ParallelProgramPasswordPanel extends CmMainResourceContainer {

    FormPanel _formPanel;
    
    TextField<String> password;
    
    String parallelProgName;
    
    public ParallelProgramPasswordPanel() {

        _formPanel = new FormPanel();
        _formPanel.setStyleName("register-student-form-panel");
        _formPanel.setLabelWidth(110);
        _formPanel.setHeight(200);
        _formPanel.setWidth(395);
        _formPanel.setFooter(true);
        _formPanel.setFrame(true);
        _formPanel.setHeaderVisible(true);
        _formPanel.setBodyBorder(false);
        _formPanel.setIconStyle("icon-form");
        _formPanel.setButtonAlign(HorizontalAlignment.CENTER);
        _formPanel.setLayout(new FormLayout());
        
        parallelProgName = UserInfo.getInstance().getLoginName();
        _formPanel.setHeading("Parallel Program Login for [ " + parallelProgName + " ]");
        
        FieldSet fsLogin = new FieldSet();
        HorizontalPanel hp = new HorizontalPanel();
        fsLogin.setHeading(" ");

        password = new TextField<String>();  
        password.setFieldLabel("Password");
        password.setAllowBlank(false);
        password.setValidator(new FieldValidator());
        password.setId("password");
        password.setEmptyText("-- enter your password --");
        fsLogin.add(password);

        Button returnToHome = new Button("Return to Home Page");
        returnToHome.setToolTip("If you should have logged in with your personal password, click to retry from the Home page.");
        returnToHome.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                Window.Location.replace(CmShared.CM_HOME_URL);
            }
        });
        hp.add(returnToHome);
        
        Button forgotPassword = new Button("Forgot Password?");
        forgotPassword.setToolTip("Click here for information about your existing password");
        forgotPassword.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                showForgotPassword();
            }
        });
        hp.add(new Label(" "));
        hp.add(forgotPassword);
        fsLogin.add(hp);
        _formPanel.add(fsLogin);
        
        Button loginButton = new Button("Login");
        loginButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doLogin();
            }
        });
        
        _formPanel.addButton(loginButton);
        FormButtonBinding binding = new FormButtonBinding(_formPanel);  

        _formPanel.setStyleAttribute("margin-top", "20px");
        setLayout(new CenterLayout());
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
                return "Parallel Program Login";
            }
            
            @Override
            public String getContextTitle() {
                return "Parallel Program Login";
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
                return 0;
            }
            
            @Override
            public void doPrevious() {
            }
            
            @Override
            public void doNext() {
            }
        });
    }
    
    
    /** 
     *  Login to Parallel Program
     *  
     *  If successful, then login user by replacing current page.
     *  
     */
    private void doLogin() {
        
        if(!_formPanel.isValid()) {
            InfoPopupBox.display(new CmInfoConfig("Validation problems", "Please correct any problems on the form."));
            return;
        }

        CmBusyManager.setBusy(true);
        ParallelProgramLoginAction action = new ParallelProgramLoginAction(UserInfo.getInstance().getUid(), password.getValue());
        CmShared.getCmService().execute(action,new CmAsyncCallback<RpcData>() {
        	@Override
        	public void onSuccess(RpcData rdata) {
                CmBusyManager.setBusy(false);
             
                /** check for error message
                 * 
                 * @TODO: move to specific object away from RpcData
                 * 
                 * if error_message is non-null, then a programmatic
                 * (local domain) error occurred.  As opposed to a lower 
                 * level exception that would be caught by the generic
                 * error handlers.
                 * 
                 */
                String errorMessage = rdata.getDataAsString("error_message");
                if(errorMessage != null && errorMessage.length() > 0) {
                    if(errorMessage.indexOf("is not available") > -1) {
                        showParallelProgNotAvail(password.getValue());
                    }
                }
                else {
                	// complete login
                	String key = rdata.getDataAsString("key");
                	completeLogin(key);
                }
        	}
        	@Override
        	public void onFailure(Throwable caught) {
            	CmBusyManager.setBusy(false);
                CmLogger.error(caught.getMessage(), caught);
                String msg = caught.getMessage();
              	CatchupMathTools.showAlert("There was a problem logging into the Parallel Program: " + msg);
        	}
		});
    }

    private void completeLogin(final String key) {
        
        final CmWindow win = new CmWindow();
        win.setSize(320,200);
        win.setModal(true);
        win.setClosable(false);
        
        String html = "<div style='margin: 10px;'>" +
                      "<p>Your current program is<br/><b>" + parallelProgName + "</b></p>" +
                      "</div>";
        
        win.add(new Html(html));
        
        win.setHeading("Catchup Math");
        
        Button close = new Button("Begin Catchup Math");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                String userKey = key;
                String url = "http://" + Window.Location.getHost();
                
                url += "/loginService?key=" + userKey;
                Window.Location.replace(url);
            }
        });
        
        win.getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
        win.addButton(close);
        
        win.setVisible(true);
    }
    
    private void showParallelProgNotAvail(String password) {
        
        final CmWindow w = new CmWindow();
        w.setHeading("Parallel Program Not Available");
        w.setStyleName("auto-student-registration-forgot-password");
        w.setLayout(new FitLayout());
        
        
        String html = "<p>The selected Parallel Program, " + UserInfo.getInstance().getLoginName() + 
                      ", is not available to the password you entered, " + password +
                      ". Perhaps your password is something like Smith-John-0304.";
        
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
    
    private void showForgotPassword() {
        
        final CmWindow w = new CmWindow();
        w.setHeading("Forgot Your Password");
        w.setStyleName("auto-student-registration-forgot-password");
        w.setLayout(new FitLayout());
        
        
        String html = "<p>Perhaps your password is something like Smith-John-0304.";
        
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

class FieldValidator implements Validator {

    public String validate(Field<?> field, String value) {
        if(value.trim().length() == 0)
            return "This field is required";
        
        if(value.contains(" "))
            return "No spaces allowed";
        
        return null;
    }
}