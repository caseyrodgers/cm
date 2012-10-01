package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Html;



class ShowHowToUseDialog extends CmWindow {
    public ShowHowToUseDialog() {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        setModal(true);
        setSize(350,200);
        setHeading("How To Use This");
        add(new Html(html));
        addCloseButton();
        addWindowListener(new WindowListener() {
                @Override
                public void windowHide(WindowEvent we) {
                        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
                }
        });
    }
    String html =
        "<div style='padding: 10px 5px;'>" +
         "<p>Work out your answer using pencil and paper or the Whiteboard. " +
         "Some teachers may require this.</p>" +
         "<p>If you see an 'Enter Your Answer' input box, you can enter your " +
         "answer there to check your work.</p>" +
        "</div>";
}
