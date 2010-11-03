package hotmath.gwt.cm_activity.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

class AnswerTextBox extends TextBox {
    public AnswerTextBox() {
        setTextAlignment(ALIGN_CENTER);
        addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if(event.getCharCode() == 27) {
                    ((TextBox) event.getSource()).cancelKey();
                    EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SHOW_INPUT_RESET));
                }
                if(event.getCharCode() == 13) {
                    EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SHOW_INPUT_SUBMIT));
                }
                else if (event.getCharCode() == '/') {
                    ((TextBox) event.getSource()).cancelKey();

                    EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SHOW_INPUT_FRACTION));
                }
                else if(getText().length() == 0 && event.getCharCode() == 8) {
                    ((TextBox) event.getSource()).cancelKey();
                    EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SHOW_INPUT_STRING_EQUATION));
                }
            }
        });
    }
}