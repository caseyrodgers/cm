package hotmath.gwt.cm_activity.client;

import hotmath.gwt.cm_activity.client.model.WordProblem;
import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ExplainPanel extends Composite {
    

    interface MyUiBinder extends UiBinder<Widget, ExplainPanel> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    
    @UiField Button goNext;
    @UiField FlowPanel questionPanel,explainPanel;
    
    public ExplainPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        goNext.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_MOVE_TO_NEXT_QUESTION));
            }
        });
    }
    
    public void setExplaination(WordProblem problem) {
        questionPanel.getElement().setInnerHTML(problem.getQuestion());
        String s = prepareForEval(problem.getVars(),problem.getExplanation());
        explainPanel.getElement().setInnerHTML(s);
        new Timer() {
            @Override
            public void run() {
                goNext.setFocus(true);
            }
        }.schedule(500);        
    }
    
    
    
    /** Call external JS validation code to prepare 
     * the explanation string for display
     * @param str
     * @return
     */
    static private native String prepareForEval(String vars, String str) /*-{
       return $wnd.getExplainString(str,vars);
    }-*/;
}
