package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.activity.LessonView;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class LessonViewImpl extends Composite implements LessonView {

    private Presenter presenter;
    FlowPanel _main = new FlowPanel();
    private String lessonName;
    private String lessonHtml;
    
    public LessonViewImpl() {
        _main.add(new HTML("Loading lesson text ..."));
        initWidget(_main);
        addStyleName("prescriptionLessonResourceReviewImpl");
    }

    @Override
    public String getViewTitle() {
        return lessonName;
    }

    @Override
    public String getBackButtonText() {
        return "Back";
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
    public BackAction getBackAction() {
        return null;
    }

    @Override
    public void isNowActive() {
    }

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.ASSIGNMENT;
    }

    @Override
    public void setPresenter(Presenter pres, CallbackOnComplete callback) {
        this.presenter = pres;
        pres.loadLesson(this, callback);
    }

    @Override
    public void loadLesson(String lessonName, String lessonHtml) {
        this.lessonName = lessonName;
        this.lessonHtml = lessonHtml;
        _main.clear();
        _main.add(new HTML(lessonHtml));
    }
}
