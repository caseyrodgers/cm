package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile3.client.event.ShowWorkViewEvent;
import hotmath.gwt.cm_mobile3.client.view.QuizView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_mobile_shared.client.util.QuestionBox;
import hotmath.gwt.cm_mobile_shared.client.util.QuestionBox.CallBack;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQuizCurrentResultAction;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class QuizActivity implements QuizView.Presenter {


    List<Integer> testQuestionAnswers;

    private com.google.gwt.event.shared.EventBus eventBus;

    public QuizActivity(com.google.gwt.event.shared.EventBus eventBus) {
        setQuizTrigger(this);
        this.eventBus = eventBus;
    }

    @Override
    public void prepareQuizView(final QuizView quizView) {

        eventBus.fireEvent(new SystemIsBusyEvent(true));
        
        quizView.clearQuizHtml();
        
        /** if the initial quiz, then the quiz html will
         *  be directly available in the login return.
         * 
         */
        final QuizHtmlResult intialQuizResult=SharedData.getFlowAction().getQuizResult();
        if(intialQuizResult != null) {
            /** execute in timer to make sure dom is ready
             *  to be read by external quiz JS.
             */
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    processQuizResult(quizView, intialQuizResult);
                    eventBus.fireEvent(new SystemIsBusyEvent(false));
                }
            });
        }
        else {
            GetQuizHtmlAction action = new GetQuizHtmlAction(SharedData.getUserInfo().getTestId());
            CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<QuizHtmlResult>() {
                public void onSuccess(QuizHtmlResult result) {
                    processQuizResult(quizView, result);
                    eventBus.fireEvent(new SystemIsBusyEvent(false));
                }
                @Override
                public void onFailure(Throwable caught) {
                    Log.error("Error preparing quiz view", caught);
                    MessageBox.showError(caught.getMessage());
                    eventBus.fireEvent(new SystemIsBusyEvent(false));
                }
            });
        }
    }
    
    private void processQuizResult(QuizView quizView, QuizHtmlResult result) {
        
        testQuestionAnswers = result.getAnswers();
        
        /** update global user data
         *  TODO: create abstraction for this data
         */
        CatchupMathMobileShared.__instance.user.setTestId(result.getTestId());
        
        quizView.setQuizHtml(result.getQuizHtml(), testQuestionAnswers.size());
        
        /** mark the correct selections */
        CmList<RpcData> al = result.getCurrentSelections(); 
        for(RpcData rd: al) {
            setSolutionQuestionAnswerIndex(rd.getDataAsString("pid"),rd.getDataAsString("answer"));
        }
        
        
        enableQuizToolbarButtons();
    }
    
    private  native void enableQuizToolbarButtons() /*-{
    try {
        var td = $doc.getElementById("testset_div");
        if(td) {
            var questHeads = td.getElementsByTagName("div");
            for(var qh=0,qt=questHeads.length;qh<qt;qh++) {
                if(questHeads[qh].className == 'question_head') {
                   var d=questHeads[qh];
                   d.innerHTML = d.innerHTML + "<a class='show_work_icon' href='#' onclick='showWhiteboardActive(this);'><img src='/gwt-resources/images/icon_show_work.png'/></a>";
                }
            }
        }
    }
    catch(e) {
           alert(e);
     }
    }-*/;

    @Override
    public void checkQuiz() {
        
        QuestionBox.askYesNoQuestion("Check Quiz?","Are you sure you are ready to check this quiz?", new CallBack() {
            @Override
            public void onSelectYes() {
                eventBus.fireEvent(new SystemIsBusyEvent(true));

                CmMobileUser user = CatchupMathMobileShared.getUser();
                CreateTestRunAction checkTestAction = new CreateTestRunAction(user.getTestId(), user.getUserId());
                CatchupMathMobileShared.getCmService().execute(checkTestAction, new AsyncCallback<CreateTestRunResponse>() {
                    @Override
                    public void onSuccess(CreateTestRunResponse result) {
                        eventBus.fireEvent(new SystemIsBusyEvent(false));
                        
                        /** Transfer results into global shared data instance
                         *  used to encapsulate all 'meta' data access. Used as
                         *  a super DAO, which high level access methods.
                         */
                        CmProgramFlowAction nextAction = result.getNextAction();
                        SharedData.setFlowAction(nextAction);
                        SharedData.getUserInfo().setRunId(result.getRunId());
                        
                        new QuizCheckInfoDialog(eventBus,result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        eventBus.fireEvent(new SystemIsBusyEvent(false));
                        MessageBox.showError("Error checking quiz: " + caught.getMessage());
                        Log.error("Error checking quiz", caught);
                    }
                });
            }
        });
    }

    @Override
    public void showWork() {
        eventBus.fireEvent(new ShowWorkViewEvent());
    }
    
    
    /** Setup method that will call a global method that will set the selected
     *  guess for the given question.
     *  
     * @param pid
     * @param which
     */
    private native void setSolutionQuestionAnswerIndex(String pid, String which) /*-{
         try {
             $wnd.setSolutionQuestionAnswerIndex(pid,which);
         }
         catch(x) {
             alert("setSolutionQuestionAnswerIndex" + x);
         }
    }-*/;

    
    boolean _isOffline=false;
    MultiActionRequestAction answerAction = new MultiActionRequestAction();
    
    
    /** Called by external JS JSNI
     * 
     * @param sQuestionIndex
     * @param answerIndex
     * @param pid
     * @return
     */
    public String questionGuessChanged_Gwt(String sQuestionIndex, String answerIndex, String pid) {
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        final int correctIndex = testQuestionAnswers.get(Integer.parseInt(sQuestionIndex));
        Boolean isCorrect = correctIndex == Integer.parseInt(answerIndex);        
        SaveQuizCurrentResultAction action = new SaveQuizCurrentResultAction(SharedData.getUserInfo().getTestId(), isCorrect, Integer.parseInt(answerIndex), pid);

        if(_isOffline) {
            answerAction.getActions().add(action);
        }
        else {
            /** save in real time */
            CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<RpcData>() {
                @Override
                public void onSuccess(RpcData arg0) {
                    /** saved */
                    eventBus.fireEvent(new SystemIsBusyEvent(false));
                }
                @Override
                public void onFailure(Throwable ex) {
                    Log.error("Error saving question guess", ex);
                    eventBus.fireEvent(new SystemIsBusyEvent(false));                }
            });
        }
        answerAction.getActions().add(action);
        
        return "";
    }
    
    private void showWhiteboard_Gwt(String pid) {
        eventBus.fireEvent(new ShowWorkViewEvent(pid));
    }
    
    String _activePid;
    private void setQuizQuestionActive(String pid) {
        _activePid = pid;
    }
    
    /** Glue code between external HM testset code and GWT.
     * 
     * @param x
     */
    private native void setQuizTrigger(QuizActivity x)/*-{
        $wnd.questionGuessChanged_Gwt = function (question, answer, pid) {
            x.@hotmath.gwt.cm_mobile3.client.activity.QuizActivity::questionGuessChanged_Gwt(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(question,answer,pid);
        };

        
        $wnd.showWhiteboard_Gwt = function (pid) {
            x.@hotmath.gwt.cm_mobile3.client.activity.QuizActivity::showWhiteboard_Gwt(Ljava/lang/String;)(pid);
        };
        
        $wnd.setQuizQuestionActive = function (pid) {
            x.@hotmath.gwt.cm_mobile3.client.activity.QuizActivity::setQuizQuestionActive(Ljava/lang/String;)(pid);
        };


    }-*/;
    

}
