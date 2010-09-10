package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.PrescriptionPage;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetMobileLessonInfoAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.MobileLessonInfo;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
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
    
    TopicViewPage page;
    public TopicViewPagePanel(TopicViewPage page) {
        this.page = page;
        
        /** do the binding */
        initWidget(uiBinder.createAndBindUi(this));
        
        mainPanel.add(new HTML("Loading lesson ..."));
        mainPanel.setStyleName("topic-view-page-panel");

        
        loadLessonData(page.getTopicFile());
        //loadPrescriptionSession(prescription.getCurrSession());
        //mainPanel.add(listItems);  
    }
    
    
    private void loadLessonData(String file) {
        GetMobileLessonInfoAction action = new GetMobileLessonInfoAction(file);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<MobileLessonInfo>() {
            @Override
            public void onSuccess(MobileLessonInfo lessonInfo) {
                CatchupMathMobileShared.getUser().setPrescripion(lessonInfo.getPresData());
                loadPrescriptionSession(lessonInfo.getPresData().getCurrSession().getInmhResources());
            }
            
            @Override
            public void onFailure(Throwable arg0) {
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
            if(type.equals("video") || type.startsWith("flashcards") || type.equals("activity") || type.equals("results"))
                continue;
            
            ListItem li = new ListItem();
            listItems.add(li);
            li.setText(r.getLabel());
            if(r.getItems().size() > 0) {
                UnOrderedList ol2 = new UnOrderedList();
                li.add(ol2);
                for(int ordinal=0;ordinal<r.getItems().size();ordinal++) {
                    InmhItemData i = r.getItems().get(ordinal);
                    ListItem li2 = new ListItem();
                    // li2.add(new ResourceButton(ordinal, i));
                    li2.add(new ResourceButton(ordinal, i));
                    ol2.add(li2);
                }
            }
        }
    }
    
    static class ResourceButton extends Button {
        InmhItemData item;
        int ordinal;
        public ResourceButton(final int ordinal, final InmhItemData item) {
            super(item.getTitle());
            this.ordinal = ordinal;
            this.item = item;
            setWidth("200px");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent arg0) {
                    History.newItem("resource:" + item.getType() + ":" + ordinal);
                }
            });
        }
    }
}

