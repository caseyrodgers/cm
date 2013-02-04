package hotmath.gwt.cm_mobile_shared.client.ui;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;

/** A normal button with Touch Events enabled
 * 
 * Provides better support on mobile devices
 * 
 * @author casey
 *
 */
public class TouchButton extends Button {
    public TouchButton() {
        sinkEvents(Event.ONCLICK | Event.TOUCHEVENTS);
    }
    
    public TouchButton(String text, ClickHandler handler) {
        super(text, handler);
    }
}

