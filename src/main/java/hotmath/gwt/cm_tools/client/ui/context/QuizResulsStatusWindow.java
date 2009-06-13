package hotmath.gwt.cm.client.ui.context;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;

public class QuizResulsStatusWindow extends Window {
    
    public QuizResulsStatusWindow(int correct, int total) {
        setStyleName("quiz-results-status-window");
        setWidth(525);
        setHeight(400);
        
        setModal(true);
        String html = "<div class='head'></div>" +
                      "<div class='content'>" +
                      correct + " out of " + total +
                      "</div>" +
                      "<div class='footer'></div>";

        add(new Html(html));
        setHeading("Quiz Results");
    }

}
