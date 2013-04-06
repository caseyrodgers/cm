package hotmath.gwt.cm_tools.client.util;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;


/** Provide a standard GXT panel to use when loading async data into containers
 * s
 * @author casey
 *
 */
public class DefaultGxtLoadingPanel extends CenterLayoutContainer {
    public  DefaultGxtLoadingPanel() {
        this("Loading");
    }
    
    public DefaultGxtLoadingPanel(String msg) {
        //getElement().setAttribute("style", "background-color: gray");
        String html = "<h1 style='color: #1C97D1;'>" + msg + "</h1>";
        add(new HTML(html));
    }
}


