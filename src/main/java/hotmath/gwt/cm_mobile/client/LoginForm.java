package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile.client.rpc.GetCmMobileLoginAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class LoginForm extends Composite {
    
    public LoginForm() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    interface MyUiBinder extends UiBinder<HorizontalPanel, LoginForm> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    


    private void doLogin() {
        GetCmMobileLoginAction action = new GetCmMobileLoginAction();
        CatchupMathMobile.getCmService().execute(action, new AsyncCallback<CmMobileUser>() {
            @Override
            public void onSuccess(CmMobileUser result) {
                Window.alert("Welcome: " + result.getName());
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error logging in: " + caught);
            }
        });
    }    
}