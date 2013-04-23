package hotmath.gwt.cm.client;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.ui.MyFieldSet;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CheckUserAccountStatusAction;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountAction;
import hotmath.gwt.shared.client.rpc.action.LogUserInAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;

import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * Provides Self Registration login screen and validation
 * 
 * @author casey
 * 
 */
public class AutoStudentRegistrationPanel extends CmMainResourceWrapper {

    FramedPanel _framedPanel;

    TextField firstName;
    TextField lastName;
    TextField birthDate;

    int LABEL_LEN = 100, FIELD_LEN = 200;
    
    public AutoStudentRegistrationPanel() {

        super(WrapperType.OPTIMIZED);

        _framedPanel = new FramedPanel();
        _framedPanel.setHeight(300);
        _framedPanel.setWidth(395);
        _framedPanel.setHeadingText("Self Registration");

        VerticalLayoutContainer verMain = new VerticalLayoutContainer();
        //verMain.addStyleName("register-student-form-panel");

        _framedPanel.setWidget(verMain);

        MyFieldSet fsAlready = new MyFieldSet("Have you already registered?");
        verMain.add(fsAlready);
        HorizontalPanel buttonBar = new HorizontalPanel();
        
        TextButton returnToHome = new TextButton("Return to Home Page");
        returnToHome.setToolTip("If you should have logged in with your personal password, click to retry from the Home page.");
        returnToHome.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                Window.Location.replace(CmShared.CM_HOME_URL);
            }
        });
        buttonBar.add(returnToHome);

        TextButton alreadyRegistered = new TextButton("Forgot Password?");
        alreadyRegistered.setToolTip("Click here for information about your existing password");
        alreadyRegistered.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showForgotPassword();
            }
        });
        buttonBar.add(alreadyRegistered);
        
        fsAlready.addThing(buttonBar);

        MyFieldSet fsProfile = new MyFieldSet("Register");
        //fsProfile.setHeight(120);
        firstName = new TextField();
        
        firstName.setAllowBlank(false);
        firstName.addValidator(new MyFieldValidator());
        firstName.setId("firstName");
        firstName.setEmptyText("-- enter first name --");
        fsProfile.addThing(new MyFieldLabel(firstName, "First Name", LABEL_LEN, FIELD_LEN));

        lastName = new TextField();
        lastName.setAllowBlank(false);
        lastName.addValidator(new MyFieldValidator());
        lastName.setId("lastName");
        lastName.setEmptyText("-- enter last name --");
        fsProfile.addThing(new MyFieldLabel(lastName, "Last name", LABEL_LEN, FIELD_LEN));

        birthDate = new TextField();
        birthDate.setAllowBlank(false);
        birthDate.addValidator(new MyFieldValidator());
        birthDate.setId("birthDate");
        birthDate.setEmptyText("-- birth month and day (mmdd) --");
        birthDate.addValidator(new MyDateFieldValidator());
        fsProfile.addThing(new MyFieldLabel(birthDate, "Birth date (mmdd)", LABEL_LEN, FIELD_LEN));

        verMain.add(fsProfile);
        
        TextButton saveButton = new TextButton("Register");

        saveButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doCreatePassword();
            }
        });
        _framedPanel.addButton(saveButton);

        // FormButtonBinding binding = new FormButtonBinding(_formPanel);
        getResourceWrapper().add(_framedPanel);

        ContextController.getInstance().setCurrentContext(new EmptyContext() {
            @Override
            public String getStatusMessage() {
                return "Create your own personal Catchup Math password.";
            }
            @Override
            public String getContextTitle() {
                return "Create Self Registration Account";
            }
        });
    }

    private boolean isFormValid() {
        return (birthDate.isValid() && lastName.isValid() && firstName.isValid());
    }

    /**
     * Create new password If successful, then load new user by replacing
     * current page. This allows for refresh of page to no re-initiate the
     * self-registration process.
     * 
     */
    private void doCreatePassword() {

        if (!isFormValid()) {
            CatchupMathTools.showAlert("Validation problems", "Please correct any problems on the form.");
            return;
        }
        final String password = (lastName.getValue() + "-" + firstName.getValue() + "-" + birthDate.getValue())
                .toLowerCase();

        CmBusyManager.setBusy(true);
        CreateAutoRegistrationAccountAction action = new CreateAutoRegistrationAccountAction(UserInfo.getInstance()
                .getUid(), lastName.getValue() + ", " + firstName.getValue().trim(), password);
        CmShared.getCmService().execute(action, new CmAsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData rdata) {
                CmBusyManager.setBusy(false);

                /**
                 * check for build-in error message
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
                    if (errorMessage.indexOf("passcode you entered") > -1) {
                        showAlreadyMsg(password);
                    } else if (errorMessage.indexOf("name you entered") > -1) {
                        checkIfPasswordMatches(password);
                    }
                } else {
                    String key = rdata.getDataAsString("key");
                    showPasswordAssignment(password, key);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                CmLogger.error(caught.getMessage(), caught);
                String msg = caught.getMessage();
                CatchupMathTools.showAlert("There was a problem creating your new account: " + msg);
            }
        });
    }

    private void showPasswordAssignment(String password, final String key) {

        final GWindow win = new GWindow(false);
        win.setPixelSize(320, 200);
        win.setModal(true);
        win.setClosable(false);

        String ln = UserInfo.getInstance().getLoginName();
        String html = "<div style='margin: 10px;'>" + "<p>Your personal password is: <br/><b>" + password + "</b></p>"
                + "<p style='margin-top: 10px;'>In the future, your Login Name will be <b>" + ln
                + "</b> along with the above password, so please write them both down!</p>" + "</div>";

        win.add(new HTML(html));

        win.setHeadingText("Your personal password for Catchup Math");

        TextButton close = new TextButton("Begin Catchup Math");
        close.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                String userKey = key;
                String url = "http://" + Window.Location.getHost();

                url += "/loginService?key=" + userKey;
                Window.Location.replace(url);
            }
        });
        win.addButton(close);
        win.setVisible(true);
    }

    private void showAlreadyMsg(final String password) {
        String msg = "<p style='padding: 10px'>You are already registered with this name.</p>";
        GWindow w = new GWindow(false);
        w.setHeadingText("Already Registered");
        w.setClosable(false);
        w.add(new HTML(msg));
        w.setPixelSize(300, 130);
        w.addButton(new TextButton("Get Login Info", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                LogUserInAction action = new LogUserInAction(null, password, CmShared.getBrowserInfo());
                CmShared.getCmService().execute(action, new CmAsyncCallback<RpcData>() {
                    public void onSuccess(RpcData result) {
                        CmBusyManager.setBusy(false);
                        String key = result.getDataAsString("key");
                        showPasswordAssignment(password, key);
                    }

                    public void onFailure(Throwable caught) {
                        CmBusyManager.setBusy(false);
                        super.onFailure(caught);
                    }
                });
            }
        }));
        w.setVisible(true);
    }

    /**
     * Check to see if only password is in use
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

            // @Override
            public void oncapture(final RpcData rdata) {
                CmBusyManager.setBusy(false);
                String msg = rdata.getDataAsString("message");
                if (msg.indexOf("duplicate") > -1) {
                    showAlreadyMsg(password);
                } else {
                    msg = "There is another registration with that name, so please add your middle name to the first-name box (e.g., Jim Bob).";
                    // this means the password including the date portion is
                    // unique
                    CatchupMathTools.showAlert("Already Registered", msg);
                }
            }
        }.register();
    }

    private void showForgotPassword() {

        final GWindow w = new GWindow(false);
        w.setHeadingText("Forgot Self-Registration password");
        w.setStyleName("auto-student-registration-forgot-password");

        String html = "<p>If you have already registered, use your personal password, not the self-registration password "
                + "when you log in. Perhaps your password was something like Smith-Susie-0705.";

        w.setModal(true);
        w.add(new HTML(html));
        w.setPixelSize(300, 170);

        TextButton close = new TextButton("Go");
        close.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                Window.Location.replace(CmShared.CM_HOME_URL);
            }

        });
        w.addButton(close);
        w.setVisible(true);
    }

}

class MyFieldValidator extends AbstractValidator<String> {
    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        if (value == null || value.trim().length() == 0)
            return createError(editor, "This field is required", value);

        if (value.contains(" ")) {
            return createError(editor, "No spaces allowed", value);
        }
        return null;
    }

}

class MyDateFieldValidator extends AbstractValidator<String> {
    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        if (value == null || value.length() == 0)
            return createError(editor, "The birth date field must be specified", value);
        else {
            if (value.length() != 4)
                return createError(editor, "The birth date field must be four digits, such as 0912", value);
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                return createError(editor, "This birth date field must be only four numbers, such as 0703", value);
            }

            Integer mon = Integer.parseInt(value.substring(0, 2));
            if (mon < 0 || mon > 12)
                return createError(editor, "The month " + mon + " is not a valid month", value);

            Integer day = Integer.parseInt(value.substring(2, 4));
            if (day < 0 || day > 31)
                return createError(editor, "The day " + day + " is not a valid month day", value);
        }
        return null;

    }
}

class EmptyContext implements CmContext {

    // @Override
    public void runAutoTest() {
    }

    // @Override
    public void resetContext() {
    }

    // @Override
    public List<Widget> getTools() {
        return null;
    }

    // @Override
    public String getStatusMessage() {
        return null;
    }

    // @Override
    public String getContextTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    // @Override
    public String getContextSubTitle() {
        return "";
    }

    // @Override
    public String getContextHelp() {
        return getStatusMessage();
    }

    // @Override
    public int getContextCompletionPercent() {
        // TODO Auto-generated method stub
        return 0;
    }

    // @Override
    public void doPrevious() {
        // TODO Auto-generated method stub

    }

    // @Override
    public void doNext() {
        // TODO Auto-generated method stub

    }
}
