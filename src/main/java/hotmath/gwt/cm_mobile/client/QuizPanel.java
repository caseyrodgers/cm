package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile.client.rpc.CreateTestRunMobileAction;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQuizCurrentResultAction;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuizPanel extends Composite {
    
    MultiActionRequestAction answerAction = new MultiActionRequestAction();

    /** define interface to the widget
     */
    interface QuizPanelBinder extends UiBinder<Widget, QuizPanel> {}

    /** Have GWT create an instance of the Binder interface/
     * 
     */
    private static QuizPanelBinder uiBinder = GWT.create(QuizPanelBinder.class);
    
    
    /** bind to the main panel */
    @UiField VerticalPanel mainPanel;
    @UiField Button checkTest;

    List<Integer> testQuestionAnswers;
    
    public QuizPanel() {
        
        /** do the binding */
        initWidget(uiBinder.createAndBindUi(this));
        mainPanel.add(new HTML("<h2>Quiz loading ...</h2>"));
        
        setQuizTrigger(this);
        
        getQuiz();
    }
    
    @UiHandler("checkTest")
    void handleClick(ClickEvent e) {
        CmMobileUser user = CatchupMathMobile.__instance.user;
        CreateTestRunMobileAction checkTestAction = new CreateTestRunMobileAction(user,answerAction);
        CatchupMathMobile.getCmService().execute(checkTestAction, new AsyncCallback<PrescriptionSessionResponse>() {
            @Override
            public void onSuccess(PrescriptionSessionResponse result) {
                CatchupMathMobile.getUser().setPrescripion(result.getPrescriptionData());
                
                History.newItem("lesson");
            }

            @Override
            public void onFailure(Throwable caught) {
                mainPanel.remove(0);
                caught.printStackTrace();               
                mainPanel.add(new HTML("<div style='color: red'><h1>Error Occurred</h2>" + caught.getMessage() + "</div>"));
            }
        });
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
    

    private void getQuiz() {
        CmMobileUser user = CatchupMathMobile.__instance.user;
        
        GetQuizHtmlAction action = new GetQuizHtmlAction(user.getUserId(),user.getTestId(), user.getTestSegment());
        CatchupMathMobile.getCmService().execute(action, new AsyncCallback<QuizHtmlResult>() {
            @Override
            public void onSuccess(QuizHtmlResult result) {
                mainPanel.remove(0);
                mainPanel.add(new HTML(result.getQuizHtml()));
                testQuestionAnswers = result.getAnswers();
                
                CatchupMathMobile.__instance.user.setTestId(result.getTestId());
                
                /** mark the correct selections */
                CmList<RpcData> al = result.getCurrentSelections(); 
                for(RpcData rd: al) {
                    setSolutionQuestionAnswerIndex(rd.getDataAsString("pid"),rd.getDataAsString("answer"));
                }                
            }

            @Override
            public void onFailure(Throwable caught) {
                mainPanel.remove(0);
                caught.printStackTrace();               
                mainPanel.add(new HTML("<div style='color: red'><h1>Error Occurred</h2>" + caught.getMessage() + "</div>"));
            }
        });
    }

    /** Called by external JS JSNI
     * 
     * @param sQuestionIndex
     * @param answerIndex
     * @param pid
     * @return
     */
    public String questionGuessChanged_Gwt(String sQuestionIndex, String answerIndex, String pid) {
        CmMobileUser user = CatchupMathMobile.__instance.user;
        final int correctIndex = testQuestionAnswers.get(Integer.parseInt(sQuestionIndex));
        Boolean isCorrect = correctIndex == Integer.parseInt(answerIndex);        
        SaveQuizCurrentResultAction action = new SaveQuizCurrentResultAction(user.getTestId(), isCorrect, Integer.parseInt(answerIndex), pid);
        answerAction.getActions().add(action);
        
        return "";
    }
    
    /** set the QuizPanel that will be called when a question is answered
     * 
     * @param x
     */
    private native void setQuizTrigger(QuizPanel x)/*-{
        $wnd.questionGuessChanged_Gwt = function (question, answer, pid) {
            x.@hotmath.gwt.cm_mobile.client.QuizPanel::questionGuessChanged_Gwt(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(question,answer,pid);
        };

    }-*/;
    
//    
//    static {
//        publishNative();
//    }
//    static private native void publishNative() /*-{
//      $wnd.checkQuiz_Gwt = @hotmath.gwt.cm_tools.client.ui.QuizPage::checkQuiz_Gwt();
//    }-*/;
}