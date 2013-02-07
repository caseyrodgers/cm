package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.ui.UserProgramStatusPanel;

public class ShowUserProgramStatusDialog extends GWindow {

    public ShowUserProgramStatusDialog() {
        super(true);
        setPixelSize(300, 250);
        setResizable(false);
        setModal(true);
        setHeadingText("User Program Statistics");

        setWidget(new UserProgramStatusPanel(UserInfo.getInstance().getUid(), UserInfo.getInstance().getViewCount(), UserInfo.getInstance()
                .getTutorInputWidgetStats()));

        setVisible(true);
    }
}
