package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;

public class QuizPage extends LayoutContainer {
	
	
	String _title;   // @TODO: move into model

	static {
        publishNative();
	}
	
	
	CmAsyncRequest callbackWhenComplete;
	public QuizPage(CmAsyncRequest callbackWhenComplete) {
		this.callbackWhenComplete = callbackWhenComplete;
		setStyleName("quiz-panel");
		//setLayout(new FitLayout());
		getQuizHtmlFromServer();
	}
	
	
    /** Push a GWT method onto the global space for the app window
     * 
     *   This wil be called from CatchupMath.js:questionGuessChanged
     *   which is called after each guess selection.
     *   
     */
    static private native void publishNative() /*-{
                                    $wnd.questionGuessChanged_Gwt = @hotmath.gwt.cm_tools.client.ui.QuizPage::questionGuessChanged_Gwt(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
                                    $wnd.checkQuiz_Gwt = @hotmath.gwt.cm_tools.client.ui.QuizPage::checkQuiz_Gwt();
                                    }-*/;



    /** Call Check Test from external JS 
     * 
     */
    static private void checkQuiz_Gwt() {
        ContextController.getInstance().doNext();
    }
    
    /** Setup method that will call a global method that will set the selected
     *  guess for the given question.
     *  
     * @param pid
     * @param which
     */
    private native void setSolutionQuestionAnswerIndex(String pid, String which) /*-{
         $wnd.setSolutionQuestionAnswerIndex(pid,which);
    }-*/;

    /** JSNI call to mark all correct answers on test
     * 
     */
    private native void markAllCorrectAnswers() /*-{
         $wnd.markAllCorrectAnswers();
    }-*/;
    

    
    private native void markAllCorrectAnswers2() /*-{
    $wnd.markAllCorrectAnswers();
}-*/;
    
    /**
     * Expose the following method into JavaScript.
     *
     * This will be an entry point for the external JS to call into Gwt
     * call when a testset selection is changed.
     *
     * @param correct If the selection is correct
     * @param answerIndex the index into array of options
     * @param the pid of this test question.
     */
    static public String questionGuessChanged_Gwt(String correct, String answerIndex, String pid) {
        
        
        if(!UserInfo.getInstance().isActiveUser()) {
            CatchupMathTools.showAlert("You are just a visitor here, please do not change any selections");
            return "OK";
        }
        
        // Boolean isCorrect = (o != null && o.equals("Correct")) ? true : false;
    	Boolean isCorrect = (correct != null && correct.equals("Correct")) ? true : false;
    	int testId = UserInfo.getInstance().getTestId();
    	
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.saveQuizCurrentResult(UserInfo.getInstance().getTestId(), isCorrect, Integer.parseInt(answerIndex), pid, new AsyncCallback() {
			public void onSuccess(Object result) {
				// CatchupMathTools.showAlert("Question change saved");
        	}
        	public void onFailure(Throwable caught) {
        		CatchupMathTools.showAlert(caught.getMessage());
        	}
        });
        return "OK";
    }
    
    
    
    /** Display the Quiz Html.
     * 
     * Mark all the currently selected questions.
     * 
     * @param quizHtml
     */
	@SuppressWarnings("unchecked")
	private void displayQuizHtml(String quizHtml) {
	    
		Html html = new Html(quizHtml);
		if(CmShared.getQueryParameter("debug") != "") {
		    html.addStyleName("debug-mode");
		}
		add(html);
		if(UserInfo.getInstance().isActiveUser()) {
            Anchor markCorrect = new Anchor("_");
            markCorrect.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent arg0) {
                    markAllCorrectAnswers();
                }
            });
            markCorrect.setStyleName("debug-quiz-mark-all-correct");		
    		add(markCorrect);
		}
		
		CatchupMathTools.setBusy(true);
		
		layout();

		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getQuizCurrentResults(UserInfo.getInstance().getUid(), new AsyncCallback() {
			public void onSuccess(Object result) {
        		ArrayList<RpcData> al = (ArrayList<RpcData>)result;
        		for(RpcData rd: al) {
        			setSolutionQuestionAnswerIndex(rd.getDataAsString("pid"),rd.getDataAsString("answer"));
        		}
        		CatchupMathTools.setBusy(false);		

        		callbackWhenComplete.requestComplete(_title);
        	}
        	public void onFailure(Throwable caught) {
        		String msg;
        		if(caught instanceof CmRpcException) {
        			msg = ((CmRpcException)caught).getMessage();
        		}
        		else {
        			msg = caught.getMessage();
        		}
        		CatchupMathTools.showAlert(msg);
        	}
        });
	}

	/** Read the raw HTML from server and inserts into DOM node
	 * 
	 * Data from server contains JSON meta info and then HTML.  The two
	 * sides are separated by +++.
	 * 
	 * @TODO: find a better way to accommodate a single request combining JSON/XML 
	 */
	private void getQuizHtmlFromServer() {
		
	    CatchupMathTools.setBusy(true);
		
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		s.getQuizHtml(UserInfo.getInstance().getUid(), UserInfo.getInstance().getTestSegment(),new AsyncCallback() {
			public void onSuccess(Object result) {
			    RpcData rdata = (RpcData)result;
				String html = rdata.getDataAsString("quiz_html");
				_title = rdata.getDataAsString("title");
				int testId = rdata.getDataAsInt("test_id");
				int testSegment = rdata.getDataAsInt("quiz_segment");
				UserInfo.getInstance().setTestSegment(testSegment);
 				// update the user info with the title name
				// @TODO: this is a hack ... temp 
				UserInfo.getInstance().setTestName(_title);
				UserInfo.getInstance().setTestId(testId);
				displayQuizHtml(html);
			}
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }
		});
	}
}
