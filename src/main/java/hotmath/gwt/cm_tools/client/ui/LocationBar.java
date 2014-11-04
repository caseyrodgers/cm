package hotmath.gwt.cm_tools.client.ui;

// import hotmath.gwt.cm.client.history.CmHistoryManager;
// import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.cm_rpc.client.UserInfo;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

/**
 * Display current user's location in CM program/Level/Prescription
 * 
 * @author casey
 * 
 */
public class LocationBar extends FlowLayoutContainer {
	public LocationBar() {
		setStyleName("location-bar");
		
        int activeLesson = UserInfo.getInstance().getSessionNumber();
        int numLessons = UserInfo.getInstance().getSessionCount();
        
        for (int i = 0; i < numLessons; i++) {
            Anchor a = new Anchor(Integer.toString(i + 1));
            final int lessNum=i;
            a.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent arg0) {
                      String locationStr = "p:" + lessNum;
                      //CmHistoryManager.getInstance().addHistoryLocation(new CmLocation(locationStr));
                }
            });
            
            String linkStyle = "normal";
            if(i == activeLesson) {
                linkStyle = "normal";
            }
            else if(i < activeLesson) {
                linkStyle = "completed";
            }
            else {
                linkStyle = "todo";
            }

            a.setStyleName(linkStyle);
            add(a);
        }
	}

	private int margins = 30;

}