package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class AssignmentsOnlyPanel extends CenterLayoutContainer {
    public AssignmentsOnlyPanel() {
        ContentPanel lc = new ContentPanel();
        lc.setHeadingText("Catchup Math Assignments");
        addStyleName("cm-welcome-panel");
        lc.addStyleName("welcome-wrapper");
        lc.setWidth(400);
        lc.setHeight(200);
        addStyleName(UserInfo.getInstance().getBackgroundStyle());
        lc.add(new HTML("<div style='font-size: 1.2em;margin: 20px'>Click the Assignments button at the top left or below to check your assignments.</p>"));
        lc.addButton(new TextButton("Show Assignments", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                StudentAssignmentSelectorDialog.showSharedDialog();
            }
        }));
        setWidget(lc);
    }
}
