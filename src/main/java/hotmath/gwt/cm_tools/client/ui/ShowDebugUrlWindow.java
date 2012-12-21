package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.shared.client.CmShared;

import com.google.gwt.user.client.ui.Anchor;

/** Display the CM debug URL for the current user
 * 
 * @author casey
 *
 */
public class ShowDebugUrlWindow extends GWindow {
	
	public ShowDebugUrlWindow() {
		super(true);
		setHeadingText("Debug URL");
		setPixelSize(200,50);
        String url = CmShared.getServerForCmStudent() + "/loginService?debug=true&uid="  + UserInfo.getInstance().getUid();
        Anchor anchor = new Anchor(url);
        setWidget(anchor);
        
        setVisible(true);
	}
}
