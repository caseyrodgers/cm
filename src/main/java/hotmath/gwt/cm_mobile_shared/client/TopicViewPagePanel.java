package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.page.PrescriptionPage;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetMobileLessonInfoAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.MobileLessonInfo;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopicViewPagePanel extends AbstractPagePanel {

    /** define interface to the widget
     */
    interface TopicViewPagePanelBinder extends UiBinder<Widget, TopicViewPagePanel> {}

    /** Have GWT create an instance of the Binder interface/
     * 
     */
    private static TopicViewPagePanelBinder uiBinder = GWT.create(TopicViewPagePanelBinder.class);
    
    
    PrescriptionData prescription;

    /** bind to the main panel */
    
    @UiField VerticalPanel mainPanel;
    
    PrescriptionPage pPage;
    GenericContainerTag listItems = new GenericContainerTag("ul");
    static PrescriptionData pData=null;
    TopicViewPage page;
    public TopicViewPagePanel(TopicViewPage page) {
        this.page = page;
        
        /** do the binding */
        initWidget(uiBinder.createAndBindUi(this));
        
        mainPanel.add(new HTML("<span style='font-weight: bold;font-height: 1.2em;>Loading lesson ...</span>"));
        mainPanel.setStyleName("topic-view-page-panel");
        
        loadLessonData(page.getTopicFile());
        //loadPrescriptionSession(prescription.getCurrSession());
        //mainPanel.add(listItems);  
    }
    
    
    private void loadLessonData(String file) {
        
        if(pData != null && pData.getCurrSession().getInmhResources().get(0).getItems().get(0).getFile().equals(file)) {
            loadPrescriptionSession(pData.getCurrSession().getInmhResources());
            return;
        }
        
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_START));
        GetMobileLessonInfoAction action = new GetMobileLessonInfoAction(file);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<MobileLessonInfo>() {
            @Override
            public void onSuccess(MobileLessonInfo lessonInfo) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                pData = lessonInfo.getPresData();
                CatchupMathMobileShared.getUser().setPrescripion(lessonInfo.getPresData());
                loadPrescriptionSession(lessonInfo.getPresData().getCurrSession().getInmhResources());
            }
            
            @Override
            public void onFailure(Throwable arg0) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                Window.alert(arg0.getMessage());
            }
        });
    }
    
    private void loadPrescriptionSession(List<PrescriptionSessionDataResource> resources) {
    	mainPanel.clear();
    	mainPanel.add(listItems);
    	
    	/** start fresh */
        listItems.clear();
        
        for (PrescriptionSessionDataResource r : resources) {
            
            String type = r.getType();
            
            /** only show mobile ready resource
             * TODO: only return mobile types
             */
            if(type.startsWith("flashcards") || type.equals("activity") || type.equals("results"))
                continue;
            
            if(r.getItems().size() > 0) {
                ListItem li = new ListItem();
                li.add(new HTML("<div class='resource_type'>" + r.getLabel() + "</div>"));
                listItems.add(li);
                
                GenericContainerTag ul = new GenericContainerTag("ul");
                ul.addStyleName("touch");                
                li.add(ul);
                for(int ordinal=0;ordinal<r.getItems().size();ordinal++) {
                    final InmhItemData item = r.getItems().get(ordinal);
                    GenericTextTag<String> resourceLi = new GenericTextTag<String>("li");
                    resourceLi.setStyleName("group");
                    resourceLi.setText(item.getTitle());
                    final int ordinalHolder = ordinal;
                    resourceLi.addHandler(new TouchClickHandler<String>() {
                        @Override
                        public void touchClick(TouchClickEvent<String> event) {
                            String tag = "resource:" + item.getType() + ":" + ordinalHolder + ":" + System.currentTimeMillis();
                            History.newItem(tag);
                        }
                    });
                    ul.add(resourceLi);
                }
            }
        }
    }
    
    static class ResourceButton extends Button {
        InmhItemData item;
        int ordinal;
        public ResourceButton(final int ordinal, final InmhItemData item) {
            super(item.getTitle());
            setStyleName("resource-button");
            this.ordinal = ordinal;
            this.item = item;
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent arg0) {
                    History.newItem("resource:" + item.getType() + ":" + ordinal);
                }
            });
        }
    }
}

