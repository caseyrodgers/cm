package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;




/** Auto Advanced information dialog.
 * 
 *  Shown when user is automatically advanced
 *  to the next user program.
 *  
 * @author casey
 *
 */
public class AutoAdvancedProgramWindow extends GWindow {
    

    public AutoAdvancedProgramWindow(String assignedName) {
        super(false);
        setModal(true);
        setHeight(255);
        setWidth(350);
        setClosable(false);
        setResizable(false);
        setStyleName("auto-assignment-window");
        String msg = "<p>You are now enrolled in: <br/><b>" + assignedName + "</b><br/> "
                + "You will be able to move ahead quickly to more advanced programs as you pass the quizzes.</p>";

        HTML html = new HTML(msg);

        setHeadingText("Quiz results");
        add(html);

        TextButton close = new TextButton("OK", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                /**
                 * force a page refresh to load newly changed program.
                 * Otherwise, things are out of sync with server.
                 */
                CmShared.reloadUser();
            }
        });

        addButton(close);
        setVisible(true);
    }
}
