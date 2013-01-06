package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.shared.client.CmShared;

import com.google.gwt.user.client.ui.Anchor;
import com.sencha.gxt.widget.core.client.FramedPanel;

/**
 * Display the CM debug URL for the current user
 * 
 * @author casey
 * 
 */
public class ShowDebugUrlWindow extends GWindow {

    public ShowDebugUrlWindow() {
        super(true);
        setHeadingText("Debug URL");
        //setPixelSize(450, 100);
        setResizable(false);
        setPixelSize(450, 150);
        String url = getDebugUrl();
        Anchor anchor = new Anchor(url);
        anchor.setHref(url);

        FramedPanel framedPanel = new FramedPanel();
        framedPanel.setHeaderVisible(false);
        framedPanel.setWidget(anchor);
        setWidget(framedPanel);
        setVisible(true);
    }

    protected String getDebugUrl() {
        return CmShared.getServerForCmStudent() + "/loginService?debug=true&uid=" + UserInfo.getInstance().getUid();
    }
}
