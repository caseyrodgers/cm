package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.data.CmAsyncRequest;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.util.CmRpcException;
import hotmath.gwt.cm.client.util.RpcData;
import hotmath.gwt.cm.client.util.UserInfo;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;

public class QuizPage extends LayoutContainer {
	
	
	String _title;   // @TODO: move into model

	static {
        publishNative();
	}
	
	
	CmAsyncRequest callbackWhenComplete;
	public QuizPage(CmAsyncRequest callbackWhenComplete) {
		this.callbackWhenComplete = callbackWhenComplete;
		setStyleName("quiz-panel");
		setScrollMode(Scroll.AUTO);
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
                                    $wnd.questionGuessChanged_Gwt = @hotmath.gwt.cm.client.ui.QuizPage::questionGuessChanged_Gwt(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
                                    }-*/;



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
            CatchupMath.showAlert("You are just a visitor here, please do not change any selections");
            return "OK";
        }
        
        // Boolean isCorrect = (o != null && o.equals("Correct")) ? true : false;
    	Boolean isCorrect = (correct != null && correct.equals("Correct")) ? true : false;
    	int testId = UserInfo.getInstance().getTestId();
    	
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.saveQuizCurrentResult(UserInfo.getInstance().getTestId(), isCorrect, Integer.parseInt(answerIndex), pid, new AsyncCallback() {
			public void onSuccess(Object result) {
				// CatchupMath.showAlert("Question change saved");
        	}
        	public void onFailure(Throwable caught) {
        		CatchupMath.showAlert(caught.getMessage());
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
	    
	    
		HTML html = new HTML(quizHtml);
		add(html);

		if(UserInfo.getInstance().isActiveUser()) {
            Anchor markCorrect = new Anchor("mark all corect");
            markCorrect.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent arg0) {
                    markAllCorrectAnswers();
                }
            });
            markCorrect.setStyleName("debug-quiz-mark-all-correct");		
    		add(markCorrect);
		}
		
		CatchupMath.setBusy(true);
		
		layout();

		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getQuizCurrentResults(UserInfo.getInstance().getUid(), new AsyncCallback() {
			public void onSuccess(Object result) {
        		ArrayList<RpcData> al = (ArrayList<RpcData>)result;
        		for(RpcData rd: al) {
        			setSolutionQuestionAnswerIndex(rd.getDataAsString("pid"),rd.getDataAsString("answer"));
        		}
        		CatchupMath.setBusy(false);		

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
        		CatchupMath.showAlert(msg);
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
		
		CatchupMath.setBusy(true);
		
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
