package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class NotActiveProgramWindow extends GWindow {
    public NotActiveProgramWindow() {
        super(false);
        
    	EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
    	
        setPixelSize(300, 150);
        setResizable(false);
        setModal(true);
        setClosable(false);
        setHeadingText("Program Changed");
        String message = "Your program has been changed. " + "Please click the button below to start your new program.";

        String msg = "<div style='font-size: 110%;padding: 10px;'>" + message + "</div>";
        add(new HTML(msg));
        addButton(new TextButton("Load Program", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
            	String url="/loginService?uid=" + UserInfo.getInstance().getUid();
            	Window.Location.assign(url);
            }
        }));        
        setVisible(true);
    }
}
