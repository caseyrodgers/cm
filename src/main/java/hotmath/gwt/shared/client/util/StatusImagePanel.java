package hotmath.gwt.shared.client.util;

import hotmath.cm.status.StatusPie;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.google.gwt.user.client.Element;

public class StatusImagePanel extends VerticalPanel {
    
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
        setStyleName("status-image-panel");
        setToolTip("Your current program status");
        String html = "<img src='/gwt-resources/images/status/status-" + total + "_" + current + ".png'/>" +
                      "<span>" + toolTip + "</span>";
        add(new Html(html));
    }
    
    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
    }
}
