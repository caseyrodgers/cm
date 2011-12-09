package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Html;

public class SolutionSetCompleteDialog extends CmWindow {
    
    public SolutionSetCompleteDialog(int numCorrect, int limit) {
        
        setHeading("Problem Set Results");
        
        setModal(true);
        setSize(350, 150);
        addStyleName("SolutionSetCompleteDialog");
        String html = 
            "You correctly answered <b style='font-size: 1.5em'> " + numCorrect + "</b> questions out of <b style='font-size: 1.5em'>" + limit + "</b>.";
        
       html = "<p style='text-align: center;margin-top: 10px;' class='solution_set_results'>" + html + "</p>";
       
       add(new Html(html));
       
       addWindowListener(new WindowListener() {
           public void windowHide(com.extjs.gxt.ui.client.event.WindowEvent we) {
               
           }
       });
              
       
       addCloseButton();
       setVisible(true);
    }

}
