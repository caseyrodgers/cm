package hotmath.gwt.cm_tools.client.ui;



import hotmath.gwt.cm.client.ui.context.QuizContext;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQuizCurrentResultAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class QuizPage extends LayoutContainer {
	
    static QuizPage __lastInstance;
	
	CmAsyncRequest callbackWhenComplete;
	static List<Integer> testQuestionAnswers;
	QuizHtmlResult _quizInfo;
	
	public QuizPage(boolean loadActive, CmAsyncRequest callbackWhenComplete) {
	    __lastInstance = this;
	    setScrollMode(Scroll.AUTOY);
		this.callbackWhenComplete = callbackWhenComplete;
		setStyleName("quiz-panel");
		getQuizHtmlFromServer(loadActive);
	}

	
	/** Called when user makes an answer selection 
	 * 
	 * @param action
	 */
	public void saveQuestionSelection(SaveQuizCurrentResultAction action) {
	    /** replace entry, or add new
	     * 
	     */
	    boolean found=false;
	    for(int i=0,t=_quizInfo.getCurrentSelections().size();i<t;i++) {
	        RpcData rdata = _quizInfo.getCurrentSelections().get(i);
	        if(rdata.getDataAsString("pid").equals(action.getPid())) {
	            rdata.putData("answer", action.getAnswerIndex());
	            found=true;
	            break;
	        }
	    }
	    if(!found) {
            RpcData nrdata = new RpcData("pid=" + action.getPid() + ",answer=" + action.getAnswerIndex());
            _quizInfo.getCurrentSelections().add(nrdata);
	    }
	}

    
    /** Call external JS global method that will set the selected
     *  guess for the given question.
     *  
     * @param pid
     * @param which
     */
    private native void setSolutionQuestionAnswerIndex(String pid, String which) /*-{
         // alert($wnd.document.getElementById);
         $wnd.setSolutionQuestionAnswerIndex(pid,which);
    }-*/;
    private native void setSolutionQuestionAnswerIndexByNumber(int questionNumber, String which) /*-{
        $wnd.setSolutionQuestionAnswerIndexByNumber(questionNumber,which);
    }-*/;

    public void markAllAnswersCorrect() {
    	questionProcessTracker.beginStep();
        for(int i=0,t=testQuestionAnswers.size();i<t;i++) {
            setSolutionQuestionAnswerIndexByNumber(i, testQuestionAnswers.get(i).toString());
        }
        questionProcessTracker.completeStep();
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

        callbackWhenComplete.requestComplete(_quizInfo.getTitle());
        
        CmMainPanel.setQuizQuestionDisplayAsActive(CmMainPanel.getLastQuestionPid());
        
        /** reset each displayed quiz */
        questionProcessTracker.finish();
	}

	/** mark the quiz with the user's current selections
	 * 
	 */
	public void markUserAnswers() {
	    CmLogger.debug("QuizPage: marking user selections: " + this);
	    DeferredCommand.addCommand(new Command() {
            @Override
            public void execute() {
                try {
                    for(RpcData rd: _quizInfo.getCurrentSelections()) {
                        setSolutionQuestionAnswerIndex(rd.getDataAsString("pid"),rd.getDataAsString("answer"));
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
	}
	
	/** Read the raw HTML from server and insert into DOM node.
	 *  Also, set the users selections as saved on server.
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
	            CmLogger.info("QuizPage.getQuizHtmlFromServer: " + quizAction);
	            CmShared.getCmService().execute(quizAction, this);
	        }
	        
            @Override
            public void oncapture(QuizHtmlResult rdata) {
                CatchupMathTools.setBusy(false);
                _quizInfo = rdata;
                testQuestionAnswers = rdata.getAnswers();
                
                UserInfo.getInstance().setTestSegment(rdata.getQuizSegment());
                UserInfo.getInstance().setTestId(rdata.getTestId());
                UserInfo.getInstance().setSubTitle(rdata.getSubTitle());
                
                UserInfo.getInstance().setTestSegmentCount(rdata.getQuizSegmentCount());
                
                if(rdata.getUserId() != UserInfo.getInstance().getUid()) {
                    UserInfo.getInstance().setActiveUser(false);
                    UserInfo.getInstance().setUserName("Guest user on account: " + rdata.getUserId());
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USERCHANGED));
                }
                displayQuizHtml(rdata.getQuizHtml());
                DeferredCommand.addCommand(new Command() {
                    @Override
                    public void execute() {
                        markUserAnswers();
                    }
                });
            }
        }.register();	    
	}
	
    
	static private MyProcessTracker questionProcessTracker = new MyProcessTracker();
	static public boolean isAnsweringQuestions() {
		return questionProcessTracker.isBusy();
	}
	
    static {
        publishNative();
        
        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_RESOURCE_CONTAINER_REFRESH) {
                    CmContext context = ContextController.getInstance().getTheContext();
                    if(context instanceof QuizContext) {
                        /** mark questions only if Quiz is current context */
                        __lastInstance.markUserAnswers();
                    }
                }
                else if(event.getEventType() == EventType.EVENT_TYPE_QUIZ_QUESTION_SELECTION_CHANGED) {
                    __lastInstance.saveQuestionSelection((SaveQuizCurrentResultAction)event.getEventData());
                }
            }
        });
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
        
        /** track before inserting into request que */
    	questionProcessTracker.beginStep();
        
        final int questionIndex = Integer.parseInt(sQuestionIndex);
        final int answerChoice = Integer.parseInt(answerIndex);
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                final int correctIndex = testQuestionAnswers.get(questionIndex);
                Boolean isCorrect = correctIndex == answerChoice;
                SaveQuizCurrentResultAction action = new SaveQuizCurrentResultAction(UserInfo.getInstance().getTestId(), isCorrect, answerChoice, pid);
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_QUIZ_QUESTION_SELECTION_CHANGED,action));
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            @Override
            public void oncapture(RpcData value) {
            	/** clear after return from server */
            	questionProcessTracker.completeStep();
            }
        }.register();
        return "OK";
    }
}

class MyProcessTracker implements ProcessTracker {
	int depth;
	
	public boolean isBusy() {
		return depth!=0;
	}

	@Override
	public void beginStep() {
		depth++;
	}

	@Override
	public void completeStep() {
		depth--;
	}

	@Override
	public void finish() {
		depth=0;
	}	
}