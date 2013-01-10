package hotmath.gwt.cm_mobile_shared.client.ui;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;

public class TouchAnchor extends Anchor {
    public TouchAnchor() {
        sinkEvents(Event.ONCLICK | Event.TOUCHEVENTS);
    }
    
    public TouchAnchor(String text) {
       this();
        setText(text);
    }
}

