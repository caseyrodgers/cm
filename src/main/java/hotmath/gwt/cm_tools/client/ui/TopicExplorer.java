package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.ui.resource.CmInmhStandardResources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.info.Info;

public class TopicExplorer extends SimpleContainer {
    
    private Topic topic;
    CmMainPanel _mainPanel;
    private PrescriptionData prescriptionData;
    private TopicExplorerGuiContext _guiContext;
    public TopicExplorer(Topic topic) {
        this.topic = topic;
    
        _guiContext = new TopicExplorerGuiContext(topic);
        _mainPanel = new CmMainPanel(_guiContext);
        CenterLayoutContainer lc = new CenterLayoutContainer();
        lc.setWidget(new HTML("<h1>Loading " + topic.getName() + "</h1>"));
        
        setWidget(lc);

        loadDataFromServer();
    }

    @Override
    public Widget asWidget() {
        return this;
    }
    
    
    Map<String, ResourceMenuButton> resourceButtons = new HashMap<String, ResourceMenuButton>();
    
    private void loadDataFromServer() {
        new RetryAction<PrescriptionSessionResponse>() {
            @Override
            public void attempt() {
                GetTopicPrescriptionAction action = new GetTopicPrescriptionAction(topic.getFile());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            
            @Override
            public void oncapture(PrescriptionSessionResponse prescription) {
                // CmMessageBox.showAlert("Prescription: " + topics);
                setWidget(_mainPanel);
                forceLayout();
                
                buildUi(prescription);
                
            }
        }.register();
        
    }
    
    
    private Widget createFiller() {
        return new HTML("<div class='resource-filler'> &nbsp; </div>");
    }


    protected void buildUi(PrescriptionSessionResponse prescription) {
        _mainPanel.setContextSubTitle("" + "<h2>" + topic.getName() +  "</h2>");
        
        buildUiInternal(prescription.getPrescriptionData());
    }

    static Map<String, List<InmhItemData>> _registeredResources = new HashMap<String, List<InmhItemData>>();
    public void buildUiInternal(PrescriptionData pData) {
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        
        this.prescriptionData = pData;
        List<PrescriptionSessionDataResource> resources = pData.getCurrSession().getInmhResources();

        flow.clear();
        
        //setScrollMode(ScrollMode.NONE);
        flow.addStyleName("prescription-cm-gui-definition-resource-panel");

        boolean isCustomProgram = false;

        ResourceMenuButton.RegisterCallback callback = new ResourceMenuButton.RegisterCallback() {
            @Override
            public void registerItems(String label, List<InmhItemData> items) {
                _registeredResources.put(label, items);
            }
            
            @Override
            public void loadResourceIntoHistory(String label, String item) {
                List<InmhItemData> resources = _registeredResources.get(label);
                Info.display("Load",  "Load resource: " + label + ", " + item);
                InmhItemData resource = resources.get(0);

                _mainPanel.showResource(resource);
                
                forceLayout();
            }
        };

        
        PrescriptionSessionDataResource review=null;
        // setTitle("Choose a resource type, then click one of its items.");
        for (PrescriptionSessionDataResource resource : resources) {
            
            /** do not show EPPs
             * 
             */
            switch(resource.getType()) {
                case CMEXTRA:
                case TESTSET:
                case ACTIVITY_STANDARD:
                case RESULTS:
                    continue;
            }
            
            if(resource.getType() == CmResourceType.WEBLINK) {
                if(resource.getItems() != null && resource.getItems().size() == 1) {
                    resource.setLabel(resource.getItems().get(0).getTitle());
                }
            }
            
            ResourceMenuButton btnResource = new ResourceMenuButton(resource, callback);

            resourceButtons.put(resource.getType().label(), btnResource);
            
            /** if a Custom Program, then make sure the results
             * button is disabled .. there are no quizzes.
             */
            if(resource.getType() == CmResourceType.RESULTS) {
                review = resource;
            }
            else {
                flow.add(btnResource);
                flow.add(createFiller());
            }
            
        }

        //flowLayoutMain.add(vertPanelResources);

        /**
         * Add the standard resources
         * 
         */

//        flow.add(new HTML("<hr class='resource-separator'/>"));
//        flow.add(createFiller());
//
//        ResourceMenuButton rbtn = new ResourceMenuButton(review, callback);
//        if(isCustomProgram) {
//            rbtn.setEnabled(false);
//        }
//        flow.add(rbtn);
//        flow.add(createFiller());
//
//        for (PrescriptionSessionDataResource resource : new CmInmhStandardResources()) {
//            ResourceMenuButton btn = new ResourceMenuButton(resource, callback);
//            flow.add(btn);
//            flow.add(createFiller());
//            resourceButtons.put(resource.getType().label(), btn);
//        }
        
        
        ((SimplePanel)_guiContext.getWestWidget()).setWidget(flow);
    }
}
