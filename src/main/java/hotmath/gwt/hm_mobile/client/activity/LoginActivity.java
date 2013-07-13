package hotmath.gwt.hm_mobile.client.activity;

import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.event.HmLoginEvent;
import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;
import hotmath.gwt.hm_mobile.client.persist.HmMobilePersistedPropertiesManager;
import hotmath.gwt.hm_mobile.client.rpc.HmMobileLoginAction;
import hotmath.gwt.hm_mobile.client.view.LoginView;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTML;

public class LoginActivity extends AbstractActivity implements LoginView.Presenter{

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
    }

    @Override
    public void goTo(Place place) {
    }
    
    String _standardLoginError = "Login not recognized. Personal subscribers enter email address; others enter school password. Please try again.";

    @Override
    public void doLogin(String userName, String password) {
        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
        
        HmMobileLoginAction action = new HmMobileLoginAction(userName, password);
        HmMobile.getCmService().execute(action, new AsyncCallback<HmMobileLoginInfo>() {
            @Override
            public void onSuccess(HmMobileLoginInfo loginInfo) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                Log.info("User logged in: " + loginInfo);
                

                if(loginInfo.getDateExpired() == null) {
                    PopupMessageBox.showMessage("Login Failed", new HTML("This Account does not include Hotmath Solutions."), null);
                }
                else if(loginInfo.isExpired()) {
                    PopupMessageBox.showMessage("Login Failed", new HTML("This Solutions Account expired as of <div style='font-weight: bold'> " + HmMobilePersistedPropertiesManager._expiredDateFormat.format(loginInfo.getDateExpired()) + "</div>"), null);
                }
                else {
                    CmRpcCore.EVENT_BUS.fireEvent(new HmLoginEvent(loginInfo));
                }
            }

            @Override
            public void onFailure(Throwable e) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                Log.error("Error logging in", e);
                
                PopupMessageBox.showMessage("Login Failed", new HTML(_standardLoginError), null);
            }
        });
        
    }

}
