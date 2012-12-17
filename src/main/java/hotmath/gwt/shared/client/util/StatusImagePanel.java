package hotmath.gwt.shared.client.util;

import hotmath.cm.status.StatusPie;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

public class StatusImagePanel extends VerticalLayoutContainer {
    
    /** Display image that represents the requested state
     * 
     * @see StatusPie to create static images
     * 
     * @param total
     * @param current
     */
    final int MAX_STATUS_IMAGES=35;
    PrescriptionData pData;
    FlowLayoutContainer _mainPanel = new FlowLayoutContainer();
    
    public StatusImagePanel(PrescriptionData pData) {
        this.pData = pData;
        setStyleName("status-image-panel");
        setToolTip("Your current program's status");
        add(_mainPanel);
        updateStatus();
    }
    
    
    /** Refresh and update the status text based on the real time 
     *  PrescriptionData items.
     *  
     */
    public void updateStatus() {
        _mainPanel.clear();
        
        
        int total=pData.getSessionTopics().size();
        int numberComplete=0;
        for(SessionTopic st: pData.getSessionTopics()) {
            if(st.isComplete()) {
                numberComplete++;
            }
        }
        
        String msg = "Completed " + numberComplete + " lesson" + (numberComplete==1?"":"s") + " of " + total + "."; 
        
        String html="";
        if(total > MAX_STATUS_IMAGES) {
            /** no stautus image, show text representation
             * 
             */
            html = "<span color'#CCCCCC'>" + msg + "</span>"; 
        }
        else {
            html = "<img src='/gwt-resources/images/status/status-" + total + "_" + numberComplete + ".png'/>" +
                      "<span>" + msg + "</span>";
        }
        
        _mainPanel.add(new HTML(html));
        forceLayout();
    }

}
