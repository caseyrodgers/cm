package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/** Sample complete thank you message.
 * 
 *  Is shown at end of sample session.
 *  
 * @author casey
 *
 */
public class SampleDemoMessageWindow extends GWindow {

    public SampleDemoMessageWindow() {
        
        super(false);
        setModal(true);
        setPixelSize(400, 200);
        setHeadingText("Thank You");
        setClosable(false);
        setResizable(false);
        addStyleName("demo-complete-window");

        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN, this));
        
        String msg = "<p>Thank you for trying a Catchup Math Pre-Algebra Session.</p>  "
                + "<p>We offer complete programs for Pre-Algebra, Algebra 1, Geometry, " +
                " Algebra 2 and high school graduation exam prep.</p>"
                + "<p>Please visit our <a href='http://catchupmath.com/schools.html'>Schools</a>, "
                + "<a href='http://catchupmath.com/colleges.html'>Colleges</a>, "
                + "or <a href='http://catchupmath.com/students.html'>Students</a> pages "
                + "for more information.</p>";
        
        add(new HTML(msg));
        setVisible(true);
        
        addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED, this));
            }
        });
    }
}
