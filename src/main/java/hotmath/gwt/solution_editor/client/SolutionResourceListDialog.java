package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.solution_editor.client.list.ListSolutionResource;
import hotmath.gwt.solution_editor.client.rpc.AddMathMlResourceAction;
import hotmath.gwt.solution_editor.client.rpc.GetMathMlResourceAction;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction.ResourceType;
import hotmath.gwt.solution_editor.client.rpc.MathMlResource;
import hotmath.gwt.solution_editor.client.rpc.RemoveSolutionResourceAdminAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;


public class SolutionResourceListDialog extends GWindow {

    ListSolutionResource _listSolutionResource = new ListSolutionResource();
    
    String pid;
    Callback _callback;
    
    public SolutionResourceListDialog(String pid) {
        this(null, pid);
    }

    TabPanel _tabPanel = new TabPanel();
    TabItemConfig _tabLocal, _tabGlobal;
    
    FlowLayoutContainer _localPanel = new FlowLayoutContainer();

    private FlowLayoutContainer _globalPanel;
    public SolutionResourceListDialog(Callback callback, String pid) {
        super(false);
        this._callback = callback;
        this.pid = pid;
        setPixelSize(800,600);
        
        addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if(_callback != null) {
                    _callback.resourceSelected(null);
                }
            }
        });
        
        _localPanel.add(new HTML("Loading ..."));
        _tabLocal = new TabItemConfig("Local Resources");
        _tabPanel.add(_localPanel, _tabLocal);
        
        _tabGlobal = new TabItemConfig("Global Resources");
        
        _globalPanel = new FlowLayoutContainer();
        _globalPanel.add(new Label("Global"));
        
        _tabPanel.add(_globalPanel, _tabGlobal);
        
        setWidget(_tabPanel);
        
        _tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
            @Override
            public void onSelection(SelectionEvent<Widget> event) {
                getDataFromServer();
            }
        });
        
        getHeader().addTool(new TextButton("Add MathML Resource", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new MathMlEditorDialog(new hotmath.gwt.solution_editor.client.MathMlEditorDialog.Callback() {
                    @Override
                    public void resourceUpdated(MathMlResource resource) {
                        createResourceOnServer(resource);
                    }
                },null, true);
            }
        }));

        getHeader().addTool(new TextButton("Upload Resource", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                String pid = determineResourceType()==ResourceType.LOCAL?SolutionResourceListDialog.this.pid:null;
                new SolutionResourceUploadDialog(pid, new SolutionResourceUploadDialog.Callback() {
                    public void resourceAdded() {
                        getDataFromServer();
                    }
                }, __resources);
            }
        }));
        
        getHeader().addTool(new TextButton("Update", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                updateSelectedResource();
            }
        }));  
        
        getHeader().addTool(new TextButton("Del", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                removeSelectedResource();
            }
        }));
        
        

        
//        if(callback != null) {
//            addButton(new Button("Select", new SelectionListener<ButtonEvent>() {
//                @Override
//                public void componentSelected(ButtonEvent ce) {
//                    if(_callback != null) {
//                        _callback.resourceSelected(getSelectedResource());
//                    }
//                    hide();
//                }
//            }));
//        }
        
        
        addButton(new TextButton("Close", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        setModal(true);
        setVisible(true);
        
        getDataFromServer();
    }
    
    private SolutionResource getSelectedResource() {
        return _selectedResource.getResource();
    }
    
    private void removeSelectedResource() {
        final String file = _selectedResource!=null?_selectedResource.getResource().getFile():"";
        if(file ==  null) {
            return;
        }
        CmMessageBox.confirm("Delete Resource?", "Are you sure you want to delete resource '" + file + "'?", new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
                if (yesNo) {
                     removeResource(file);
                }
            }
        });
    }

    private void updateSelectedResource() {
        final String file = _selectedResource != null?_selectedResource.getResource().getFile():null;
        if(file == null)
            return;
        
        
        GetMathMlResourceAction action = null;
        if(determineResourceType() == ResourceType.LOCAL) {
            action = new GetMathMlResourceAction(pid,file);
        }
        else {
            action = new GetMathMlResourceAction(file);
        }
        
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, "Reading MathML for: " + file));
        SolutionEditor.getCmService().execute(action, new AsyncCallback<MathMlResource>() {
            public void onSuccess(MathMlResource resource) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, ""));

                new MathMlEditorDialog(new hotmath.gwt.solution_editor.client.MathMlEditorDialog.Callback() {
                    @Override
                    public void resourceUpdated(MathMlResource resource) {
                        createResourceOnServer(resource);
                    }
                },resource,true);


            }
            @Override
            public void onFailure(Throwable arg0) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, ""));
                arg0.printStackTrace();
                com.google.gwt.user.client.Window.alert(arg0.getLocalizedMessage());
            }
        });                
    }
    
    private void removeResource(String file) {
        RemoveSolutionResourceAdminAction action = null;
        if(determineResourceType() == ResourceType.LOCAL)
            action = new RemoveSolutionResourceAdminAction(pid,file);
        else 
            action = new RemoveSolutionResourceAdminAction(file);
        
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, "Removing resource: " + file));
        SolutionEditor.getCmService().execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData rpcData) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, ""));
                
                getDataFromServer();
            }
            @Override
            public void onFailure(Throwable arg0) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, ""));
                arg0.printStackTrace();
                com.google.gwt.user.client.Window.alert(arg0.getLocalizedMessage());
            }
        });                
    }

    private void addNewMathMlResource() {
        new SolutionResourceAddDialog(pid, new SolutionResourceAddDialog.Callback() {
            @Override
            public void resourceAdded() {
                getDataFromServer();
            }
        });
    }
    

    SolutionResourcePanel _selectedResource;
    protected CmList<SolutionResource> __resources;
    private void showResources(CmList<SolutionResource> resources) {
        
        final Container container = (Container) _tabPanel.getActiveWidget();
        container.clear();
        
        if(resources.size() == 0) {
            container.add(new Label("No Resources"));
        }
        else {
            for(SolutionResource sr: resources) {
                ResourceType resourceType = determineResourceType();
                String rPid = (determineResourceType() == ResourceType.GLOBAL)?null:pid;
                final SolutionResourcePanel thisResource = new SolutionResourcePanel(sr,rPid);
                
                final FocusPanel focusPanel = new FocusPanel(thisResource);
                focusPanel.addDoubleClickHandler(new DoubleClickHandler() {
                    @Override
                    public void onDoubleClick(DoubleClickEvent event) {
                        updateSelectedResource();
                    }
                });
                focusPanel.addClickHandler(new ClickHandler() {
                    
                    @Override
                    public void onClick(ClickEvent event) {
                        for(int i=0;i<container.getWidgetCount();i++) {
                            Widget w = container.getWidget(i);
                            if(w instanceof FocusPanel) {
                                ((FocusPanel)w).getWidget().removeStyleName("selected");
                            }
                        }
                        thisResource.addStyleName("selected");
                        
                        _selectedResource = thisResource;
                    }
                });
                focusPanel.setWidget(thisResource);
                container.add(focusPanel);
            }
        }
        
        forceLayout();
    }
    
    private native void putElementLinkIDsInList(Element elt, ArrayList list) /*-{
        var links = elt.getElementsByTagName("div");
        for (var i = 0; i < links.length; i++ ) {
            var link = links.item(i);
            link.id = ("uid-a-" + i);
            list.@java.util.ArrayList::add(Ljava/lang/Object;) (link.id);
        }
    }-*/;


    private ResourceType determineResourceType() {
        if(_tabPanel.getActiveWidget() == _localPanel)
            return ResourceType.LOCAL;
        else if(_tabPanel.getActiveWidget() == _globalPanel)
            return ResourceType.GLOBAL;
        
        return null;
    }
    
    private void getDataFromServer() {
        
        ResourceType resourceType = determineResourceType();
        
        SolutionEditor.__status.setText("Getting " + resourceType + " resources ...");
        
        GetSolutionResourcesAdminAction action = new GetSolutionResourcesAdminAction(pid, resourceType);
        SolutionEditor.getCmService().execute(action, new AsyncCallback<CmList<SolutionResource>>() {
            public void onSuccess(CmList<SolutionResource> resources) {
                __resources = resources;
                SolutionEditor.__status.setText("");
                showResources(resources);
            }
            @Override
            public void onFailure(Throwable arg0) {
                SolutionEditor.__status.setText("");
                arg0.printStackTrace();
                com.google.gwt.user.client.Window.alert(arg0.getLocalizedMessage());
            }
        });
    }
    
    
    private void createResourceOnServer(MathMlResource resource) {
        AddMathMlResourceAction action = null;
        if(determineResourceType() == ResourceType.LOCAL) {
            action = new AddMathMlResourceAction(this.pid,resource.getName(),resource.getMathMl());
        }
        else {
            action = new AddMathMlResourceAction(resource.getName(),resource.getMathMl());
        }
        SolutionEditor.getCmService().execute(action, new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                getDataFromServer();
            }
            @Override
            public void onFailure(Throwable caught) {
                com.google.gwt.user.client.Window.alert("Error: " + caught);
            }
        });
    }



    static public interface Callback {
        void resourceSelected(SolutionResource resource);
    }
    
    static public void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                new SolutionResourceListDialog("test_casey_1_1_1_1");
            }
        });
    }
    
}
