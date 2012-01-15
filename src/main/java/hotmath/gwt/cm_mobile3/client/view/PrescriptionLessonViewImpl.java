package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
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

import com.allen_sauer.gwt.log.client.Log;
import com.google.code.gwt.storage.client.Storage;
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
        
        String lesson = lessonData.getTopic();
        lesson += " <span style='font-size: 70%'>" + "(" + SharedData.getCountLessonsRemaining() + " lessons out of " + SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getSessionTopics().size() + " remaining)";
        lessonTitle.setInnerHTML(lesson);
        
        rppItems.clear();
        
        for(PrescriptionSessionDataResource resource: lessonData.getInmhResources()) {
            
            if(resource.getType().equals("activity"))
                continue;
            
            if(resource.getItems().size() == 0) {
                continue;
            }
            
            ListItem li = new ListItem();
            li.setStyleName("resourceType");
            li.add(new HTMLPanel("<b>" + resource.getLabel() + "</b><span> -- " + resource.getDescription() + "</span>"));
            listItems.add(li);
            
            boolean isRpp = resource.getType().equals("practice")?true:false;
            
            int cnt=0;
            for(InmhItemData itemData: resource.getItems()) {
                MyGenericTextTag textTag = new MyGenericTextTag(isRpp, itemData, touchHandler);
                listItems.add(textTag);
                
                if(isRpp) {
                    rppItems.add(textTag);
                }
                
                cnt++;
            }
        }    
        
        refreshRppIndicators();
        indicateIfAllRppsViewed();
    }
    
    private SessionTopic getSessionTopic(String topicName) {
        List<SessionTopic> topics = SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getSessionTopics();
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
                topic.setComplete(true);
            }
            
            
            if(topic.isComplete())
                correctImage.setInnerHTML("Completed");
                correctImage.addClassName("is_complete");            
                lessonTitle.setInnerHTML("<img src='/gwt-resources/images/check_correct.png'/>" + lessonData.getTopic());
            }
            else {
                correctImage.setInnerHTML("");
                correctImage.removeClassName("is_complete");
            }
        }

    
    @Override
    public String getTitle() {
        String programName = SharedData.getUserInfo().getTestName();
        return " Current Topic for " + programName;
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
    public BackAction getBackAction() {
        return new BackAction() {
            @Override
            public boolean goBack() {
                presenter.goBack();
                return false;
            }
        };
        
    }

    List<MyGenericTextTag> rppItems = new ArrayList<MyGenericTextTag>();
    @Override
    public void refreshRppIndicators() {
        for(MyGenericTextTag tt: rppItems) {
            tt.removeStyleName("is_viewed");
            if(tt.resourceItem.isViewed()) {
                if(tt.getStyleName().indexOf("is_viewed") == -1) {
                    tt.addStyleName("is_viewed");
                    tt.getElement().setInnerHTML("<img src='/gwt-resources/images/check_correct.png'/> " + tt.getText());
                }
            }
        }
        indicateIfAllRppsViewed();
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
//        if(false && isRpp) {
//            addStyleName("is_rpp");
//            if(resourceItem.isViewed()) {
//                addStyleName("is_viewed");
//            }
//        }
        
        // add a bit of unbreakable indentation
        getElement().setInnerHTML("<span >&nbsp;&nbsp;&nbsp;<span class='group-item-dot'>&nbsp;</span>" + (!isRpp?resourceItem.getTitle():getResourceTitle(resourceItem)) + "</span>");
    }
    
    private String getResourceTitle(InmhItemData itemData) {
        
        String title = null;
        Storage storage = Storage.getLocalStorage();
        if(storage != null) {
            String tag = "mc_" + itemData.getFile();
            String data = storage.getItem(tag);
            if(data != null) {
                String p[] = data.split("\\|");
                if(p.length == 2) {
                    try {
                        int probNum = Integer.parseInt(p[0]);
                        
                        String tit2 = " <span class='ps-complete'> " + probNum + " completed</span>";
                        String tit = itemData.getTitle();
                        int pos = tit.indexOf(")");
                        if(pos > -1) {
                            tit = tit.substring(0, pos) + ", " + tit2 + ")";
                        }
                        else {
                            tit = tit2;
                        }
                        title = tit; 
                    }
                    catch(Exception e) {
                        Log.error("Error parsing tutor context", e);
                    }
                }
            }
        }
        
        if(title == null) {
            return itemData.getTitle();
        }
        else {
            return title;
        }
    }
}
