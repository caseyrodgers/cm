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
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.MyIconButton;
import hotmath.gwt.cm_tools.client.ui.ResourceMenuButton;
import hotmath.gwt.cm_tools.client.ui.TopicExplorerGuiContext;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard.WhiteboardResourceCallback;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2.TutorViewerProperties;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
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
        
        setWidget(tabPanel);

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

            String cntLabel = resource.getItems().size() + " item" + (resource.getItems().size() == 1?"":"s");
            if(resource.getType() == CmResourceType.WEBLINK) {
                if(resource.getItems() != null && resource.getItems().size() == 1) {
                    resource.setLabel(resource.getItems().get(0).getTitle());
                }
            }

            AccContentPanel cp = new AccContentPanel(resource);
            cp.setHeadingText(resource.getLabel());
            /** if a Custom Program, then make sure the results
             * button is disabled .. there are no quizzes.
             */
            if(resource.getType() == CmResourceType.RESULTS) {
                review = resource;
            }
            else {
                // _cardLayout.add(cp);
                TabItemConfig tabConfig = new TabItemConfig(resource.getLabel() + " (" + cntLabel + ")", false);
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

    class AccContentPanel extends ContentPanel {
        private PrescriptionSessionDataResource resource;

        public AccContentPanel(PrescriptionSessionDataResource resource) {
            this.resource = resource;
        }
        
        public void removeResource() {
        }

        public void updateGui() {
            if(getWidget() == null) {
                InmhItemData item = resource.getItems().get(0);
                if(_callback != null) {
                    prepareResource(item);
                    new Timer() {
                        @Override
                        public void run() {
                            _callback.resourceIsLoaded();
                        }
                    }.schedule(1000);
                }
                forceLayout();
            }
        }
        
        
        WhiteboardResourceCallback whiteboardTutorCallback = new WhiteboardResourceCallback() {
            public void ensureMaximizeResource() {}
            public void ensureOptimizedResource() {}
            public ResizeContainer getResizeContainer() {
                return null;
            }
        };
        
        
        TutorViewerProperties tutorViewerProperties = new TutorViewerProperties() {
            @Override
            public boolean shouldFireTutorEvents() {
                return false;
            }
        };
        
        private void prepareResource(final InmhItemData which) {
            try {
                ResourceViewerFactory.ResourceViewerFactory_Client client = new ResourceViewerFactory.ResourceViewerFactory_Client() {
                    @Override
                    public void onUnavailable() {
                        CatchupMathTools.showAlert("Resource not available");
                    }

                    @Override
                    public void onSuccess(ResourceViewerFactory instance) {
                        try {
                            
                            InmhItemData itemToShow = null;
                            for(InmhItemData i: resource.getItems()) {
                                if(i == which)    {
                                    itemToShow = i;
                                    break;
                                }
                            }
                            if(itemToShow != null) {
                                CmResourcePanel viewer = instance.create(itemToShow);
                                if(viewer instanceof ResourceViewerImplTutor2) {
                                    ((ResourceViewerImplTutor2)viewer).setWhiteboardCallback(whiteboardTutorCallback);
                                    ((ResourceViewerImplTutor2)viewer).setTutorViewerProperties(tutorViewerProperties);
                                }
                                showResource(viewer, itemToShow.getTitle(), true);
                            }
                        } catch (Exception e) {
                            CatchupMathTools.showAlert("Could not load resource: " + e.getLocalizedMessage());
                        }
                    }
                };
                ResourceViewerFactory.createAsync(client);
            } catch (Exception hme) {
                hme.printStackTrace();
                CatchupMathTools.showAlert("Error: " + hme.getMessage());
            }            
        }


        HorizontalLayoutContainer _toolbar=null;
        ComboBox<InmhItemData> _combo;


        private void showResource(final CmResourcePanel viewer, String title, boolean b) {
            if(getWidget() ==  null) {
                List<Widget> tools = new ArrayList<Widget>();
                if(viewer.getContainerTools() == null) {
                    tools = new ArrayList<Widget>();     
                }
                else {
                    tools = viewer.getContainerTools();
                }
                
                for(Widget t: tools) {
                    addTool(t);
                }
                if(resource.getItems().size() > 1) {
                    _combo = createItemCombo(resource.getItems());
                    _combo.addSelectionHandler(new SelectionHandler<InmhItemData>() {
                        @Override
                        public void onSelection(SelectionEvent<InmhItemData> event) {
                            InmhItemData item = _combo.getCurrentValue();
                            prepareResource(item);
                        }
                    });
                    // combo.getElement().setAttribute("style",  "float: right;position: static !important;margin: 0 20px !important");
                    TextButton prev = new TextButton("<", new SelectHandler() {
                        @Override
                        public void onSelect(SelectEvent event) {
                            moveToItem(-1);
                        }
                    });
                    prev.setToolTip("Move to previous item");
                    TextButton next = new TextButton(">", new SelectHandler() {
                        @Override
                        public void onSelect(SelectEvent event) {
                            moveToItem(1);
                        }
                    });
                    next.setToolTip("Move to next item");
                    prev.getElement().setAttribute("style",  "margin-left: 20px");
                    addTool(prev);
                    addTool(_combo);
                    addTool(next);
                }
            }
            
            setWidget(viewer.getResourcePanel());
            
            if(viewer.needForcedUiRefresh()) {
                viewer.getResourcePanel().setVisible(false);
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        viewer.getResourcePanel().setVisible(true);
                        forceLayout();
                    }
                });
            }
            else {
                forceLayout();
            }
            
            
            forceLayout();

            if(_callback != null) {
                _callback.resourceIsLoaded();
            }
        }

        protected void moveToItem(int amount) {
            InmhItemData val = _combo.getCurrentValue();
            List<InmhItemData> items = _combo.getStore().getAll();
            int which=0;
            for(InmhItemData item: items) {
                if(item == val) {
                    break;
                }
                which++;
            }
            which += amount;
            if(which > items.size()-1) {
                which = 0;
            }
            else if(which < 0) {
                which = items.size()-1;
            }
            _combo.setValue(items.get(which));
            prepareResource(items.get(which));            
        }

        public PrescriptionSessionDataResource getResource() {
            return resource;
        }

        public void setResource(PrescriptionSessionDataResource resource) {
            this.resource = resource;
        }
    }
    
    

    interface ItemComboProps extends PropertyAccess<String> {
        ModelKeyProvider<InmhItemData> file();
        LabelProvider<InmhItemData> title();
    }
    ItemComboProps props = GWT.create(ItemComboProps.class);
    private ComboBox<InmhItemData> createItemCombo(List<InmhItemData> list) {
        ListStore<InmhItemData> store = new ListStore<InmhItemData>(props.file());
        ComboBox<InmhItemData> combo = new ComboBox<InmhItemData>(store, props.title());
        combo.setAllowBlank(false);
        combo.setForceSelection(true);
        combo.setEditable(false);
        combo.setTriggerAction(TriggerAction.ALL);
        for(InmhItemData i: list) {
            if(!store.getAll().contains(i)) {
                store.add(i);
            }
        }
        combo.setValue(store.get(0));
        return combo;
    }
}





class ResourceToolBar extends FlowLayoutContainer {
    public interface Callback {
        void loadResourceType(CmResourceType type);
    }
    public ResourceToolBar(final Callback callback) {
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