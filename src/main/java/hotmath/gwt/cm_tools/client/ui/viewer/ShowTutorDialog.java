package hotmath.gwt.cm_tools.client.ui.viewer;


import hotmath.gwt.shared.client.util.UserInfo;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Frame;

/** Display the Live Tutoring dialog
 * 
 * @author casey
 *
 */
public class ShowTutorDialog extends Window {
    
    String pid;
    
    public ShowTutorDialog(String pid) {
        this.pid = pid;
        setHeading("Live Tutoring");
        setSize(820, 570);
        buildGui();
    }
    
    
    private void buildGui() {
        String contentUrl = "pid=" + pid;
        setLayout(new FitLayout());
        Frame f = new Frame("/collab/lwl/cm_lwl_launch.jsp?uid=" + UserInfo.getInstance().getUid() + "&contentUrl=" + contentUrl);
        f.setSize("100%", "500px");
        add(f);
    }
}
