package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.widget.Html;

public class SolutionSetCompleteDialog extends CmWindow {
    
    public SolutionSetCompleteDialog(int numCorrect, int limit) {
        
        setHeading("Problem Set Results");
        
        setModal(true);
        setSize(250, 150);
        addStyleName("SolutionSetCompleteDialog");
        String html = 
            "  <div class='results'>" +
            "    <b>Correct Responses: </b>" +
            "" + numCorrect +
            "  </div>" +
            "  <div >" +
            "    <b>Total Questions: </b>" +
            "" + limit + 
            "  </div>";
            
        
       html = "<p class='solution_set_results'>" + html + "</p>";
       
       add(new Html(html));
       
       addCloseButton();
       setVisible(true);
    }

}
