package hotmath.gwt.cm_tools.client.ui;

import com.google.gwt.user.client.Event;
import com.sencha.gxt.widget.core.client.button.IconButton;

public class MyIconButton extends IconButton {
    Event currentEvent;
    
    public MyIconButton(String style) {
        super(style);
    }
    
    @Override
    public void onBrowserEvent(Event event) {
        currentEvent = event;
        super.onBrowserEvent(event);
        currentEvent = null;
    }
    
    public Event getCurrentEvent() {
        return currentEvent;
    }
}