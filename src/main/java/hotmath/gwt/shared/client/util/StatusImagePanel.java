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
    final int MAX_STATUS_IMAGES=35;
    
    public StatusImagePanel(int total, int current, String title, String toolTip) {
        this.total = total;
        this.current = current;
        setStyleName("status-image-panel");
        setToolTip("Your current program's status");
        
        String html="";
        if(total > MAX_STATUS_IMAGES) {
            /** no stautus image, show text representation
             * 
             */
            html = "<span color'#CCCCCC'>Completed " + current + " lesson" + (current==1?"":"s") + " of " + total + ".</span>"; 
        }
        else {
            html = "<img src='/gwt-resources/images/status/status-" + total + "_" + current + ".png'/>" +
                      "<span>" + toolTip + "</span>";
        }
        
        add(new Html(html));
    }
    
    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
    }
}
