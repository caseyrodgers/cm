package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;

/** Sample complete thank you message.
 * 
 *  Is shown at end of sample session.
 *  
 * @author casey
 *
 */
public class SampleDemoMessageWindow extends Window {

    public SampleDemoMessageWindow() {
        
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN, this));
        
        String msg = "<p>Thank you for trying a Catchup Math Pre-Algebra Session.</p>  "
                + "<p>We offer complete programs for Pre-Algebra, Algebra 1, Geometry, " +
                " Algebra 2 and high school graduation exam prep.</p>"
                + "<p>Please visit our <a href='http://catchupmath.com/schools.html'>Schools</a>, "
                + "<a href='http://catchupmath.com/colleges.html'>Colleges</a>, "
                + "or <a href='http://catchupmath.com/students.html'>Students</a> pages "
                + "for more information.</p>";
        setClosable(false);   // end of the road
        setStyleName("demo-complete-window");
        setHeight(140);
        setWidth(400);
        setHeading("Thank You");
        setModal(true);
        add(new Html(msg));
        setVisible(true);
        
        addWindowListener(new WindowListener() {
            @Override
            public void windowHide(WindowEvent we) {
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED, this));
            }
        });
    }
}
