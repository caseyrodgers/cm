package hotmath.gwt.cm_tools.client.ui.ui;

import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.util.SystemSyncChecker;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Window;

/** Standard dialog to show when users are set to 
 *  stop after completing active program.
 *  
 * @author casey
 *
 */
public class EndOfProgramWindow extends CmWindow {

    public static String msg = 
        "<div style='background: white; padding: 20px;'>" +
        "<p>The program that you have been assigned is completed and you have been logged out.</p>" +  
        "<p>Please check with your teacher to discuss your next assignment.</p>" +
        "</div>";
    
    public EndOfProgramWindow() {
        addStyleName("end-of-program-window");
        setHeading("Program Completed!");
        setModal(true);
        

        
        setClosable(false);
        
        if(CmShared.getQueryParameter("debug") == null) {
            setClosable(false);
        }
        
        add(new Html(msg));
        
        addButton(new Button("OK", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Window.Location.assign("/");
            }
        }));
        setSize(300,175);
        setResizable(false);
        setVisible(true);
    } 
    
    
    private Button createCheckButton() {
        
        Button btn = new Button("Check for Newly Assigned Program");
        btn.setToolTip("Check to see if a new program has been assigned.");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                InfoPopupBox.display("Check for program assignment", "Checking for new program assignment...");
                SystemSyncChecker.checkForUpdate();
            }
        });
        
        return btn;
    }
}