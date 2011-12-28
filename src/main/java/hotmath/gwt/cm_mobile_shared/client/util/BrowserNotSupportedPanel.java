package hotmath.gwt.cm_mobile_shared.client.util;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class BrowserNotSupportedPanel extends FlowPanel {
    
    public BrowserNotSupportedPanel() {
        
        String _html = "<h1>Catchup Math Mobile</h1>" +
                       "<h2>Browser Not Supported</h2>" +
                       "<p>This beta version of Catchup Math Mobile currently only supports IPads.</p>" +
                       "<p>We will be supporting additional tablet environments in the near future.</p>";
        
        getElement().setAttribute("style", "padding: 25px");
        addStyleName("browserNotSupportedPanel");
        add(new HTML(_html));
    }
}
