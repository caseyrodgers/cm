package hotmath.gwt.solution_editor.client;

import com.google.gwt.user.client.ui.Frame;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;


public class ShowHelpWindow extends CmWindow {
    
    public ShowHelpWindow() {
        setHeading("Solution Editor Help");
        setSize(640,480);
        
        setResizable(false);
        addCloseButton();
        Frame frame = new Frame("/gwt-resources/solution_editor/help.html");
        frame.setSize("640px", "480px");
        add(frame);
        setVisible(true);
    }

}
