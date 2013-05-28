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
    private CallbackOnComplete callback;
    FlowPanel _main = new FlowPanel();
    
    public LessonViewImpl() {
        _main.add(new HTML("Loading lesson text ..."));
        initWidget(_main);
    }

    @Override
    public String getViewTitle() {
        return "Lesson View";
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.ASSIGNMENT;
    }

    @Override
    public void setPresenter(Presenter pres, CallbackOnComplete callback) {
        this.presenter = pres;
        this.callback = callback;
        pres.loadLesson(this, callback);
    }

    @Override
    public void loadLesson(String lessonName, String lessonHtml) {
        _main.clear();
        _main.add(new HTML(lessonHtml));
    }

}
