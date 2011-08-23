package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.ListItem;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class PrescriptionLessonViewImpl extends AbstractPagePanel implements PrescriptionLessonView {
    Presenter presenter;
    PrescriptionSessionData lessonData;
    
    @UiField
    HeadingElement lessonTitle;
    
    @UiField
    HTMLPanel resourceList;
    
    @UiField
    DivElement correctImage;
    
    @UiField
    Button movePrev, moveNext;
    
    @UiField
    Button choose;
    
    public PrescriptionLessonViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        resourceList.add(listItems);
        listItems.addStyleName("touch");
    }

    interface MyUiBinder extends UiBinder<Widget, PrescriptionLessonViewImpl> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        
        presenter.prepareView(this);
    }
    
    TouchClickHandler<String> touchHandler = new TouchClickHandler<String>() {
        @Override
        public void touchClick(TouchClickEvent<String> event) {
            presenter.loadResource(((MyGenericTextTag)event.getTarget()).resourceItem);
        }
    };
    
    @Override
    public void setLesson(PrescriptionSessionData lessonData) {
        
        listItems.clear();
        this.lessonData = lessonData; 
        lessonTitle.setInnerHTML(lessonData.getTopic());
        
        rppItems.clear();
        
        for(PrescriptionSessionDataResource resource: lessonData.getInmhResources()) {
            ListItem li = new ListItem();
            li.setStyleName("resourceType");
            li.add(new HTMLPanel("<b>" + resource.getLabel() + "</b><span> -- " + resource.getDescription() + "</span>"));
            listItems.add(li);
            
            boolean isRpp = resource.getType().equals("practice")?true:false;
            for(InmhItemData itemData: resource.getItems()) {
                MyGenericTextTag textTag = new MyGenericTextTag(isRpp, itemData, touchHandler);
                listItems.add(textTag);
                
                if(isRpp) {
                    rppItems.add(textTag);
                }
            }
        }    
        
        indicateIfAllRppsViewed();
    }
    
    private SessionTopic getSessionTopic(String topicName) {
        List<SessionTopic> topics = CatchupMathMobileShared.getUser().getFlowAction().getPrescriptionResponse().getPrescriptionData().getSessionTopics();
        for(SessionTopic t: topics) {
            if(t.getTopic().equals(topicName)) {
                return t;
            }
        }
        return null;
    }
    
    private void indicateIfAllRppsViewed() {
        // are all rpps viewed?
        int viewedCount=0;
        for(int i=0,t=rppItems.size();i<t;i++) {
            if(rppItems.get(i).resourceItem.isViewed()) {
                viewedCount++;
            }
        }

        if(viewedCount == rppItems.size()) {
            
            SessionTopic topic = getSessionTopic(lessonData.getTopic());
            if(!topic.isComplete()) {
                /** not currently marked as correct, so update server and
                 *  data structure and label
                 */
                presenter.markLessonAsComplete(topic);
            }
            
            correctImage.setInnerHTML("Completed");
        }
        else {
            correctImage.setInnerHTML("Not Completed");
        }
        
    }
    
    @Override
    public String getTitle() {
        return "Lesson Resources";
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

    List<MyGenericTextTag> rppItems = new ArrayList<MyGenericTextTag>();
    @Override
    public void refreshRppIndicators() {
        for(MyGenericTextTag tt: rppItems) {
            tt.removeStyleName("is_viewed");
            if(tt.resourceItem.isViewed()) {
                tt.addStyleName("is_viewed");
            }
        }
        indicateIfAllRppsViewed();
    }
    
    @UiHandler("moveNext")
    protected void handleMoveNext(ClickEvent ce) {
        presenter.moveToNextLesson(this);
    }
    
    @UiHandler("movePrev")
    protected void handleMovePrev(ClickEvent ce) {
        presenter.moveToPreviousLesson(this);
    }
    
    @UiHandler("choose")
    protected void handleOnChoose(ClickEvent ce) {
        presenter.showLessonChooser();
    }
   
}

class MyGenericTextTag extends GenericTextTag<String> {
    InmhItemData resourceItem;
    public MyGenericTextTag(boolean isRpp, InmhItemData resourceItem,TouchClickHandler<String> touchHandler) {
        super("li");
        this.resourceItem = resourceItem;
        addStyleName("group");
        addHandler(touchHandler);
        if(isRpp) {
            addStyleName("is_rpp");
            if(resourceItem.isViewed()) {
                addStyleName("is_viewed");
            }
        }
        getElement().setInnerHTML("<span>" + resourceItem.getTitle() + "</span>");
    }
}
