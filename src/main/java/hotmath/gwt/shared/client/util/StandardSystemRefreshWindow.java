package hotmath.gwt.shared.client.util;


import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;


/** Provide a standard window that informs user and requires
 *  a refresh of the page to reinitialize with server.
 *  
 * @author casey
 *
 */
public class StandardSystemRefreshWindow extends GWindow {

    public StandardSystemRefreshWindow(String title, String message) {
        this(title, message, null);
    }
    public StandardSystemRefreshWindow(String title, String message, TextButton btn) {
        super(false);
        
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));

        setPixelSize(300, 200);
        setResizable(false);
        setClosable(true);
        setModal(true);
        setClosable(false);
        setHeadingText(title);

        String msg = "<div style='font-size: 110%;padding: 10px;'>" + message + "</div>";
        add(new HTML(msg));
        if(btn == null) {
            btn = new TextButton("Refresh Page", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    CmShared.refreshPage();
                }
            });
        }
        addButton(btn);
    }
}
