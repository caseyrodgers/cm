package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;

public class AssignmentsOnlyPanel extends CenterLayoutContainer {
    
    interface MyUiBinder extends UiBinder<Widget, AssignmentsOnlyPanel> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public AssignmentsOnlyPanel() {
        ContentPanel lc = new ContentPanel();
        lc.setHeadingText("Catchup Math Assignments");
        lc.setWidth(400);
        lc.setHeight(200);
        addStyleName(UserInfo.getInstance().getBackgroundStyle());
        lc.add(uiBinder.createAndBindUi(this));
        setWidget(lc);
    }
}
