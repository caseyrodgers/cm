package hotmath.gwt.hm_mobile.client.activity;

import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;
import hotmath.gwt.hm_mobile.client.rpc.HmMobileLoginAction;
import hotmath.gwt.hm_mobile.client.view.LoginView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class LoginActivity extends AbstractActivity implements LoginView.Presenter{

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
    }

    @Override
    public void goTo(Place place) {
    }

    @Override
    public void doLogin(String userName, String password) {
        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
        
        HmMobileLoginAction action = new HmMobileLoginAction(userName, password);
        HmMobile.getCmService().execute(action, new AsyncCallback<HmMobileLoginInfo>() {
            @Override
            public void onSuccess(HmMobileLoginInfo loginInfo) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                Window.alert("YOU ARE NOW LOGGED IN: " + loginInfo);
            }

            @Override
            public void onFailure(Throwable arg0) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));

                arg0.printStackTrace();
                Window.alert("Server error: " + arg0);
            }
        });
        
    }

}
