package hotmath.gwt.shared.client.util;


import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;


/** Provide a standard window that informs user and requires
 *  a refresh of the page to reinitialize with server.
 *  
 * @author casey
 *
 */
public class StandardSystemRefreshWindow extends CmWindow {

    public StandardSystemRefreshWindow(String title, String message) {
        this(title, message, null);
    }
    public StandardSystemRefreshWindow(String title, String message, Button btn) {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));

        setSize(300, 200);
        setResizable(true);
        setClosable(true);
        setModal(true);
        setClosable(false);
        setHeading(title);

        setStyleName("standard-system-refresh-window");

        String msg = "<div style='font-size: 110%;padding: 10px;'>" + message + "</div>";
        add(new Html(msg));
        if(btn == null) {
            btn = new Button("Refresh Page", new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    CmShared.refreshPage();
                }
            });
        }
        addButton(btn);
            
    }
}
