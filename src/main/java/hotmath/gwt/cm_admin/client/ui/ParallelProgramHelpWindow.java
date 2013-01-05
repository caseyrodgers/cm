package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;
import com.sencha.gxt.widget.core.client.FramedPanel;

public class ParallelProgramHelpWindow extends GWindow {

    public  ParallelProgramHelpWindow() {
        super(true);
        setPixelSize(550,380);

        setModal(true);
        setResizable(false);
        setStyleName("help-window");
        setHeadingText("Parallel Program Help");
        
        FramedPanel fp = new FramedPanel();
        fp.setHeaderVisible(false);

        Frame frame = new Frame("/gwt-resources/parallel-program-help.html");
        DOM.setElementProperty(frame.getElement(), "frameBorder", "no"); // disable
        DOM.setElementPropertyInt(frame.getElement(), "border", 0); // disable
        DOM.setElementPropertyInt(frame.getElement(), "frameSpacing", 0); // disable
        frame.setSize("100%", "300px");
        
        fp.setWidget(frame);

        setWidget(fp);

        setVisible(true);
    }
}

