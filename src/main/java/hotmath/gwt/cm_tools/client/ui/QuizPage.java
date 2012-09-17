package hotmath.gwt.cm_tools.client.ui;


import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQuizCurrentResultAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class QuizPage extends FlowLayoutContainer {
	
    static QuizPage __lastInstance;
	
	static List<Integer> testQuestionAnswers;
	QuizHtmlResult _quizInfo;
	
	public QuizPage(boolean loadActive, QuizHtmlResult quizInfo) {
	    __lastInstance = this;
	    setScrollMode(ScrollMode.AUTOY);
		setStyleName("quiz-panel");

		setQuizHtmlResult(quizInfo);

        CmMainPanel.setQuizQuestionDisplayAsActive(CmMainPanel.getLastQuestionPid());
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
	    
	    HTML html = new HTML(quizHtml);
		if(CmShared.getQueryParameter("debug") != "") {
		    html.addStyleName("debug-mode");
		}
		add(html);
        /** reset each displayed quiz */
        questionProcessTracker.finish();
	}


	/** mark the quiz with the user's current selections
	 * 
	 */
	public void markUserAnswers() {

	    initializeQuiz();

	    CmLogger.debug("QuizPage: marking user selections: " + this);
	    
	    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                try {
                    for(RpcData rd: _quizInfo.getCurrentSelections()) {
                        setSolutionQuestionAnswerIndex(rd.getDataAsString("pid"),rd.getDataAsString("answer"));
                    }
                }
                catch(Exception e) {
                    CmLogger.error("Error marking user test answers", e);
                }
            }
        });

	}
	
	private native void initializeQuiz() /*-{
	    $wnd.initializeQuiz();
	}-*/;
	
	/** Read the raw HTML from server and insert into DOM node.
	 *  Also, set the users selections as saved on server.
	 * 
	 */
	private void getQuizHtmlFromServer(final boolean loadActive, final int testSegment) {

	    new RetryAction<QuizHtmlResult>() {
	        
	        @Override
	        public void attempt() {
	            CatchupMathTools.setBusy(true);
	            GetQuizHtmlAction quizAction = new GetQuizHtmlAction(UserInfo.getInstance().getTestId());
	            setAction(quizAction);
	            CmLogger.info("QuizPage.getQuizHtmlFromServer: " + quizAction);
	            CmShared.getCmService().execute(quizAction, this);
	        }
	        
            @Override
            public void oncapture(QuizHtmlResult quizResponse) {
                setQuizHtmlResult(quizResponse);
            }
        }.register();	    
	}
	
	
	private void setQuizHtmlResult(QuizHtmlResult quizResponse) {
	    CatchupMathTools.setBusy(false);

        _quizInfo = quizResponse;
        testQuestionAnswers = _quizInfo.getAnswers();
        UserInfo.getInstance().setProgramSegment(_quizInfo.getQuizSegment());
        UserInfo.getInstance().setTestId(_quizInfo.getTestId());
        UserInfo.getInstance().setRunId(0); /* not in a prescription */
        UserInfo.getInstance().setSubTitle(_quizInfo.getSubTitle());
        
        UserInfo.getInstance().setProgramSegmentCount(_quizInfo.getQuizSegmentCount());
        
        displayQuizHtml(_quizInfo.getQuizHtml());
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                markUserAnswers();            }
        });
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
                    
                    /** TODO: move QuizContext into shared
                     * 
                     * PROBLEM:
                     * We only want to run this code when the QuizPage is active. 
                     * 
                     * So, how do we know that ...?   We can check the context, but
                     * the QuizContext lives in cm module, not available from cm_admin
                     * which shares the QuizPanel.
                     * 
                     * Said another way, we cannot have the cm_shared module depend on 
                     * cm or cm_admin.  
                     * 
                     * 
                     * TODO: Move QuizContext (all contexts) into cm_shared to allow
                     * sharing between cm_admin and cm
                     * 
                     * 
                     * For now, check number of tools to determine ...  this will allow
                     * us to do a hybrid (gui only) build.  
                     * 
                     * 
                     * 
                     */
                    int toolCount = ContextController.getInstance().getTheContext().getTools().size();
                    if(toolCount == 1) {  /** this will NOT WORK! (temporary fix!) */
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