package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.event.ShowWorkViewEvent;
import hotmath.gwt.cm_mobile3.client.view.QuizView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQuizCurrentResultAction;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
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
        
        quizView.clearQuizHtml();
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        GetQuizHtmlAction action = new GetQuizHtmlAction(CatchupMathMobileShared.getUser().getBaseLoginResponse().getUserInfo().getTestId());
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<QuizHtmlResult>() {
            public void onSuccess(QuizHtmlResult result) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                
                testQuestionAnswers = result.getAnswers();
                
                CatchupMathMobileShared.__instance.user.setTestId(result.getTestId());
                
                quizView.setQuizHtml(result.getQuizHtml(), testQuestionAnswers.size());
                
                /** mark the correct selections */
                CmList<RpcData> al = result.getCurrentSelections(); 
                for(RpcData rd: al) {
                    setSolutionQuestionAnswerIndex(rd.getDataAsString("pid"),rd.getDataAsString("answer"));
                }
                
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error preparing quiz view", caught);
                MessageBox.showError(caught.getMessage());
                eventBus.fireEvent(new SystemIsBusyEvent(false));
            }
        });
    }

    @Override
    public void checkQuiz() {
        MessageBox.showMessage("Checking Quiz");
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
                 alert(x);
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
        CmMobileUser user = CatchupMathMobileShared.__instance.user;
        final int correctIndex = testQuestionAnswers.get(Integer.parseInt(sQuestionIndex));
        Boolean isCorrect = correctIndex == Integer.parseInt(answerIndex);        
        SaveQuizCurrentResultAction action = new SaveQuizCurrentResultAction(user.getTestId(), isCorrect, Integer.parseInt(answerIndex), pid);

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
    
    /** Glue code between external HM testset code and GWT.
     * 
     * @param x
     */
    private native void setQuizTrigger(QuizActivity x)/*-{
        $wnd.questionGuessChanged_Gwt = function (question, answer, pid) {
            x.@hotmath.gwt.cm_mobile3.client.activity.QuizActivity::questionGuessChanged_Gwt(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(question,answer,pid);
        };

    }-*/;
    
    
    static public void checkQuiz_Gwt() {
        Window.alert("IN GWT: checking quiz");
    }
  
  static {
      publishNative();
  }
  static private native void publishNative() /*-{
    $wnd.checkQuiz_Gwt = @hotmath.gwt.cm_mobile3.client.activity.QuizActivity::checkQuiz_Gwt();
  }-*/;    
}
