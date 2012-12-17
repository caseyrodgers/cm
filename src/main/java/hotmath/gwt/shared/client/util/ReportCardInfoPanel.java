package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_rpc.client.UserInfo;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;


/** Panel that contains minimal information about 
 *  user's current state within CM
 *  
 * @author casey
 * 
 * 
 * TODO: Not USED?
 *
 */
public class ReportCardInfoPanel extends FlowLayoutContainer {
    UserInfo user;
    public ReportCardInfoPanel(UserInfo user) {
        this.user = user;
        String html = getInfoHtml();
        add(new HTML(html));
    }
    
    
    private String getInfoHtml() {
        
        String html="";
        html += "<h3>" + user.getTestName() + "</h3>";
        if(user.getRunId() > 0) {
            html += "You are currently viewing lesson " + (user.getSessionNumber()+1) + " of " + user.getSessionCount();
        }
        else {
            int segment = user.getTestSegment()==0?1:user.getTestSegment();
            html += "You are currently taking quiz segment " + segment + " of " + user.getProgramSegmentCount();
        }
        return html;
    }
}
