package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.rpc.AddMathMlResourceAction;
import hotmath.gwt.solution_editor.client.rpc.GetMathMlResourceAction;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction.ResourceType;
import hotmath.gwt.solution_editor.client.rpc.MathMlResource;
import hotmath.gwt.solution_editor.client.rpc.RemoveSolutionResourceAdminAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class SolutionResourceListDialog extends Window {
    ListView<SolutionResourceModel> listView;
    String pid;
    Callback _callback;
    
    public SolutionResourceListDialog(String pid) {
        this(null, pid);
    }

    TabPanel _tabPanel = new TabPanel();
    TabItem _tabLocal, _tabGlobal;
    
    public SolutionResourceListDialog(Callback callback, String pid) {
        this._callback = callback;
        this.pid = pid;
        setSize(800,600);
        
        addWindowListener(new WindowListener() {
            @Override
            public void windowHide(WindowEvent we) {
                if(_callback != null) {
                    _callback.resourceSelected(null);
                }
            }
        });
        
        setScrollMode(Scroll.AUTO);
        
        _tabLocal = new TabItem("Local Resources");
        _tabLocal.add(new Html("Loading ..."));
        
        _tabPanel.add(_tabLocal);
        
        _tabGlobal = new TabItem("Global Resources");
        _tabGlobal.add(new Label("Global"));
        
        _tabPanel.add(_tabGlobal);
        
        add(_tabPanel);
        
        _tabPanel.addListener(Events.Select, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                getDataFromServer();
            }
        });
        
        getHeader().addTool(new Button("Add MathML Resource", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new MathMlEditorDialog(new hotmath.gwt.solution_editor.client.MathMlEditorDialog.Callback() {
                    @Override
                    public void resourceUpdated(MathMlResource resource) {
                        createResourceOnServer(resource);
                    }
                },null, true);
            }
        }));

        getHeader().addTool(new Button("Upload Resource", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                String pid = determineResourceType()==ResourceType.LOCAL?SolutionResourceListDialog.this.pid:null;
                new SolutionResourceUploadDialog(pid, new SolutionResourceUploadDialog.Callback() {
                    public void resourceAdded() {
                        getDataFromServer();
                    }
                });
            }
        }));
        
        getHeader().addTool(new Button("Update", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                updateSelectedResource();
            }
        }));  
        
        getHeader().addTool(new Button("Del", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
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
        
        
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        setModal(true);
        setVisible(true);
        
       // getDataFromServer();
    }
    
    private SolutionResource getSelectedResource() {
        return _selectedResource.getResource();
    }
    
    private void removeSelectedResource() {
        final String file = _selectedResource.getResource().getFile();
        MessageBox.confirm("Delete Resource?", "Are you sure you want to delete resource '" + file + "'?", new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                if(!be.isCancelled()) {
                     removeResource(file);
                }
            }
        });
    }

    private void updateSelectedResource() {
        final String file = _selectedResource.getResource().getFile();
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
    private void showResources(CmList<SolutionResource> resources) {
        
        TabItem tabItem = _tabPanel.getSelectedItem();
        tabItem.removeAll();
        
        for(SolutionResource sr: resources) {
            ResourceType resourceType = determineResourceType();
            String rPid = (determineResourceType() == ResourceType.GLOBAL)?null:pid;
            final SolutionResourcePanel thisResource = new SolutionResourcePanel(sr,rPid);
            thisResource.addListener(Events.OnDoubleClick, new Listener<BaseEvent>() {
                @Override
                public void handleEvent(BaseEvent be) {
                    updateSelectedResource();
                }
            });
            thisResource.addListener(Events.OnClick, new Listener<BaseEvent>() {
                public void handleEvent(BaseEvent be) {
                    ArrayList<String> links = new ArrayList<String>();
                    putElementLinkIDsInList(thisResource.getParent().getElement(), links);
                    for(int v=0,t=links.size();v<t;v++) {
                        Element el = DOM.getElementById(links.get(v));
                        el.removeClassName("selected");
                    }
                    thisResource.el().addStyleName("selected");
                    
                    _selectedResource = thisResource;
                }
            });
            tabItem.add(thisResource);
        }
        
        layout();
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
        if(_tabPanel.getSelectedItem() == _tabLocal)
            return ResourceType.LOCAL;
        else if(_tabPanel.getSelectedItem() == _tabGlobal)
            return ResourceType.GLOBAL;
        
        return null;
    }
    
    private void getDataFromServer() {
        
        ResourceType resourceType = determineResourceType();
        
        SolutionEditor.__status.setText("Getting " + resourceType + " resources ...");
        
        GetSolutionResourcesAdminAction action = new GetSolutionResourcesAdminAction(pid, resourceType);
        SolutionEditor.getCmService().execute(action, new AsyncCallback<CmList<SolutionResource>>() {
            public void onSuccess(CmList<SolutionResource> resources) {
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
    
}
