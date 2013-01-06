package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.GetQuizResultsHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizResultsMetaInfo;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.viewer.QuizResultsPdfPanel;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplResults;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class ShowQuizResultsDialog extends GWindow {

    int runId;
    FramedPanel mainPan;

    public ShowQuizResultsDialog(int runId) {
        super(true);
        this.runId = runId;
        setPixelSize(550, 480);
        setMaximizable(true);
        setModal(true);
        setHeadingText("Quiz Results");

        mainPan = new FramedPanel();
        mainPan.setHeaderVisible(false);
        CenterLayoutContainer center = new CenterLayoutContainer();
        center.add(new Label("Loading ..."));
        mainPan.setWidget(center);
        setWidget(mainPan);
        addStyleName("resource-viewer-impl-results");
        getResultHtml();

        setVisible(true);
    }

    private void getResultHtml() {
        new RetryAction<QuizResultsMetaInfo>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetQuizResultsHtmlAction action = new GetQuizResultsHtmlAction(runId);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(QuizResultsMetaInfo results) {
                CmBusyManager.setBusy(false);
                showResultsAsHtml(results.getRpcData());
            }
        }.register();

    }

    private void showResultsAsPdf(String pdfUrl) {

        clear();
        setWidget(new QuizResultsPdfPanel(pdfUrl));
        forceLayout();
        setVisible(true);
    }

    private void showResultsAsHtml(RpcData rdata) {

        String html = rdata.getDataAsString("quiz_html");
        String resultJson = rdata.getDataAsString("quiz_result_json");
        String title = rdata.getDataAsString("title");

        setHeadingText("Quiz Results: " + title);

        HTML htmlEl = new HTML(html);
        //htmlEl.getElement().setAttribute("style", "padding-left: 20px;");
        
        FlowLayoutContainer scroller = new FlowLayoutContainer();
        scroller.setScrollMode(ScrollMode.AUTO);
        scroller.add(htmlEl);
        mainPan.setWidget(scroller);
        
        forceLayout();

        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MATHJAX_RENDER));
        hideAnswerResults();
        ResourceViewerImplResults.markAnswers(resultJson);
    }

    /**
     * hide new style of question results
     */
    private native void hideAnswerResults() /*-{
                                            $wnd.hideQuizQuestionResults();
                                            }-*/;

}
