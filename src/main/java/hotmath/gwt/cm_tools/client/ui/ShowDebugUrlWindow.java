package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.shared.client.CmShared;

import com.google.gwt.user.client.ui.Anchor;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

/** Display the CM debug URL for the current user
 * 
 * @author casey
 *
 */
public class ShowDebugUrlWindow extends GWindow {
	
	public ShowDebugUrlWindow() {
		super(true);
		setHeadingText("Debug URL");
		setPixelSize(400,50);
        String url = CmShared.getServerForCmStudent() + "/loginService?debug=true&uid="  + UserInfo.getInstance().getUid();
        Anchor anchor = new Anchor(url);
        anchor.setHref(url);
        
        FlowLayoutContainer flc = new FlowLayoutContainer();
        flc.add(anchor);
        setWidget(flc);
        
        setVisible(true);
	}
}
