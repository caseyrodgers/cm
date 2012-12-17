package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import java.util.Date;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Frame;

public class ShowFirstTimeVisitorWindow extends GWindow {

    UserInfo user;
    public ShowFirstTimeVisitorWindow(UserInfo user) {
        super(true);
        setHeadingText("Welcome to Catchup Math");
        
        this.user = user;
        setPixelSize(440,300);
        setModal(true);
        
        Frame f = new Frame("/gwt-resources/first-time-visitor.html");
        setWidget(f);

        setVisible(true);
    }
    

    /** Only show dialog once a week
     * 
     * @param user
     */
    static public void displayIfFirstTime(UserInfo user) {
        
        String viewed = Cookies.getCookie("cm_first");
        if(viewed == null) {
            Date now = new Date();
            long nowLong = now.getTime();
            nowLong = nowLong + (1000 * 60 * 60 * 24 * 7);//seven days
            now.setTime(nowLong);

            Cookies.setCookie("cm_first", "time: " + now.getTime(), now);
            
            new ShowFirstTimeVisitorWindow(user);
        }
    }
}
