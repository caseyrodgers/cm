package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.Frame;
import com.sencha.gxt.widget.core.client.ContentPanel;

public class AssignmentGuideWindow extends GWindow {
    static AssignmentGuideWindow __instance;    
    ContentPanel main = new ContentPanel();
    public AssignmentGuideWindow() {
        super(true);
        //setAutoHeight(true);
        setPixelSize(640,480);
        setModal(true);
        setResizable(false);
        setHeadingText("Catchup Math Assignments Guide");
        Frame frame = new Frame("/gwt-resources/cm-admin-assignments-guide.html");
        frame.setSize("100%", "450px");
        main.add(frame);
        setWidget(main);
        setVisible(true);
    }
    public static void showWindow() {
        if(__instance == null) {
            __instance = new AssignmentGuideWindow();
        }
        __instance.setVisible(true);
    }

}
