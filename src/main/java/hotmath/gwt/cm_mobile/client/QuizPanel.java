package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
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

        
        HTML html1 = new HTML();
        html1.setHTML("<a href='http://www.google.com'>Click me!</a>");
        mainPanel.add(html1);  /** add to binded panel */
        
        HTML html2 = new HTML();
        html2.setHTML("This is my sample <b>content</b>!");
        mainPanel.add(html2);  /** add second object */
        
        getQuiz();
    }
    

    private void getQuiz() {
        
        GetQuizHtmlAction action = new GetQuizHtmlAction(23562, 20008, 0);
        CatchupMathMobile.getCmService().execute(action, new AsyncCallback<QuizHtmlResult>() {
            @Override
            public void onSuccess(QuizHtmlResult result) {
                mainPanel.add(new HTML(result.getQuizHtml()));
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error getting quiz: " + caught);
            }
        });
    }    
}