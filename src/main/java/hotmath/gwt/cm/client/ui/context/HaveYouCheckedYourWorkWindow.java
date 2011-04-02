package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;

public class HaveYouCheckedYourWorkWindow extends CmWindow {

    public HaveYouCheckedYourWorkWindow(final Callback callback) {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        setSize(330, 120);
        setStyleName("quiz-context-msg-window");
        setClosable(false);

        setModal(true);
        setHeading("Ready to Check Quiz?");
        add(new Html("<p style='padding: 15px 10px;'>Did you work out your answers carefully?</p>"));

        addButton(new Button("Yes", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
                callback.quizIsReadyToBeChecked();
            }
        }));

        addButton(new Button("No", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
                close();
            }
        }));

        setVisible(true);
    }
    
    static public interface Callback {
        void quizIsReadyToBeChecked();
    }
}
