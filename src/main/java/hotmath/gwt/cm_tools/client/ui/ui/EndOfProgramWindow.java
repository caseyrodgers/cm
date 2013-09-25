package hotmath.gwt.cm_tools.client.ui.ui;

import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.util.SystemSyncChecker;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/** Standard dialog to show when users are set to 
 *  stop after completing active program.
 *  
 * @author casey
 *
 */
public class EndOfProgramWindow extends GWindow {

    public static String msg = 
        "<div style='background: white; padding: 20px;'>" +
        "<p>The program that you have been assigned is completed and you have been logged out.</p>" +  
        "<p>Please check with your teacher to discuss your next assignment.</p>" +
        "</div>";
    
    public EndOfProgramWindow() {
        super(false);
        
        setPixelSize(300,175);
        setResizable(false);
        setModal(true);
        
        addStyleName("end-of-program-window");
        setHeadingText("Program Completed!");

        if(CmShared.getQueryParameter("debug") == null) {
            setClosable(false);
        }
        addButton(new TextButton("OK", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                Window.Location.assign("/");            
            }
        }));
        
        setWidget(new HTML(msg));
        setVisible(true);
    } 
    
    
    private TextButton createCheckButton() {
        TextButton btn = new TextButton("Check for Newly Assigned Program");
        btn.setToolTip("Check to see if a new program has been assigned.");
        btn.addSelectHandler(new SelectHandler() {
            public void onSelect(SelectEvent event) {
                InfoPopupBox.display("Check for program assignment", "Checking for new program assignment...");
                SystemSyncChecker.checkForUpdate(true, null);
            }
            
        });
        return btn;
    }
}