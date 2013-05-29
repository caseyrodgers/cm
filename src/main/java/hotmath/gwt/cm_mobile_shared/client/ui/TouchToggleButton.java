package hotmath.gwt.cm_mobile_shared.client.ui;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ToggleButton;

/** A normal ToggleButton with Touch Events enabled
 * 
 * Provides better support on mobile devices
 * 
 * @author casey
 *
 */
public class TouchToggleButton extends ToggleButton {
    public TouchToggleButton() {
        sinkEvents(Event.ONCLICK | Event.TOUCHEVENTS);
    }
    
    public TouchToggleButton(String text, ClickHandler handler) {
        super(text, handler);
    }
}

