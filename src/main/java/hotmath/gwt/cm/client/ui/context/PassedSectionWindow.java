package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.StandardFlowCallback;
import hotmath.gwt.cm_core.client.flow.CmProgramFlowClientManager;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/** Shown after a user has completed a complete program segment
 *
 * @author casey
 *
 */
public class PassedSectionWindow extends GWindow {

    public PassedSectionWindow() {
        super(false);
        addStyleName("passed-section-window");
        String msg = null;
        if(UserInfo.getInstance().isCustomProgram()) {
            msg = "<p>You completed this section!</p>";
        }
        else {
            msg = "<p>You passed this section!</p>";
        }
        msg += "<p>You will now be shown the next quiz.</p>";
        setHeadingText("Congratulations");

        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN, this));

        setPixelSize(375,200);
        setClosable(false);
        setModal(true);
        add(new HTML(msg));
        setResizable(false);

        addButton(new TextButton("Continue",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                CmProgramFlowClientManager.getNextActiveProgramState(new StandardFlowCallback() {
                    @Override
                    public void programFlow(CmProgramFlowAction flowResponse) {
                        super.programFlow(flowResponse);
                        
                        /** Only close dialog after complete round trip to server */
                        close();
                    }
                });
            }
        }));


        addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED, this));
            }
        });

        setVisible(true);
    }
}
