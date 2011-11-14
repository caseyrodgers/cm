package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Html;

public class SolutionSetCompleteDialog extends CmWindow {
    
    public SolutionSetCompleteDialog(int numCorrect, int limit) {
        
        setHeading("Problem Set Results");
        
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN, this));
        
        setModal(true);
        setSize(350, 150);
        addStyleName("SolutionSetCompleteDialog");
        String html = 
            "You correctly answered <b style='font-size: 1.5em'> " + numCorrect + "</b> questions out of <b style='font-size: 1.5em'>" + limit + "</b>.";
        
       html = "<p style='text-align: center;margin-top: 10px;' class='solution_set_results'>" + html + "</p>";
       
       add(new Html(html));
       
       addWindowListener(new WindowListener() {
           public void windowHide(com.extjs.gxt.ui.client.event.WindowEvent we) {
               EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED, this));
           }
       });
              
       
       addCloseButton();
       setVisible(true);
    }

}
