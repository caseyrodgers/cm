package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.widget.Html;

/** Standard dialog to show when users are set to 
 *  stop after completing active program.
 *  
 * @author casey
 *
 */
public class EndOfProgramWindow extends CmWindow {
    public EndOfProgramWindow() {
        this(true);
    }
    
    public EndOfProgramWindow(boolean canContinue) {
        addStyleName("end-of-program-window");
        setHeading("Program Completed!");
        String msg = "<p>Congratulations! You have completed this program.</p>";
        if(canContinue) {
            msg += "<p>You are welcome to continue until you are assigned another program.</p>";
            addCloseButton();
        }
        else {
            msg += "<p>Please wait until you are assigned another program.</p>";
            setClosable(false);
        }
        
        add(new Html("<div style='padding: 15px;'>" + msg + "</div>"));
        setSize(300,175);
        setResizable(false);
        setVisible(true);
    } 
}