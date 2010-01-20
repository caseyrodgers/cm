package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetQuizCurrentResultsAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlAction;
import hotmath.gwt.shared.client.rpc.action.SaveQuizCurrentResultAction;
import hotmath.gwt.shared.client.rpc.result.QuizHtmlResult;
import hotmath.gwt.shared.client.util.CmAsyncCallback;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QuizPage extends LayoutContainer {
	
	
	String _title;   // @TODO: move into model

	static {
        publishNative();
	}
	
	
	CmAsyncRequest callbackWhenComplete;
	public QuizPage(CmAsyncRequest callbackWhenComplete) {
	    
	    setScrollMode(Scroll.AUTOY);
	    
		this.callbackWhenComplete = callbackWhenComplete;
		setStyleName("quiz-panel");
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
    static public native void markAllCorrectAnswers() /*-{
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
    	
    	CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
    	s.execute(new SaveQuizCurrentResultAction(UserInfo.getInstance().getTestId(), isCorrect, Integer.parseInt(answerIndex), pid), new AsyncCallback<RpcData>() {
			public void onSuccess(RpcData result) {
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
	    
	    CmBusyManager.setBusy(true, false);
	    
		Html html = new Html(quizHtml);
		if(CmShared.getQueryParameter("debug") != "") {
		    html.addStyleName("debug-mode");
		}
		add(html);
		layout();

		/** @TODO: move to cmService
		 * 
		 */
		CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
		GetQuizCurrentResultsAction action = new GetQuizCurrentResultsAction(UserInfo.getInstance().getUid());
        s.execute(action, new CmAsyncCallback<CmList<RpcData>>() {
			public void onSuccess(CmList<RpcData> al) {
        		for(RpcData rd: al) {
        			setSolutionQuestionAnswerIndex(rd.getDataAsString("pid"),rd.getDataAsString("answer"));
        		}
        		CmBusyManager.setBusy(false);
        		callbackWhenComplete.requestComplete(_title);
        	}
        	public void onFailure(Throwable caught) {
        	    CatchupMathTools.setBusy(false);
        	    super.onFailure(caught);
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
		
	    GetQuizHtmlAction quizAction = new GetQuizHtmlAction(UserInfo.getInstance().getUid(), UserInfo.getInstance().getTestSegment());
	    UserInfo.getInstance().setTestId(0);
	    
	    Log.info("QuizPage.getQuizHtmlFromServer: " + quizAction);
		CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
		
		s.execute(quizAction, new CmAsyncCallback<QuizHtmlResult>() {
		    @Override
		    public void onSuccess(QuizHtmlResult rdata) {
                UserInfo.getInstance().setTestSegment(rdata.getQuizSegment());
                
                _title = rdata.getTitle();

                UserInfo.getInstance().setTestId(rdata.getTestId());
                UserInfo.getInstance().setTestSegmentCount(rdata.getQuizSegmentCount());
                
                if(rdata.getUserId() != UserInfo.getInstance().getUid()) {
                    UserInfo.getInstance().setActiveUser(false);
                    UserInfo.getInstance().setUserName("Guest user on account: " + rdata.getUserId());
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USERCHANGED));
                }
                
                displayQuizHtml(rdata.getQuizHtml());
                
                CatchupMathTools.setBusy(false);
		    }
		    @Override
		    public void onFailure(Throwable caught) {
                CatchupMathTools.setBusy(false);
                super.onFailure(caught);
		    }
        });
		
	}
}
