package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile.client.rpc.GetCmMobileLoginAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginForm extends Composite {
    @UiField
    Button loginButton;

    public LoginForm() {
        initWidget(uiBinder.createAndBindUi(this));
        setHeight("300px");
    }

    interface MyUiBinder extends UiBinder<Widget, LoginForm> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    DialogBox loginBox;

    /**
     * Contains a reference to the username text field
     */
    @UiField
    TextBox usernameBox;

    /**
     * Contains a reference to the password text field
     */
    @UiField
    PasswordTextBox passwordBox;

    @UiHandler("loginButton")
    void handleClick(ClickEvent e) {
        doLogin();
    }

    private void doLogin() {
        String uName = usernameBox.getValue();
        String pass = passwordBox.getValue();
        if(uName == null || pass == null) {
            Window.alert("Enter username and password");
            return;
        }
            
        GetCmMobileLoginAction action = new GetCmMobileLoginAction();
        action.setName(uName);
        action.setPassword(pass);
        CatchupMathMobile.getCmService().execute(action, new AsyncCallback<CmMobileUser>() {
            @Override
            public void onSuccess(CmMobileUser result) {
                CatchupMathMobile.__instance.user = result;
                
                History.newItem("quiz");
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error logging in: " + caught);
            }
        });
    }
}