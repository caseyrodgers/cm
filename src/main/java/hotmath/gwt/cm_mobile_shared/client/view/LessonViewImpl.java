package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_core.client.BackAction;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.activity.LessonView;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class LessonViewImpl extends Composite implements LessonView {

    private Presenter presenter;
    FlowPanel _main = new FlowPanel();
    FlowPanel _body = new FlowPanel();
    private String lessonName;
    static SexyButton _languageButton;
    
    public LessonViewImpl() {
        
        SubToolBar subTb = new SubToolBar();
        
        if(_languageButton == null) {
            _languageButton = new SexyButton("Spanish", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    
                    presenter.loadLesson(LessonViewImpl.this, _languageButton.getText().contains("Spanish"), new CallbackOnComplete() {
                        @Override
                        public void isComplete() {
                            Log.debug("Lesson loaded: " + lessonName);
                        }
                    
                    });
                    
                    if(_languageButton.getText().contains("Spanish")) {
                        _languageButton.setButtonText("English",null);
                    }
                    else {
                        _languageButton.setButtonText("Spanish",null);
                    }
                }
            });
        }
        
        subTb.add(_languageButton);
        
        _main.add(subTb);
        
        _body.add(new HTML("Loading lesson text ..."));
        _main.add(_body);
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
        pres.loadLesson(this, !_languageButton.getText().contains("Spanish"), callback);
    }

    @Override
    public void loadLesson(String lessonName, String lessonHtml) {
        this.lessonName = lessonName;
        _body.clear();
        _body.add(new HTML(lessonHtml));
    }
    
    
    @Override
    public String getHeaderBackground() {
        return null;
    }
}
