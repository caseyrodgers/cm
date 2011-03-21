package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.util.SystemVersionUpdateChecker;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;

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
        setModal(true);
        

        
        String msg = "<p>Congratulations! You have completed this program.</p>";
        if(canContinue) {
            msg += "<p>You are welcome to continue until you are assigned another program.</p>";
            addCloseButton();
        }
        else {
            msg += "<p>Please wait until you are assigned another program.</p>";
            setClosable(false);
            addButton(createCheckButton());
        }
        
        
        
        if(CmShared.getQueryParameter("debug") == null) {
            setClosable(false);
        }
        
        add(new Html("<div style='padding: 5px;'>" + msg + "</div>"));
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
                SystemVersionUpdateChecker.checkForUpdate();
            }
        });
        
        return btn;
    }
}