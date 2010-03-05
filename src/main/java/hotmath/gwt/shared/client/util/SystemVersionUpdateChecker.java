package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.ui.FooterPanel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CatchupMathVersionInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetCatchupMathVersionAction;
import hotmath.gwt.shared.client.rpc.result.CatchupMathVersion;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Timer;

/** Display a message from central server
 * 
 * @author casey
 *
 */
public class SystemVersionUpdateChecker extends CmWindow {

    static final int CHECK_EVERY = 1000 *  15;// 60 * 15;
    static SystemVersionUpdateChecker _theWindow;
    
    private SystemVersionUpdateChecker() {
        _theWindow = this;
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        
        setSize(300,200);
        setResizable(true);
        setClosable(true);
        setModal(true);
        setClosable(false);
        setHeading("Catchup Math Update");

        String html = "We have recently updated Catchup Math.  " + 
                      "Please Refresh your browser by pressing the F5 key or click the button below. " +
                      "Thank you for using Catchup Math!";
        
        setStyleName("server-update-window");
        
        String msg = "<div style='font-size: 110%;padding: 10px;'>" 
            + html 
            + "</div>";
        add(new Html(msg));
        addButton(new Button("Refresh Page", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                _theWindow = null;
                FooterPanel.refreshPage();
            }
        }));
        setVisible(true);
    }
    
    /** Monitor messages by calling server every N seconds and looks
     *  for messages this user has not already seen
     *  
     */
    static public void monitorVersionChanges() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                checkForUpdate();
            }
        };
        timer.scheduleRepeating(CHECK_EVERY);
        
        /** check now */
        checkForUpdate();
    }
    
    
    /** Call server and look for new messages.
     * 
     * Only call server if window is not currently being displayed.
     */
    static private void checkForUpdate() {
        if(_theWindow != null)
            return;
        new RetryAction<CatchupMathVersion>() {
            @Override
            public void attempt() {
                GetCatchupMathVersionAction action = new GetCatchupMathVersionAction();
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            @Override
            public void oncapture(CatchupMathVersion version) {
                Log.debug("GetCatchupMathVersionAction: " + version.getVersion() + " current: " + CatchupMathVersionInfo.getBuildVersion());
                if(version.getVersion() != CatchupMathVersionInfo.getBuildVersion()) {
                    new SystemVersionUpdateChecker();
                }
            }
        }.attempt();
    }
}
