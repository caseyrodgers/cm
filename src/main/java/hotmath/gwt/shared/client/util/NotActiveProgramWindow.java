package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Window;

public class NotActiveProgramWindow extends CmWindow {
    public NotActiveProgramWindow() {
    	EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
    	setStyleName("standard-system-refresh-window");
    	
        setSize(300, 200);
        setResizable(true);
        setClosable(true);
        setModal(true);
        setClosable(false);
        setHeading("Program Changed");
        String message = "Your program has been changed. " +
        "Please click the button below to start your new program.";

        String msg = "<div style='font-size: 110%;padding: 10px;'>" + message + "</div>";
        add(new Html(msg));
        addButton(new Button("Load Program", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
            	String url="/loginService?uid=" + UserInfo.getInstance().getUid();
            	Window.Location.assign(url);
            }
        }));        
        setVisible(true);
    }
}
