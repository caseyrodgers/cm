package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Timer;

/** Auto Advanced information dialog.
 * 
 *  Shown when user is automatically advanced
 *  to the next user program.
 *  
 * @author casey
 *
 */
public class AutoAdvancedProgramWindow extends CmWindow {
    

    public AutoAdvancedProgramWindow(String assignedName) {
        setModal(true);
        setHeight(255);
        setWidth(350);
        setClosable(false);
        setResizable(false);
        setStyleName("auto-assignment-window");
        String msg = "<p>You are now enrolled in: <br/><b>" + assignedName + "</b><br/> "
                + "You will be able to move ahead quickly to more advanced " + "programs as you pass the quizzes.</p>";

        Html html = new Html(msg);

        setHeading("Quiz results");
        add(html);

        Button close = new Button();
        close.setText("OK");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {

                /**
                 * force a page refresh to load newly changed program.
                 * Otherwise, things are out of sync with server.
                 */
                CatchupMath.reloadUser();
            }
        });
        
        if(UserInfo.getInstance().isAutoTestMode()) {
            new Timer() {
                @Override
                public void run() {
                    CatchupMath.reloadUser();
                }
            }.schedule(1000);
        }

        addButton(close);
        setVisible(true);
    }
}
