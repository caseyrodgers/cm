package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.ui.CmProgramFlowClientManager;
import hotmath.gwt.cm.client.ui.StandardFlowCallback;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.util.GenericVideoPlayerForMona;
import hotmath.gwt.cm_tools.client.util.GenericVideoPlayerForMona.MonaVideo;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;

/** Shown after a user has completed a complete program segment
 *
 * @author casey
 *
 */
public class PassedSectionWindow extends CmWindow {

    public PassedSectionWindow() {
        addStyleName("passed-section-window");
        String msg = null;
        if(UserInfo.getInstance().isCustomProgram()) {
            msg = "<p>You completed this section!</p>";
        }
        else {
            msg = "<p>You passed this section!</p>";
        }
        msg += "<p>You will now be shown the next quiz.</p>";
        setHeading("Congratulations");

        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN, this));

        setClosable(false);
        setModal(true);
        add(new Html(msg));
        setResizable(false);
        addButton(
            new Button("Congratulations Video", new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    new GenericVideoPlayerForMona(MonaVideo.PASS_QUIZ);
                }
            }));

        addButton(new Button("Continue",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                
                CmProgramFlowClientManager.getNextActiveProgramState(new StandardFlowCallback() {
                    @Override
                    public void programFlow(CmProgramFlowAction flowResponse) {
                        super.programFlow(flowResponse);
                        
                        close();
                    }
                });
            }
        }));


        addWindowListener(new WindowListener() {
            @Override
            public void windowHide(WindowEvent we) {
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED, this));
            }
        });

        setVisible(true);
    }
}
