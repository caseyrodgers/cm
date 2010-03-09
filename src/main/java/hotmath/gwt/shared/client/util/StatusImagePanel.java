package hotmath.gwt.shared.client.util;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Element;

public class StatusImagePanel extends LayoutContainer {
    
    int total;
    int current;
    
    /** Display image that represents the requested state
     * 
     * @see StatusPie to create static images
     * 
     * @param total
     * @param current
     */
    public StatusImagePanel(int total, int current, String title, String toolTip) {
        this.total = total;
        this.current = current;
        
        setStyleAttribute("position", "relative"); // so we can absolute position title
        setStyleAttribute("margin-top", "25px");
        
        setToolTip(toolTip);
        
        String html = 
            "<h2 style='color: #2097D1;position: absolute;top: 10px;left: 55px;'>" + title + "</h2>" +
            "<img style='margin-left: 20px;' src='/gwt-resources/images/status/status-" + total + "_" + current + ".png'/>";
        add(new Html(html));
    }
    
    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
    }
}
