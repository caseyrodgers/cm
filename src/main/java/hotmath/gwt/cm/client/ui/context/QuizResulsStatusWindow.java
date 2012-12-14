package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.HTML;

public class QuizResulsStatusWindow extends GWindow {
    
    public QuizResulsStatusWindow(int correct, int total) {
        super(false);
        addStyleName("quiz-results-status-window");
        setWidth(525);
        setHeight(400);
        
        setModal(true);
        String html = "<div class='head'></div>" +
                      "<div class='content'>" +
                      correct + " out of " + total +
                      "</div>" +
                      "<div class='footer'></div>";

        add(new HTML(html));
        setHeadingText("Quiz Results");
    }

}
