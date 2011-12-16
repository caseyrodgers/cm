package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuizViewImpl extends AbstractPagePanel implements QuizView {
    @UiField
    DivElement quizHtml;
    
    @UiField
    SpanElement questionCount;
    
    @UiField
    HTMLPanel quizView;
    
    @UiField
    Button checkQuizTop, checkQuizBottom, showWorkTop, showWorkBottom;

    Presenter presenter;
    
    public QuizViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    interface MyUiBinder extends UiBinder<Widget, QuizViewImpl> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        presenter.prepareQuizView(this);
    }

    @Override
    public String getTitle() {
        int segNum = SharedData.getUserInfo().getTestSegment();
        int segCount = SharedData.getUserInfo().getProgramSegmentCount();
        String programName = SharedData.getUserInfo().getTestName();
        String user = SharedData.getUserInfo().getUserName();
        String section = "";
        if(!SharedData.getUserInfo().isCustomProgram()) {
            section = " for Section " + segNum + " of " + segCount;
        }
        String title = programName + " Quiz " + section;
        return title;
    }
    @Override
    public String getBackButtonText() {
        return null;
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }

    @Override
    public void setQuizHtml(String html, int count) {
        quizHtml.setInnerHTML(html);
        questionCount.setInnerHTML("" + count);
        quizView.setVisible(true);
    }

    @Override
    public void clearQuizHtml() {
        quizView.setVisible(false);
        quizHtml.setInnerHTML("");
    }
    
    @UiHandler("checkQuizTop")
    protected void doCheckQuizTop(ClickEvent ce) {
        presenter.checkQuiz();
    }

    
    @UiHandler("checkQuizBottom")
    protected void doCheckQuizBottom(ClickEvent ce) {
        presenter.checkQuiz();
    }
    
    @UiHandler("showWorkTop")
    protected void doShowWorkTop(ClickEvent ce) {
        presenter.showWork();
    }

    
    @UiHandler("showWorkBottom")
    protected void doShowWorkBottom(ClickEvent ce) {
        presenter.showWork();
    }
    
}