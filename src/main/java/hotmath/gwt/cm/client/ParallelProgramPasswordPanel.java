package hotmath.gwt.cm.client;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.action.ParallelProgramLoginAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;
import hotmath.gwt.shared.client.util.CmInfoConfig;

import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

/**
 * Student login panel for Parallel Programs
 * 
 * @author bob
 * 
 */
public class ParallelProgramPasswordPanel extends CmMainResourceWrapper {

    FramedPanel _framedPanel;

    TextField password;

    String parallelProgName;

    public ParallelProgramPasswordPanel() {

        super(WrapperType.OPTIMIZED);

        _framedPanel = new FramedPanel();
        _framedPanel.setStyleName("register-student-form-panel");
        _framedPanel.setHeight(180);
        _framedPanel.setWidth(395);
        _framedPanel.setHeaderVisible(true);
        _framedPanel.setBodyBorder(false);

        parallelProgName = UserInfo.getInstance().getLoginName();

        _framedPanel.setHeadingText("Parallel Program Login for [ " + parallelProgName + " ]");

        FieldSet fsLogin = new FieldSet();
        VerticalLayoutContainer verticalLoginContent = new VerticalLayoutContainer();

        fsLogin.setHeadingText("Your Parallel Program password");

        password = new TextField();
        password.setEmptyText("-- enter your password --");
        password.setId("password");
        password.setAllowBlank(false);
        password.addValidator(new MyFieldValidator());

        password.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (event.getCharCode() == 13) {
                    // is a ENTER/RETURN
                    doLogin();
                }
            }
        });

        verticalLoginContent.add(new FieldLabel(password, "Password"));

        TextButton returnToHome = new TextButton("Return to Home Page");
        returnToHome
                .setToolTip("If you should have logged in with your personal password, click to retry from the Home page.");
        returnToHome.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                Window.Location.replace(CmShared.CM_HOME_URL);
            }
        });
        ButtonBar buttonBar = new ButtonBar();
        verticalLoginContent.add(buttonBar);

        buttonBar.add(returnToHome);

        TextButton forgotPassword = new TextButton("Forgot Password?");
        forgotPassword.setToolTip("Click here for information about your existing password");
        forgotPassword.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showForgotPassword();
            }
        });
        buttonBar.add(forgotPassword);
        verticalLoginContent.add(buttonBar);

        fsLogin.add(verticalLoginContent);

        TextButton loginButton = new TextButton("Login");
        loginButton.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                doLogin();
            }
        });

        _framedPanel.setWidget(fsLogin);
        _framedPanel.addButton(loginButton);

        // FormButtonBinding binding = new FormButtonBinding(_formPanel);

        // _framedPanel.addStyleAttribute("margin-top", "20px");

        getResourceWrapper().add(_framedPanel);

        ContextController.getInstance().setCurrentContext(new MyContext());
    }

    /**
     * Login to Parallel Program
     * 
     * If successful, then login user by replacing current page.
     * 
     */
    private void doLogin() {

        if (!password.isValid()) {
            InfoPopupBox.display(new CmInfoConfig("Validation problems", "Please correct any problems on the form."));
            return;
        }

        CmBusyManager.setBusy(true);
        ParallelProgramLoginAction action = new ParallelProgramLoginAction(UserInfo.getInstance().getUid(),
                password.getValue());
        CmShared.getCmService().execute(action, new CmAsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData rdata) {
                CmBusyManager.setBusy(false);

                /**
                 * check for error message
                 * 
                 * @TODO: move to specific object away from RpcData
                 * 
                 *        if error_message is non-null, then a programmatic
                 *        (local domain) error occurred. As opposed to a lower
                 *        level exception that would be caught by the generic
                 *        error handlers.
                 * 
                 */
                String errorMessage = rdata.getDataAsString("error_message");
                if (errorMessage != null && errorMessage.length() > 0) {
                    if (errorMessage.indexOf("is not available") > -1) {
                        showParallelProgNotAvail(password.getValue());
                    }
                } else {
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
        String url = "http://" + Window.Location.getHost();
        url += "/loginService?key=" + key;
        Window.Location.replace(url);
    }

    private void showParallelProgNotAvail(String password) {
        String html = "<p>The selected Parallel Program, " + UserInfo.getInstance().getLoginName()
                + ", is not available to the password you entered, " + password
                + ". Perhaps your password is something like Smith-John-0304.";

        CmMessageBox.showAlert(html, "Parallel Program Not Available", new CallbackOnComplete() {
            @Override
            public void isComplete() {
                // w.close();
            }
        });
    }

    private void showForgotPassword() {

        String html = "<p>Perhaps your password is something like Smith-John-0304.";

        CmMessageBox.showAlert(html, "Forgot Your Password", new CallbackOnComplete() {
            @Override
            public void isComplete() {
                // w.close();
            }
        });
    }

    static class MyFieldValidator extends AbstractValidator<String> {
        @Override
        public List<EditorError> validate(Editor<String> editor, String value) {
            if (value.trim().length() == 0)
                return createError(editor, "This field is required", value);

            if (value.contains(" "))
                return createError(editor, "No spaces allowed", value);

            return null;
        }

    }
    
    static class MyContext implements CmContext {

        @Override
        public void runAutoTest() {
        }

        @Override
        public void resetContext() {
        }

        @Override
        public List<Widget> getTools() {
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
    }    
}


