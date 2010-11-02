package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile2.client.CatchupMathMobile2;
import hotmath.gwt.cm_mobile2.client.CatchupMathMobile2.Callback;
import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.page.PrescriptionPage;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
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
        
        CatchupMathMobile2.loadLessonData(page.getTopicFile(),new Callback() {
            @Override
            public void isComplete(Object data) {
                pData = (PrescriptionData)data;
                loadPrescriptionSession(pData.getCurrSession().getInmhResources());                
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
                            String lessonFile = page.getTopicFile();
                            History.newItem(new TokenParser(item.getType(), lessonFile, ordinalHolder).getHistoryTag());
                        }
                    });
                    ul.add(resourceLi);
                }
            }
        }
        
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_LESSON_LOADED, pData));
    }
}

