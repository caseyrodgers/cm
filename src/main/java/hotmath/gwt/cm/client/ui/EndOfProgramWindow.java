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
        setHeading("Program Completed!");
        String html = "<p style='padding: 15px;'>Congratulations! You have completed this program.  You are welcome to continue until you are assigned another program.</p>";
        add(new Html(html));
        setSize(300,150);
        addCloseButton();
        setVisible(true);
    } 
}