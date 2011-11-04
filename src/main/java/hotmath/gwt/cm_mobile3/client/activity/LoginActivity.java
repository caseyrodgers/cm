package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile3.client.event.ShowWelcomeViewEvent;
import hotmath.gwt.cm_mobile3.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_mobile3.client.view.LoginView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.ShowFlashRequiredEvent;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;

import java.util.Date;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoginActivity implements LoginView.Presenter {
    
    private com.google.gwt.event.shared.EventBus eventBus;

    public LoginActivity(com.google.gwt.event.shared.EventBus eventBus) {
        this.eventBus = eventBus;
    }

    
    
    public void prepareLogin(LoginView loginView) {
        loginView.prepareLogin(readCookie());
    }

    
    @Override
    public void doLogin(final String userName, final String passWord) {
        if(userName == null || userName.length() == 0 || passWord == null) {
            MessageBox.showError("Enter username and password");
            return;
        }
        
        if(!userName.equals("catchup_demo")) {
            saveCookie(userName,passWord);
        }
        
        Log.info("Logging in user: " + userName + ", " + passWord);
        
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        
        GetCmMobileLoginAction action = new GetCmMobileLoginAction();
        action.setName(userName);
        action.setPassword(passWord);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmMobileUser>() {
            @Override
            public void onSuccess(CmMobileUser result) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                Log.info("Login successful: " + result);
                
                
                if(result.getFlowAction().getPlace() == CmPlace.ERROR_FLASH_REQUIRED) {
                    eventBus.fireEvent(new ShowFlashRequiredEvent());
                }
                else {
                    CatchupMathMobileShared.__instance.user = result;
                    
                    SharedData.setUserInfo(result.getBaseLoginResponse().getUserInfo());
                    SharedData.setFlowAction(result.getFlowAction());
                    
                    eventBus.fireEvent(new ShowWelcomeViewEvent());
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                
                /** Look for special cases and provide helper functionality */
                if(caught.getMessage().indexOf("Invalid user type: ADMIN") > -1) {
                    Window.Location.assign("/loginService?user=" + userName + "&pwd=" + passWord);
                }
                else {
                    eventBus.fireEvent(new SystemIsBusyEvent(false));
                    MessageBox.showMessage("Could not log you in: " + caught.getMessage());
                }
            }
        });
    }

    @Override
    public void createDemo() {
        Log.info("Create demo account");
        String userName="catchup_demo";
        String passWord = "demo";
        doLogin(userName, passWord);
    }

    
    public static class UserInfo {
        
        
        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassWord() {
            return passWord;
        }

        public void setPassWord(String passWord) {
            this.passWord = passWord;
        }

        String user, passWord;
    }
    
    
    private void saveCookie(String uName, String pass) {
        Date now = new Date();
        long nowLong = now.getTime();
        nowLong = nowLong + (1000 * 60 * 60 * 24 * 3000);
        now.setTime(nowLong);
        Cookies.setCookie("cm_login", uName + ":" + pass,now);
    }

    /** read the login cookie and extract data
     * 
     */
    private UserInfo readCookie() {
        String login = Cookies.getCookie("cm_login");
        if(login != null) {
            String p[] = login.split(":");
            if(p.length == 2) {
                UserInfo ui = new UserInfo();
                ui.user = p[0];
                ui.passWord = p[1];
                return ui;
            }
        }        
        
        return null;
    }    
    
}
