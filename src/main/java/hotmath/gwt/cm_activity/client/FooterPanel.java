package hotmath.gwt.cm_activity.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class FooterPanel extends FlowPanel{
   public FooterPanel() {
       setStyleName("act-footer");
       add(new Label("Footer"));
   }
}
