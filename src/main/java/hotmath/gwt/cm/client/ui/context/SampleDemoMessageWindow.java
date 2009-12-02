package hotmath.gwt.cm.client.ui.context;

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
        String msg = "<p>Thank you for trying a Catchup Math Pre-algebra Session.</p>  "
                + "<p>Please visit our <a href='http://catchupmath.com/schools.html'>Schools</a>, "
                + "<a href='http://catchupmath.com/colleges.html'>Colleges</a>, "
                + "and <a href='http://catchupmath.com/students.html'>Students</a> pages "
                + "for more information.</p>";
        setClosable(false);
        setStyleName("demo-complete-window");
        setHeight(140);
        setWidth(400);
        setHeading("Thank You");
        setModal(true);
        add(new Html(msg));
        setVisible(true);
    }
}
