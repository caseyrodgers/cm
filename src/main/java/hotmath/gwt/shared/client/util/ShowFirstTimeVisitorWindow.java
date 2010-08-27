package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.Date;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Frame;

public class ShowFirstTimeVisitorWindow extends CmWindow {

    UserInfo user;
    public ShowFirstTimeVisitorWindow(UserInfo user) {
        
        this.user = user;
        setStyleName("show-first-time-visitor-window");
        setSize(440,300);
        setModal(true);
       
        
        Button close = new Button("Close");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        addButton(close);
        getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
        
        drawGui();
        
        
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        
        setVisible(true);
    }
    
    
    protected void drawGui() {
       setLayout(new FillLayout());
       setHeading("Welcome to Catchup Math");
       Frame f = new Frame("/gwt-resources/first-time-visitor.html");
       add(f);
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
