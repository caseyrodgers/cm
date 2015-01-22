package hotmath.gwt.cm_mobile3.client.view;


import hotmath.gwt.cm_core.client.BackAction;
import hotmath.gwt.cm_mobile3.client.event.HandleNextFlowEvent;
import hotmath.gwt.cm_mobile_shared.client.HasWhiteboard;
import hotmath.gwt.cm_mobile_shared.client.ListItem;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.google.gwt.user.client.ui.HTMLPanel;

public class SearchLessonViewImpl extends PrescriptionLessonViewImpl implements SearchLessonView, HasWhiteboard{

    private hotmath.gwt.cm_mobile3.client.view.SearchLessonView.Presenter myPresenter;

    public SearchLessonViewImpl() {
        buttonBar.setAttribute("style", "display: none");        
    }
    
    
    @Override
    public void setPresenter(hotmath.gwt.cm_mobile3.client.view.SearchLessonView.Presenter presenter) {
        this.myPresenter = presenter;
        presenter.setupView(this);
    }

    @Override
    public void showWhiteboard(String pid) {
    }

    @Override
    public void loadLesson(PrescriptionSessionData data) {
        setLesson(data);
    }

    
    
    @Override
    public String getHeaderBackground() {
        return "#7F2909"; 
    }


    
    @Override
    public void setLesson(PrescriptionSessionData lessonData) {
        
        fixupLessonData(lessonData);
        
        
        listItems.clear();
        this.lessonData = lessonData;
        
        String lesson = lessonData.getTopic();
        lessonTitle.setInnerHTML(lesson);
        
        rppItems.clear();
        
        for(PrescriptionSessionDataResource resource: lessonData.getInmhResources()) {
            
            switch(resource.getType()) {
                case ACTIVITY:
                case CMEXTRA:
                case WEBLINK:
                case WEBLINK_EXTERNAL:
                case RESULTS:
                        continue;
                        
                default: 
                    break;
            }
            
            if(resource.getItems().size() == 0) {
                continue;
            }
            
            
            ListItem li = new ListItem();
            li.setStyleName("resourceType");
            li.add(new HTMLPanel("<b>" + resource.getLabel() + "</b>"));
            listItems.add(li);
            
            boolean isRpp = resource.getType() == CmResourceType.PRACTICE?true:false;
            
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
    
    public void fixupLessonData(PrescriptionSessionData lessonData) {
        for(PrescriptionSessionDataResource resource: lessonData.getInmhResources()) {
            for(InmhItemData item: resource.getItems()) {
                if(item.getType() == CmResourceType.PRACTICE) {
                }
            }
        }
    }
    
    @Override
    protected void loadResource(InmhItemData resourceItem) {
        myPresenter.loadResource(resourceItem);        
    }
    
    @Override
    public String getViewTitle() {
        return "Search Resources";
    }
    
    @Override
    public String getBackButtonText() {
        return "Back";
    }

    @Override
    public BackAction getBackAction() {
        return new BackAction() {
            @Override
            public boolean goBack() {
                return true;
            }
        };
    }

}
