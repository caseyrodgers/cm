package hotmath.gwt.cm_search.client.view;


import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ListItem;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_search.client.CatchupMathSearch;
import hotmath.gwt.cm_search.client.places.SearchPlace;

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

public class TopicViewImpl extends AbstractPagePanel implements TopicView {
    
    Presenter presenter;
    PrescriptionSessionData lessonData;
    
    @UiField
    HeadingElement lessonTitle;
    
    @UiField
    HTMLPanel resourceList;
    
    @UiField
    DivElement correctImage;
    
    
    @UiField
    Button newSearch;
    
    public TopicViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        resourceList.add(listItems);
        listItems.addStyleName("touch");
    }

    interface MyUiBinder extends UiBinder<Widget, TopicViewImpl> {
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
        lessonTitle.setInnerHTML(lesson);
        
        rppItems.clear();
        
        for(PrescriptionSessionDataResource resource: lessonData.getInmhResources()) {
            
            if(resource.getType().equals("results")) {
                continue;
            }
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
    
    @Override
    public String getTitle() {
        String programName = SharedData.getUserInfo().getTestName();
        return " Current Topic for " + programName;
    }


    List<MyGenericTextTag> rppItems = new ArrayList<MyGenericTextTag>();
    
    @UiHandler("newSearch")
    protected void handleOnChoose(ClickEvent ce) {
        CatchupMathSearch.__clientFactory.getPlaceContainer().goTo(new SearchPlace(""));
    }

    @Override
    public void showTopic(String topicFile) {
        // TODO Auto-generated method stub
        
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
