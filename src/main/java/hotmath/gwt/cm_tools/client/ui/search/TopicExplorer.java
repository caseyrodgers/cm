package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_core.client.event.RppHasBeenViewedEvent;
import hotmath.gwt.cm_core.client.event.RppHasBeenViewedEventHandler;
import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.ui.MyIconButton;
import hotmath.gwt.cm_tools.client.ui.ResourceMenuButton;
import hotmath.gwt.cm_tools.client.ui.TopicExplorerGuiContext;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;

public class TopicExplorer extends SimpleContainer {
    
    private Topic topic;
    
    private PrescriptionData prescriptionData;
    private TopicExplorerGuiContext _guiContext;
    public TopicExplorer(Topic topic, TopicExplorerCallback callback) {
        this.topic = topic;
        this._callback = callback;
    
        _guiContext = new TopicExplorerGuiContext(topic);
        
        CenterLayoutContainer lc = new CenterLayoutContainer();
        lc.setWidget(new HTML("<h1>Loading " + topic.getName() + "</h1>"));
        setWidget(lc);
        
//        setNorthWidget(new ResourceToolBar(new ResourceToolBar.Callback() {
//            @Override
//            public void loadResourceType(CmResourceType type) {
//                for(int i=0;i<_cardLayout.getWidgetCount();i++) {
//                    AccContentPanel w = (AccContentPanel)_cardLayout.getWidget(i);
//                    if(w.getResource().getType() == type) {
//                        _cardLayout.setActiveWidget(w);
//                        break;
//                    }
//                }                
//            }
//        }), new BorderLayoutData(45));
        
        loadDataFromServer();
        
        CmRpcCore.EVENT_BUS.addHandler(RppHasBeenViewedEvent.TYPE, new RppHasBeenViewedEventHandler() {
            @Override
            public void rppHasBeenViewed(RppHasBeenViewedEvent item) {
                // _mainPanel.markRppAsViewed(item);
            }
        });
    }


    @Override
    public Widget asWidget() {
        return this;
    }

    
    static public interface TopicExplorerCallback {
        void resourceIsLoaded();
    }
    
    Map<String, ResourceMenuButton> resourceButtons = new HashMap<String, ResourceMenuButton>();
    private TopicExplorerCallback _callback;
    
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
                
                DefaultGxtLoadingPanel loading = new DefaultGxtLoadingPanel();
                setWidget(loading);
                
                
                buildUi(prescription);
                
            }
        }.register();
        
    }
    
    public void setCallback(TopicExplorerCallback callback) {
        this._callback = callback;
    }
    
    private Widget createFiller() {
        return new HTML("<div class='resource-filler'> &nbsp; </div>");
    }


    protected void buildUi(PrescriptionSessionResponse prescription) {
        buildUiInternal(prescription.getPrescriptionData());
    }

    private Map<String, List<InmhItemData>> _registeredResources = new HashMap<String, List<InmhItemData>>();

    CardLayoutContainer _cardLayout;
    TabPanel tabPanel;
    public void buildUiInternal(PrescriptionData pData) {
        
        _cardLayout = new CardLayoutContainer() {
            @Override
            public void setActiveWidget(Widget widget) {
                if(widget instanceof AccContentPanel) {
                  if(__active != null) {
                      __active.removeResource();
                      __active = null;
                  }
                  __active = ((AccContentPanel)widget);
                  __active.updateGui();
             
                  super.setActiveWidget(widget);
            }
        }};
        
        tabPanel = new TabPanel();
        
        tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
            @Override
            public void onSelection(SelectionEvent<Widget> event) {
                final Widget activeWidget = tabPanel.getActiveWidget();
                if(activeWidget instanceof AccContentPanel) {
                    if(__active != null) {
                        __active.removeResource();
                        __active = null;
                    }
                    __active = ((AccContentPanel)activeWidget);
                    __active.updateGui();
                }
            }
        });
        
        this.prescriptionData = pData;
        List<PrescriptionSessionDataResource> resources = pData.getCurrSession().getInmhResources();

        ResourceMenuButton.RegisterCallback callback = new ResourceMenuButton.RegisterCallback() {
            @Override
            public void registerItems(String label, List<InmhItemData> items) {
                _registeredResources.put(label, items);
            }
            
            @Override
            public void loadResourceIntoHistory(String label, String item) {
                List<InmhItemData> resources = _registeredResources.get(label.toLowerCase());
                Info.display("Load",  "Load resource: " + label + ", " + item);
                
//                int index = Integer.parseInt(item);
//                InmhItemData resource = resources.get(index);
                // _mainPanel.showResource(resource);
                if(_callback != null) {
                    _callback.resourceIsLoaded();
                }
            }
            
            @Override
            public boolean shouldTrackViewedResources() {
                return false;
            }
        };
        
        
        
        PrescriptionSessionDataResource review=null;
        // setTitle("Choose a resource type, then click one of its items.");
        for (PrescriptionSessionDataResource resource : resources) {
            
            
            if(resource.getItems().size() == 0) {
                continue;
            }
            
            /** do not show EPPs
             * 
             */
            switch(resource.getType()) {
                case CMEXTRA:
                case TESTSET:
                case ACTIVITY_STANDARD:
                case RESULTS:
                case WEBLINK_EXTERNAL:
                case WEBLINK:
                    
                    /** skip these */
                    continue;
                    
                case PRACTICE:
                    resource.setLabel("Practice Problems");
                    break;
                    
			    default:
				    break;
            }

            String cntLabel = "";
            if(resource.getItems().size() > 1) {
                cntLabel = " ( " + resource.getItems().size() + " item" + (resource.getItems().size() == 1?"":"s") + ")";
            }
            if(resource.getType() == CmResourceType.WEBLINK) {
                if(resource.getItems() != null && resource.getItems().size() == 1) {
                    resource.setLabel(resource.getItems().get(0).getTitle());
                }
            }

            AccContentPanel cp = AccContentPanelFactory.create(resource, _callback);
            cp.setHeadingText(resource.getLabel());
            /** if a Custom Program, then make sure the results
             * button is disabled .. there are no quizzes.
             */
            if(resource.getType() == CmResourceType.RESULTS) {
                review = resource;
            }
            else {
                // _cardLayout.add(cp);
                TabItemConfig tabConfig = new TabItemConfig(resource.getLabel() + cntLabel, false);
                tabConfig.setIcon( getResourceIcon(resource.getType()));
                tabPanel.add(cp,tabConfig);
            }
        }

        //_cardLayout.setActiveWidget(_cardLayout.getWidget(0));
        
        setWidget(tabPanel);
        
        /** load the first resource */
        callback.loadResourceIntoHistory(CmResourceType.REVIEW.label(),  "0");
    }

    
    private ImageResource getResourceIcon(CmResourceType type) {
        switch(type) {
            case ACTIVITY:
                return SearchResources.INSTANCE.resourceActivityImage();
                
            case VIDEO:
                return SearchResources.INSTANCE.resourceVideoImage();
                
            case PRACTICE:
                return SearchResources.INSTANCE.resourcePracticeImage();
                
            case REVIEW:
                return SearchResources.INSTANCE.resourceLessonImage();
                
            default:
                CmMessageBox.showAlert("Unknown CmResourceType: " + type);
        }
        
        
        return null;
    }

    static AccContentPanel __active;

}





class ResourceToolBar extends FlowLayoutContainer {
    
    static public interface ResourceToolBarCallback {
        void loadResourceType(CmResourceType type);
    }
    
    public ResourceToolBar(final ResourceToolBarCallback callback) {
        final MyIconButton resourceActivityButton = new MyIconButton("search-resource-activity-btn");
        resourceActivityButton.addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                callback.loadResourceType(CmResourceType.ACTIVITY);
            }
        });
        add(resourceActivityButton);
 
        final MyIconButton resourceVideoButton = new MyIconButton("search-resource-video-btn");
        resourceVideoButton.addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                callback.loadResourceType(CmResourceType.VIDEO);
            }
        });
        add(resourceVideoButton);
        
        final MyIconButton resourceProblemButton = new MyIconButton("search-resource-problem-btn");
        resourceProblemButton.addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                callback.loadResourceType(CmResourceType.PRACTICE);
            }
        });
        add(resourceProblemButton);
         
        final MyIconButton resourceLessonButton = new MyIconButton("search-resource-lesson-btn");
        resourceLessonButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                callback.loadResourceType(CmResourceType.REVIEW);
            }
        });
        add(resourceLessonButton);
        
   }
}