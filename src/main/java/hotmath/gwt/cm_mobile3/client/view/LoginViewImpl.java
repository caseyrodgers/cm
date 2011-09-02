package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile3.client.activity.LoginActivity.UserInfo;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.LoginPage;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginViewImpl extends AbstractPagePanel implements LoginView {

    Presenter presenter;
    
    public LoginViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    interface MyUiBinder extends UiBinder<Widget, LoginViewImpl> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);



    private void doLogin() {
        String uName = usernameBox.getValue();
        String pass = passwordBox.getValue();
        
        presenter.doLogin(uName, pass);
    }
    

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getTitle() {
        return "Login to Catchup Math";
    }
    @Override
    public String getBackButtonText() {
        return null;
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }

    @Override
    public void prepareLogin(UserInfo userInfo) {
        if(userInfo != null) {
            usernameBox.setText(userInfo.getUser());
            passwordBox.setText(userInfo.getPassWord());
        }   
    }
    

    @UiHandler("loginButton")
    void handleLogin(ClickEvent ce) {
        doLogin();
    }
    
    
    @UiHandler("demoButton")
    void handleDemo(ClickEvent ce) {
        presenter.createDemo();
    }
    
    
    @UiHandler("passwordBox")
    protected void doChangeValue(ValueChangeEvent<String> event) {
      Log.info(event.toDebugString());
    }
    
    
    @UiField
    Button loginButton;
    LoginPage loginPage;
    
    @UiField
    Button demoButton;

    @UiField
    DialogBox loginBox;

    @UiField
    TextBox usernameBox;

    @UiField
    PasswordTextBox passwordBox;
    
    @UiField
    Label errorMessage;
}