package hotmath.gwt.cm_mobile.client.page;

import hotmath.gwt.cm_mobile.client.AbstractPagePanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class QuizPagePanel extends AbstractPagePanel {
	
	QuizPage quizPage;
	public QuizPagePanel(QuizPage quizPage) {
		FlowPanel fp = new FlowPanel();
		fp.add(new HTML("Quiz Panle"));
		
		initWidget(fp);
	}

}
