package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.Frame;

public class RecentUpdatesWindow extends GWindow {

    public  RecentUpdatesWindow() {
    	super(true);
        //setAutoHeight(true);
        setPixelSize(640,500);

        setModal(true);
        setResizable(false);
        //setStyleName("help-window");
        setHeadingText("Catchup Math Recent Updates");

        Frame frame = new Frame("/gwt-resources/cm-admin-recent-updates.html");
        frame.setSize("100%", "450px");

        add(frame);

        setVisible(true);
    }

}

