package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class CustomProgramHelpWindow extends CmWindow {
    public CustomProgramHelpWindow() {
        setSize(400,390);
        setModal(true);
        setMaximizable(true);
        setHeading("Custom Program Help");
        setLayout(new FitLayout());
        add(new CustomProgramHelpBinder());
        addCloseButton();
        setVisible(true);
    }
}
