package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;

import java.util.List;

import com.google.gwt.core.client.GWT;
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
        
        segmentCompleteTop.setVisible(false);
        segmentCompleteBottom.setVisible(false);
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
    public void setLessonListing(List<SessionTopic> lessons) {
        
        boolean isSegmentComplete = true;
        
        listItems.clear();
        listItems.addStyleName("touch");
        for(int i=0,t=lessons.size();i<t;i++) {
            MyGenericTextTag2 tt = new MyGenericTextTag2(lessons.get(i),i,touchHandler);
            listItems.add(tt);
            
            if(!lessons.get(i).isComplete()) {
                isSegmentComplete = false;
            }
        }
        lessonLising.add(listItems);
        
        
        if(isSegmentComplete) {
            segmentCompleteTop.setVisible(true);
            segmentCompleteBottom.setVisible(true);
        }
        else {
            segmentCompleteTop.setVisible(false);
            segmentCompleteBottom.setVisible(false);
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
        presenter.moveToNextSegment();
    }
}



class MyGenericTextTag2 extends GenericTextTag<String> {
    int sessionNum;
    public MyGenericTextTag2(SessionTopic topic, int sessionNum,TouchClickHandler<String> touchHandler) {
        super("li");
        this.sessionNum = sessionNum;
        addStyleName("group");
        addHandler(touchHandler);
        
        String text=null;
        if(topic.isComplete()) {
            text = "<img src='/gwt-resources/images/check_correct.png'/> " + topic.getTopic();
        }
        else {
            text = topic.getTopic();
        }
        
        getElement().setInnerHTML("<span>" + text + "</span>");
    }
}
