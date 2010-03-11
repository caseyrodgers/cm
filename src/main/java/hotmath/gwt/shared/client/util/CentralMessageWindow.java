package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CentralMessage;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CheckForCentralMessagesAction;
import hotmath.gwt.shared.client.rpc.action.CmList;

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
public class CentralMessageWindow extends CmWindow {

    static final int CHECK_FOR_MESSAGES_EVERY = 1000 * 60 * 15;
    static CentralMessageWindow _theWindow;
    CmList<CentralMessage> _messages;
    private CentralMessageWindow(CmList<CentralMessage> messages) {
        this._messages = messages;
        _theWindow = this;
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        
        setSize(300,200);
        setResizable(true);
        setClosable(true);
        setModal(true);
        setClosable(false);
        setHeading("Server Message");

        setStyleName("central-message-window");
        String msg = "";
        for(CentralMessage m: messages) {
            msg += "<li>" + m.getMessage() + "</li>";
        }
        msg = "<ul>" + msg + "</ul>";
        
        msg = "<div style='font-size: 110%;padding: 10px;'>" 
            + "<h1>Important Server Message</h1>"
            + msg 
            + "</div>";
        add(new Html(msg));
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                setMessagesAsRead();
                _theWindow = null;
                close();
            }
        }));
        setVisible(true);
    }
    
    private void setMessagesAsRead() {
        new RetryAction<CmList<CentralMessage>>() {
            @Override
            public void attempt() {
                CheckForCentralMessagesAction action = new CheckForCentralMessagesAction(UserInfo.getInstance().getUid(),_messages);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            @Override
            public void oncapture(CmList<CentralMessage> messages) {
                /* empty */
            }
        }.register();
    }
    
    /** Monitor messages by calling server every N seconds and looks
     *  for messages this user has not already seen
     *  
     */
    static public void monitorMessages() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                checkForMessages();
            }
        };
        timer.scheduleRepeating(CHECK_FOR_MESSAGES_EVERY);
        
        /** check now */
        checkForMessages();
    }
    
    
    /** Call server and look for new messages.
     * 
     * Only call server if window is not currently being displayed.
     */
    static private void checkForMessages() {
        if(_theWindow != null)
            return;
        
        new RetryAction<CmList<CentralMessage>>() {
            @Override
            public void attempt() {
                CheckForCentralMessagesAction action = new CheckForCentralMessagesAction(UserInfo.getInstance().getUid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            @Override
            public void oncapture(CmList<CentralMessage> messages) {
                if(messages.size() > 0) {
                    new CentralMessageWindow(messages);
                }
            }
        }.register();
    }
    
}
