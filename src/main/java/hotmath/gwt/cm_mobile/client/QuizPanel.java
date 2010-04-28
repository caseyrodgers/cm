package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
    
    
    public QuizPanel() {
        
        /** do the binding */
        initWidget(uiBinder.createAndBindUi(this));
        mainPanel.add(new HTML("<h2>Quiz loading ...</h2>"));
        
        getQuiz();
    }
    

    private void getQuiz() {
        MobileUser user = CatchupMathMobile.__instance.user;
        
        GetQuizHtmlAction action = new GetQuizHtmlAction(user.uid,user.testId, user.testSegment);
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
}