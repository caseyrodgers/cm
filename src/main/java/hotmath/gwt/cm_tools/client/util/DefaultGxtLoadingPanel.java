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
        getElement().setAttribute("style", "background-color: gray");
        String html = "<h1 style='font-size: 25px;color: #666'>" + msg + "</h1>";
        add(new HTML(html));
    }
}


