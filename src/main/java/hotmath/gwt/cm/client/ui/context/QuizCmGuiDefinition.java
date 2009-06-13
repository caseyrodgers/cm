package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmGuiDefinition;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.QuizPage;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.util.UserInfo;
import hotmath.gwt.shared.client.data.CmAsyncRequest;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
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
	    
	    LayoutContainer cp = new LayoutContainer();
	    cp.setStyleName("quiz-page-resource");
	    String html="";
	    if(UserInfo.getInstance().getTestName().indexOf("Auto-Enrollment") > -1) {
	        html = 
	            "<p>Take the quiz to the right.</p>" +
	            "<p>Afterwards, you may get one or two more" +
	            "   quizzes before starting your review and practice. " +
	            "</p>" +
	            "<p>Most of the quiz questions require pencil " +
	            "   and paper. Please don't guess." +
	            "</p>" +
	            "<p>If you log out, your answers will be saved." +
	            "</p> ";
	    }
	    else {
            html = "<h1>HOW TO USE CATCHUP MATH</h1>" +
                   "<p>Take the 10-question quiz to the right.</p>" +
                   "<p>Then, we will provide you with personalized review and practice.</p>" +
                   "<p>Most of the quiz questions require pencil and paper. Please don't guess.</p> ";
	    }
	    cp.add(new Html(html));
	    cp.add(new Html(CatchupMathTools.FEEDBACK_MESSAGE));
		return cp;
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