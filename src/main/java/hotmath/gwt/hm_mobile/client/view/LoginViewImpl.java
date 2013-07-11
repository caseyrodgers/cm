package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.ui.TouchButton;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginViewImpl extends AbstractPagePanel implements LoginView, IPage {

	interface MyUiBinder extends UiBinder<Widget, LoginViewImpl> {
	}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	Presenter presenter;

    private CallbackOnComplete callback;

	public LoginViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
		passwordWrapper.setAttribute("style",  "visibility: hidden");
		usernameBox.addKeyUpHandler(new KeyUpHandler() {
            
            @Override
            public void onKeyUp(KeyUpEvent event) {
                System.out.println("code: " + event.getNativeEvent().getKeyCode());
                
                if( (event.isShiftKeyDown() && event.getNativeEvent().getKeyCode() == 50) || usernameBox.getText().indexOf("@") > -1) {
                    passwordWrapper.setAttribute("style",  "visibility: visible");            
                }
                else {
                    passwordWrapper.setAttribute("style",  "visibility: hidden");
                }
            }
        });
	}
    @Override
    public void resetView() {
    }

    @Override
    public String getViewTitle() {
        return "Hotmath Member Login";
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
    public void setPresenter(Presenter presenter, CallbackOnComplete callback) {
        this.presenter = presenter;
        this.callback = callback;
        
        // form is ready
        this.callback.isComplete();
    }
    
    
    @UiHandler("loginButton")
    protected void handleLoginButton(ClickEvent ce) {
        String userName = usernameBox.getValue();
        if(userName == null || userName.length() == 0) {
            showMessage("User name must be specified.");
            return;
        }
        String password = null;
        if(userName.indexOf("@") > -1) {
            password = passwordBox.getValue();
            if(password == null || password.length() == 0) {
                showMessage("Password must be specified.");
                return;
            }
        }
        
        
        presenter.doLogin(userName, password);
    }
    
    private void showMessage(String msg) {
        errorMessage.setText(msg);
    }
    
    @UiField 
    DialogBox loginBox;
    
    @UiField
    TextBox usernameBox;
    
    @UiField
    PasswordTextBox passwordBox;
    
    @UiField
    Label errorMessage;
    
    @UiField
    TouchButton loginButton, demoButton;
    
    @UiField
    Element passwordWrapper;
    
}
