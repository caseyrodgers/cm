package hotmath.gwt.cm_tools.client.ui;

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
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard.WhiteboardResourceCallback;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2.TutorViewerProperties;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.info.Info;

public class TopicExplorer extends SimpleContainer {
    
    private Topic topic;
    
    private PrescriptionData prescriptionData;
    private TopicExplorerGuiContext _guiContext;
    public TopicExplorer(Topic topic) {
        this.topic = topic;
    
        _guiContext = new TopicExplorerGuiContext(topic);
        
        CenterLayoutContainer lc = new CenterLayoutContainer();
        lc.setWidget(new HTML("<h1>Loading " + topic.getName() + "</h1>"));
        
        setWidget(lc);

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

    
    static public interface Callback {
        void resourceIsLoaded();
    }
    
    Map<String, ResourceMenuButton> resourceButtons = new HashMap<String, ResourceMenuButton>();
    private Callback _callback;
    
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
                forceLayout();
                
                buildUi(prescription);
                
            }
        }.register();
        
    }
    
    public void setCallback(Callback callback) {
        this._callback = callback;
    }
    
    private Widget createFiller() {
        return new HTML("<div class='resource-filler'> &nbsp; </div>");
    }


    protected void buildUi(PrescriptionSessionResponse prescription) {
        buildUiInternal(prescription.getPrescriptionData());
    }

    private Map<String, List<InmhItemData>> _registeredResources = new HashMap<String, List<InmhItemData>>();

    public void buildUiInternal(PrescriptionData pData) {
        final AccordionLayoutContainer accPanel = new AccordionLayoutContainer() {
            @Override
            protected void onExpand(ExpandEvent event) {
                final Widget activeWidget = getActiveWidget();
                if(activeWidget instanceof AccContentPanel) {
                    if(__active != null) {
                        __active.removeResource();
                        __active = null;
                    }
                    __active = ((AccContentPanel)activeWidget);
                    __active.updateGui();
                }
                
                super.onExpand(event);
            }
            
            
        };
        final AccordionLayoutAppearance appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);
        accPanel.setExpandMode(ExpandMode.SINGLE_FILL);
        
        
        
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
                    /** skip these */
                    continue;
                    
                case PRACTICE:
                    resource.setLabel("Practice Problems");
                    break;
                    
                    
                case WEBLINK_EXTERNAL:
                    resource.setType(CmResourceType.WEBLINK);
                    break;
                    
                case WEBLINK:
                    for(InmhItemData i: resource.getItems()) {
                        i.setType(CmResourceType.WEBLINK);
                    }
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
            
            
            
            ContentPanel cp = new AccContentPanel(appearance, resource);
            cp.setHeadingText(resource.getLabel() + " (" + cntLabel + ")");
            cp.getHeader().addStyleName("acc-content-panel");
            // ResourceMenuButton btnResource = new ResourceMenuButton(resource, callback);

            // resourceButtons.put(resource.getType().label(), btnResource);
            
            /** if a Custom Program, then make sure the results
             * button is disabled .. there are no quizzes.
             */
            if(resource.getType() == CmResourceType.RESULTS) {
                review = resource;
            }
            else {
                accPanel.add(cp);
            }
        }

        setWidget(accPanel);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                accPanel.setActiveWidget(accPanel.getWidget(0));
                forceLayout();
            }
        });

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
        
        
        // ((SimplePanel)_guiContext.getWestWidget()).setWidget(flow);
        
        
        
        /** load the first resource */
        callback.loadResourceIntoHistory(CmResourceType.REVIEW.label(),  "0");
    }

    
    static AccContentPanel __active;

    class AccContentPanel extends ContentPanel {
        private PrescriptionSessionDataResource resource;

        public AccContentPanel(AccordionLayoutAppearance appearance, PrescriptionSessionDataResource resource) {
            super(appearance);
            this.resource = resource;
        }
        
        public void removeResource() {
            
        }

        public void updateGui() {
            if(getWidget() == null) {
                
                InmhItemData item = resource.getItems().get(0);
                prepareResource(item);
                _callback.resourceIsLoaded();
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
        BorderLayoutContainer _main=null;
        private void showResource(CmResourcePanel viewer, String title, boolean b) {
            if(_main == null) {
                _main = new BorderLayoutContainer();
                List<Widget> tools = new ArrayList<Widget>();
                if(viewer.getContainerTools() == null) {
                    tools = new ArrayList<Widget>();     
                }
                else {
                    tools = viewer.getContainerTools();
                }
                if(_toolbar == null) {
                    _toolbar = new HorizontalLayoutContainer();
                    _toolbar.getElement().setAttribute("style",  "margin: 5px");
                    for(Widget t: tools) {
                        _toolbar.add(t);
                    }
                    if(resource.getItems().size() > 1) {
                        final ComboBox<InmhItemData> combo = createItemCombo(resource.getItems());
                        combo.addSelectionHandler(new SelectionHandler<InmhItemData>() {
                            
                            @Override
                            public void onSelection(SelectionEvent<InmhItemData> event) {
                                InmhItemData item = combo.getCurrentValue();
                                prepareResource(item);
                            }
                        });
                        combo.getElement().setAttribute("style",  "float: right;position: static !important;margin: 0 20px !important");
                        _toolbar.add(combo);
                    }
                    
                    _main.setNorthWidget(_toolbar, new BorderLayoutData(35));
                }
                setWidget(_main);
            }
            _main.setCenterWidget(viewer.getResourcePanel());
            
            _callback.resourceIsLoaded();
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
