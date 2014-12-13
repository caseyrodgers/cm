package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc.client.UserInfo;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class NoProgramAssignedPanel extends CenterLayoutContainer {
    public NoProgramAssignedPanel() {
        ContentPanel lc = new ContentPanel();
        lc.setHeadingText("No Program Assigned");
        addStyleName("cm-welcome-panel");
        lc.addStyleName("welcome-wrapper");
        lc.setWidth(400);
        lc.setHeight(200);
        addStyleName(UserInfo.getInstance().getBackgroundStyle());
        lc.add(new HTML("<div style='font-size: 1.2em;margin: 20px'>No Program Assigned.  Please tell your instructor.</p>"));
        lc.addButton(new TextButton("Retry", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                CmCore.reloadUser();
            }
        }));
        setWidget(lc);
    }
}
