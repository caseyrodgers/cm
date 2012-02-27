package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


public class PrescriptionLessonListingViewImpl extends AbstractPagePanel implements  PrescriptionLessonListingView {
    
    @UiField
    HTMLPanel lessonLising;
    
    @UiField 
    Button segmentCompleteTop, segmentCompleteBottom;
    
    @UiField
    Element onlyShowForProfPrograms;
    
    @UiField
    Element buttonBarBottom, buttonBarTop;
    
    interface MyUiBinder extends UiBinder<Widget, PrescriptionLessonListingViewImpl> {
    }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    TouchClickHandler<String> touchHandler = new TouchClickHandler<String>() {
        @Override
        public void touchClick(TouchClickEvent<String> event) {
            presenter.loadLesson(((MyGenericTextTag2)event.getTarget()).sessionNum);
        }
    };


    public PrescriptionLessonListingViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        addStyleName("prescriptionLessonListingViewImpl");
    }


    Presenter presenter;
    
    @Override
    public String getTitle() {
        return "Available Lesson Topics";
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
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        
        presenter.setupView(this);
    }
    
    @Override
    public void hideInfoAboutNextQuiz() {
        onlyShowForProfPrograms.setInnerHTML("");
    }

    @Override
    public void showNextQuizButton(boolean trueFalse) {
        if(!trueFalse) {
            buttonBarTop.setAttribute("style","display:none");
            buttonBarBottom.setAttribute("style","display:none");
        }
        else {
            buttonBarTop.setAttribute("style","display:block");
            buttonBarBottom.setAttribute("style","display:block");
        }
    }
    
    boolean _isComplete;
    
    @Override
    public void setLessonListing(List<SessionTopic> lessons) {
        
        _isComplete = true;
        
        listItems.clear();
        listItems.addStyleName("touch");
        for(int i=0,t=lessons.size();i<t;i++) {
            MyGenericTextTag2 tt = new MyGenericTextTag2(lessons.get(i),presenter.getStatusForLesson(lessons.get(0).getTopic()),i,touchHandler);
            listItems.add(tt);
            
            if(!lessons.get(i).isComplete()) {
                _isComplete = false;
            }
        }
        lessonLising.add(listItems);
        
        
        if(_isComplete) {
            segmentCompleteTop.getElement().removeClassName("disabled");
            segmentCompleteBottom.getElement().removeClassName("disabled");
        }
        else {
            segmentCompleteTop.getElement().addClassName("disabled");
            segmentCompleteBottom.getElement().addClassName("disabled");
        }
    }
    
    
    @UiHandler("segmentCompleteTop")
    protected void onSegmentCompleteTop(ClickEvent ce) {
        onSegmentComplete();
    }
    @UiHandler("segmentCompleteBottom")
    protected void onSegmentCompleteBottom(ClickEvent ce) {
        onSegmentComplete();
    }    
    
    private void onSegmentComplete() {
        if(_isComplete) {
            presenter.moveToNextSegment();
        }
        else {
            MessageBox.showMessage("You will need to finish all required problems first.");
        }
        
    }
}



class MyGenericTextTag2 extends GenericTextTag<String> {
    int sessionNum;
    public MyGenericTextTag2(SessionTopic topic, String status, int sessionNum,TouchClickHandler<String> touchHandler) {
        super("li");
        this.sessionNum = sessionNum;
        addStyleName("group");
        addHandler(touchHandler);
        

        String topicLabel = topic.getTopicStatus();
        
        
        /** if current topic, then check current counts
         *  since they might have changed since last DB
         *  read.
         *  
         */
        if(SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getCurrSession().getTopic().equals(topic.getTopic())) {
            topicLabel = SharedData.calculateCurrentLessonStatus(topic.getTopic());
        }
        
        String text=null;
        if(topic.isComplete()) {
            text = "<img src='/gwt-resources/images/check_correct.png'/> " + topic.getTopic();
        }
        else {
            text = topic.getTopic();
        }
        
        text += " (" + topicLabel + ")";
        
        getElement().setInnerHTML("<span>" + text + "</span>");
    }
}
