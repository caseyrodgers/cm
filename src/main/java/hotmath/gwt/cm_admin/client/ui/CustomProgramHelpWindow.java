package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.GWindow;

public class CustomProgramHelpWindow extends GWindow {
    public CustomProgramHelpWindow() {
        super(true);
        setPixelSize(400,390);
        setModal(true);
        setMaximizable(true);
        setHeadingText("Custom Program Help");
        add(new CustomProgramHelpBinder());
        setVisible(true);
    }
}
