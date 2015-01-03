package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.search.TopicExplorer.TopicExplorerCallback;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard.WhiteboardResourceCallback;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2.TutorViewerProperties;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;


public class AccContentPanel extends ContentPanel {
    private PrescriptionSessionDataResource resource;
    TopicExplorerCallback _callback;
    
    public AccContentPanel(PrescriptionSessionDataResource resource, TopicExplorerCallback callback) {
        this.resource = resource;
        this._callback = callback;
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

    
    
    public List<Widget> getContainerTools(CmResourcePanel viewer) {
        List<Widget> tools = new ArrayList<Widget>();
        if(viewer.getContainerTools() == null) {
            tools = new ArrayList<Widget>();     
        }
        else {
            tools = viewer.getContainerTools();
        }
        return tools;
    }

    HorizontalLayoutContainer _toolbar=null;
    ComboBox<InmhItemData> _combo;


    protected void showResource(final CmResourcePanel viewer, String title, boolean b) {
        if(getWidget() ==  null) {
            List<Widget> tools = getContainerTools(viewer);
            
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