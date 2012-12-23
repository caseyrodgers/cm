package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;


public class CmMessageBoxGxt2 {
	
	 /**
     * Display standard message dialog
     * 
     * @param msg
     */
    static public void showAlert(String msg) {
        showAlert("Info", msg);
    }

    static public void showAlert(String title, String msg) {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        MessageBox.alert(title, msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
            }
        });
    }

    static public void showAlert(String title, String msg, final CmAsyncRequest callback) {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        MessageBox.alert(title, msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                if (callback != null)
                    callback.requestComplete(be.getValue());
                
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
            }
        });
    }

    static public void showAlert(String title, String msg, final CmAsyncRequest callback, final String arg) {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        MessageBox.alert(title, msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                if (callback != null)
                    callback.requestComplete(arg);
                
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
            }
        });
    }
}
