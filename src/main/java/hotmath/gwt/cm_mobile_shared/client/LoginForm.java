package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetCmMobileLoginAction;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginForm extends AbstractPagePanel {
    @UiField
    Button loginButton;
    LoginPage loginPage;

    public LoginForm(LoginPage loginPage) {
    	this.loginPage = loginPage;
        initWidget(uiBinder.createAndBindUi(this));
        readCookie();
    }

    interface MyUiBinder extends UiBinder<Widget, LoginForm> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    DialogBox loginBox;

    @UiField
    TextBox usernameBox;

    @UiField
    PasswordTextBox passwordBox;
    
    @UiField
    Label errorMessage;

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
        
        saveCookie(uName,pass);
        
        
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_START));
        GetCmMobileLoginAction action = new GetCmMobileLoginAction();
        action.setName(uName);
        action.setPassword(pass);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmMobileUser>() {
            @Override
            public void onSuccess(CmMobileUser result) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                SharedData.setData(result);
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_USER_LOGIN));
            }

            @Override
            public void onFailure(Throwable caught) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                errorMessage.setText("Could not log in");
            }
        });
    }
    
    
    
    /** read the login cookie and extract data
     * 
     */
    private void readCookie() {
        String login = Cookies.getCookie("cm_login");
        if(login != null) {
            String p[] = login.split(":");
            if(p.length == 2) {
                usernameBox.setValue(p[0]);
                passwordBox.setValue(p[1]);
            }
        }        
    }
    
    private void saveCookie(String uName, String pass) {
        Date now = new Date();
        long nowLong = now.getTime();
        nowLong = nowLong + (1000 * 60 * 60 * 24 * 3000);
        now.setTime(nowLong);
        Cookies.setCookie("cm_login", uName + ":" + pass,now);
    }
}