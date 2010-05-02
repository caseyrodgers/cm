package hotmath.gwt.cm_tools.client.ui;



import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQuizCurrentResultAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class QuizPage extends LayoutContainer {
	
	
	String _title;   // @TODO: move into model
	CmAsyncRequest callbackWhenComplete;
	static List<Integer> testQuestionAnswers;
	public QuizPage(boolean loadActive, CmAsyncRequest callbackWhenComplete) {
	    
	    setScrollMode(Scroll.AUTOY);
	    
		this.callbackWhenComplete = callbackWhenComplete;
		setStyleName("quiz-panel");
		getQuizHtmlFromServer(loadActive);
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
    private native void setSolutionQuestionAnswerIndexByNumber(int questionNumber, String which) /*-{
        $wnd.setSolutionQuestionAnswerIndexByNumber(questionNumber,which);
    }-*/;

    public void markAllAnswersCorrect() {
        for(int i=0,t=testQuestionAnswers.size();i<t;i++) {
            setSolutionQuestionAnswerIndexByNumber(i, testQuestionAnswers.get(i).toString());
        }
    }
    
    
    /** Display the Quiz Html.
     * 
     * Mark all the currently selected questions.
     * 
     * @param quizHtml
     */
	private void displayQuizHtml(String quizHtml, CmList<RpcData> selections) {
	    
		Html html = new Html(quizHtml);
		if(CmShared.getQueryParameter("debug") != "") {
		    html.addStyleName("debug-mode");
		}
		add(html);
		layout();

		/** mark the correct selections */
        CmList<RpcData> al = selections; 
        callbackWhenComplete.requestComplete(_title);
        for(RpcData rd: al) {
            setSolutionQuestionAnswerIndex(rd.getDataAsString("pid"),rd.getDataAsString("answer"));
        }
        CmMainPanel.setQuizQuestionDisplayAsActive(CmMainPanel.getLastQuestionPid());
	}

	/** Read the raw HTML from server and inserts into DOM node
	 * 
	 */
	private void getQuizHtmlFromServer(final boolean loadActive) {

	    new RetryAction<QuizHtmlResult>() {
	        
	        @Override
	        public void attempt() {
	            CatchupMathTools.setBusy(true);
	            GetQuizHtmlAction quizAction = new GetQuizHtmlAction(UserInfo.getInstance().getUid(), UserInfo.getInstance().getTestId(), UserInfo.getInstance().getTestSegment());
	            setAction(quizAction);
	            quizAction.setLoadActive(loadActive);
	            Log.info("QuizPage.getQuizHtmlFromServer: " + quizAction);
	            CmShared.getCmService().execute(quizAction, this);
	        }
	        
            @Override
            public void oncapture(QuizHtmlResult rdata) {
                CatchupMathTools.setBusy(false);
                
                testQuestionAnswers = rdata.getAnswers();
                
                UserInfo.getInstance().setTestSegment(rdata.getQuizSegment());
                UserInfo.getInstance().setTestId(rdata.getTestId());
                UserInfo.getInstance().setSubTitle(rdata.getSubTitle());
                
                _title = rdata.getTitle();
                UserInfo.getInstance().setTestSegmentCount(rdata.getQuizSegmentCount());
                
                if(rdata.getUserId() != UserInfo.getInstance().getUid()) {
                    UserInfo.getInstance().setActiveUser(false);
                    UserInfo.getInstance().setUserName("Guest user on account: " + rdata.getUserId());
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USERCHANGED));
                }
                displayQuizHtml(rdata.getQuizHtml(),rdata.getCurrentSelections());
            }
        }.register();	    
	}
	
    
    static {
        publishNative();
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
    
    
    /** JSNI call to mark all correct answers on test
     * 
     */
    static public native void markAllCorrectAnswers() /*-{
         $wnd.markAllCorrectAnswers();
    }-*/;
    

    
    /**
     * Expose the following method into JavaScript.
     *
     * This will be an entry point for the external JS to call 
     * into Gwt when a testset selection is changed.
     *
     * @param correct If the selection is correct
     * @param answerIndex the index into array of options
     * @param the pid of this test question.
     */
    static public String questionGuessChanged_Gwt(String sQuestionIndex, String answerIndex, final String pid) {

        if(!UserInfo.getInstance().isActiveUser()) {
            CatchupMathTools.showAlert("You are just a visitor here, please do not change any selections");
            return "OK";
        }
        
        final int questionIndex = Integer.parseInt(sQuestionIndex);
        final int answerChoice = Integer.parseInt(answerIndex);
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                final int correctIndex = testQuestionAnswers.get(questionIndex);
                Boolean isCorrect = correctIndex == answerChoice;
                SaveQuizCurrentResultAction action = new SaveQuizCurrentResultAction(UserInfo.getInstance().getTestId(), isCorrect, answerChoice, pid);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            @Override
            public void oncapture(RpcData value) {
                /** do nothing */
            }
        }.register();
        return "OK";
    }
}
