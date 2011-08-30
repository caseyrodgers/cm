package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile3.client.activity.LoginActivity.UserInfo;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;

public interface LoginView extends IPage {
    
    void setPresenter(Presenter presenter);
    
    void prepareLogin(UserInfo userInfo);
    
    static public interface Presenter {
        void doLogin(String userName, String passWord);
        void createDemo();
    }

}
