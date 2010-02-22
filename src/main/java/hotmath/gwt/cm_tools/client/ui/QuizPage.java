package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetQuizCurrentResultsAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlAction;
import hotmath.gwt.shared.client.rpc.action.SaveQuizCurrentResultAction;
import hotmath.gwt.shared.client.rpc.result.QuizHtmlResult;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class QuizPage extends LayoutContainer {
	
	
	String _title;   // @TODO: move into model

	static {
        publishNative();
	}
	
	
	CmAsyncRequest callbackWhenComplete;
	public QuizPage(boolean loadActive, CmAsyncRequest callbackWhenComplete) {
	    
	    setScrollMode(Scroll.AUTOY);
	    
		this.callbackWhenComplete = callbackWhenComplete;
		setStyleName("quiz-panel");
		getQuizHtmlFromServer(loadActive);
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
    alert('sending: ' + pid + ', ' + which);
         //$wnd.setSolutionQuestionAnswerIndex(pid,which);
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
    static public String questionGuessChanged_Gwt(final String correct, final String answerIndex, final String pid) {

        if(!UserInfo.getInstance().isActiveUser()) {
            CatchupMathTools.showAlert("You are just a visitor here, please do not change any selections");
            return "OK";
        }
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                Boolean isCorrect = (correct != null && correct.equals("Correct")) ? true : false;
                CmServiceAsync s = CmShared.getCmService();
                s.execute(new SaveQuizCurrentResultAction(UserInfo.getInstance().getTestId(), isCorrect, Integer.parseInt(answerIndex), pid),this);
            }
            @Override
            public void oncapture(RpcData value) {
                /** do nothing */
            }
        }.attempt();
        return "OK";
    }
    
    
    
    /** Display the Quiz Html.
     * 
     * Mark all the currently selected questions.
     * 
     * @param quizHtml
     */
	private void displayQuizHtml(String quizHtml) {
	    
		Html html = new Html(quizHtml);
		if(CmShared.getQueryParameter("debug") != "") {
		    html.addStyleName("debug-mode");
		}
		add(html);
		layout();

		new RetryAction<CmList<RpcData>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetQuizCurrentResultsAction action = new GetQuizCurrentResultsAction(UserInfo.getInstance().getUid());
                CmShared.getCmService().execute(action,this);
            }
            
            public void oncapture(CmList<RpcData> al) {
                for(RpcData rd: al) {
                    setSolutionQuestionAnswerIndex(rd.getDataAsString("pid"),rd.getDataAsString("answer"));
                }
                CmBusyManager.setBusy(false);
                callbackWhenComplete.requestComplete(_title);
            }
        }.attempt();
	}

	/** Read the raw HTML from server and inserts into DOM node
	 * 
	 * Data from server contains JSON meta info and then HTML.  The two
	 * sides are separated by +++.
	 * 
	 * @TODO: find a better way to accommodate a single request combining JSON/XML 
	 */
	private void getQuizHtmlFromServer(final boolean loadActive) {

	    new RetryAction<QuizHtmlResult>() {
	        
	        @Override
	        public void attempt() {
	            CatchupMathTools.setBusy(true);
	            GetQuizHtmlAction quizAction = new GetQuizHtmlAction(UserInfo.getInstance().getUid(), UserInfo.getInstance().getTestSegment());
	            quizAction.setLoadActive(loadActive);
	            Log.info("QuizPage.getQuizHtmlFromServer: " + quizAction);
	            CmShared.getCmService().execute(quizAction, this);
	        }
	        
            @Override
            public void oncapture(QuizHtmlResult rdata) {
                UserInfo.getInstance().setTestSegment(rdata.getQuizSegment());
                UserInfo.getInstance().setTestId(rdata.getTestId());
                _title = rdata.getTitle();
                UserInfo.getInstance().setTestSegmentCount(rdata.getQuizSegmentCount());
                if(rdata.getUserId() != UserInfo.getInstance().getUid()) {
                    UserInfo.getInstance().setActiveUser(false);
                    UserInfo.getInstance().setUserName("Guest user on account: " + rdata.getUserId());
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USERCHANGED));
                }
                
                displayQuizHtml(rdata.getQuizHtml());
                
                CatchupMathTools.setBusy(false);
            }
        }.attempt();	    
	}
}
