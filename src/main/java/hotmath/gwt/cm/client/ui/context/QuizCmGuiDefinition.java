package hotmath.gwt.cm.client.ui.context;


import hotmath.gwt.cm.client.data.CmAsyncRequest;
import hotmath.gwt.cm.client.ui.CmContext;
import hotmath.gwt.cm.client.ui.CmGuiDefinition;
import hotmath.gwt.cm.client.ui.ContextController;
import hotmath.gwt.cm.client.ui.QuizPage;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


/**
 * add the quiz resource as the default
 * 
 * @TODO: which quiz?
 */
public class QuizCmGuiDefinition implements CmGuiDefinition {
	
	public String getTitle() {
		return "Quiz Page";
	}

	
	public Widget getWestWidget() {
		String html = 
		"<p>Welcome to Catchup Math.</p> " +
        "<p>Please start by taking " +
        "the quiz to the right.</p> " +
        "<p>Then we will provide " +
        "personalized review and " +
        "practice to get you caught up!</p>";		
		
		Template t = new Template(html);
		HTML ohtml = new HTML(t.getHtml());
		ohtml.setStyleName("quiz-page-resource");
		return ohtml;
	}
	
	public Widget getCenterWidget() {
		QuizPage qp = new QuizPage(new CmAsyncRequest() {
			public void requestComplete(String quizTitle) {
				QuizContext qc = (QuizContext)getContext();
				qc.setTitle(quizTitle);
				ContextController.getInstance().setCurrentContext(qc);
			}
			public void requestFailed(int code, String text) {
			}
		});
		return qp;
	}

	public CmContext getContext() {
		// TODO Auto-generated method stub
		return new QuizContext(this);
	}
}