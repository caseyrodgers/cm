package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.GetQuizResultsHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplResults;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class ShowQuizResultsDialog extends CmWindow {
    
    int runId;
    public ShowQuizResultsDialog(int runId) {
        this.runId = runId;
        setSize(550,480);
        setScrollMode(Scroll.AUTO);
        setHeading("Quiz Results");

        addStyleName("resource-viewer-impl-results");
        setLayout(new FitLayout());
        getResultHtml();
        
        addCloseButton();
        
        setVisible(true);
    }
    
    
    private void getResultHtml() {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetQuizResultsHtmlAction action = new GetQuizResultsHtmlAction(runId);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData rdata) {
                CmBusyManager.setBusy(false);
                
                String html = rdata.getDataAsString("quiz_html");
                String resultJson = rdata.getDataAsString("quiz_result_json");
                int total = rdata.getDataAsInt("quiz_question_count");
                int correct = rdata.getDataAsInt("quiz_correct_count");
                String title = rdata.getDataAsString("title");
                
                setHeading("Quiz Results: " + title);

                Html quizPanel = new Html(html);

                add(quizPanel, new BorderLayoutData(LayoutRegion.CENTER));
                
                layout(true);

                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MATHJAX_RENDER));
                hideAnswerResults();
                ResourceViewerImplResults.markAnswers(resultJson);
            }
        }.register();
        
    }
    
    /** 
     *  hide new style of question results
     */
    private native void hideAnswerResults() /*-{
    $wnd.hideQuizQuestionResults();
}-*/;

}
