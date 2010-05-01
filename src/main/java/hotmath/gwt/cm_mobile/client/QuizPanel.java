package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuizPanel extends Composite {

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
    
    public QuizPanel() {
        
        /** do the binding */
        initWidget(uiBinder.createAndBindUi(this));
        mainPanel.add(new HTML("<h2>Quiz loading ...</h2>"));
        
        setQuizTrigger(this);
        
        getQuiz();
    }
    
    @UiHandler("checkTest")
    void handleClick(ClickEvent e) {
        History.newItem("lesson");
    }    

    private void getQuiz() {
        CmMobileUser user = CatchupMathMobile.__instance.user;
        
        GetQuizHtmlAction action = new GetQuizHtmlAction(user.getUserId(),user.getTestId(), user.getTestSegment());
        CatchupMathMobile.getCmService().execute(action, new AsyncCallback<QuizHtmlResult>() {
            @Override
            public void onSuccess(QuizHtmlResult result) {
                mainPanel.remove(0);
                mainPanel.add(new HTML(result.getQuizHtml()));
            }

            @Override
            public void onFailure(Throwable caught) {
                mainPanel.remove(0);
                caught.printStackTrace();               
                mainPanel.add(new HTML("<div style='color: red'><h1>Error Occurred</h2>" + caught.getMessage() + "</div>"));
            }
        });
    }
    
    public String questionGuessChanged_Gwt(String sQuestionIndex, String answerIndex, final String pid) {
        Window.alert(pid);
        return "";
    }
    
    /** set the QuizPanel that will be called when a question is answered
     * 
     * @param x
     */
    private native void setQuizTrigger(QuizPanel x)/*-{
        $wnd.questionGuessChanged_Gwt = function (question, answer, pid) {
            x.@hotmath.gwt.cm_mobile.client.QuizPanel::questionGuessChanged_Gwt(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
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