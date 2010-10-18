package hotmath.gwt.cm_activity.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class HeaderPanel extends FlowPanel{ 

    public HeaderPanel() {
        setStyleName("act-header");
        add(new Label("Header Panel"));
    }

}
