package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.Frame;


public class ShowHelpWindow extends GWindow {
    
    public ShowHelpWindow() {
        super(true);
        setHeadingText("Solution Editor Help");
        setPixelSize(640,480);
        
        setResizable(false);
        Frame frame = new Frame("/gwt-resources/solution_editor/help.html");
        frame.setSize("640px", "480px");
        add(frame);
        setVisible(true);
    }

}
