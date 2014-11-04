package hotmath.gwt.cm_tools.client.ui.viewer;


import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.Frame;

/** Display the Live Tutoring dialog
 * 
 * @author casey
 *
 */
public class ShowTutorDialog extends GWindow {
    
    String pid;
    
    public ShowTutorDialog(String pid) {
        super(true);
        this.pid = pid;
        setHeadingText("Live Tutoring");
        setPixelSize(820, 570);
        buildGui();
    }
    
    
    private void buildGui() {
        String contentUrl = "pid=" + pid;
        Frame f = new Frame("/collab/lwl/cm_lwl_launch.jsp?uid=" + UserInfo.getInstance().getUid() + "&contentUrl=" + contentUrl);
        f.setSize("100%", "500px");
        setWidget(f);
    }
}
