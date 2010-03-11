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
        setStyleAttribute("margin-top", "25px");
        setToolTip(toolTip);
        String html = 
            "<img style='margin-left: 20px;' src='/gwt-resources/images/status/status-" + total + "_" + current + ".png'/>";
        add(new Html(html));

//        double perDone = Math.round(((double)current / (double)total * 100));
//        html = "<div style='text-align: left;width: 100%;background: white' id='status_bar'>" +
//               "  <div style='background-color: green;width: " + perDone + "%' class='done'>&nbsp;</div>" +
//               "</div>";
//        
//        add(new Html(html));
    }
    
    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
    }
}
