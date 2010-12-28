package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.shared.client.rpc.RetryActionManager;
import hotmath.gwt.solution_editor.client.SolutionSearcherDialog.Callback;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionAdminAction;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionAdminAction.Type;
import hotmath.gwt.solution_editor.client.rpc.SaveSolutionAdminAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionAdminResponse;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class SolutionEditor implements EntryPoint {

    @Override
    public void onModuleLoad() {
        
        UserInfo userInfo = new UserInfo();
        UserInfo.setInstance(userInfo);
        RetryActionManager.getInstance();
        
        Viewport mainPort = new Viewport();
        mainPort.setLayout(new FitLayout());
        mainPort.setScrollMode(Scroll.AUTOY);
        
        mainPort.setLayout(new BorderLayout());
        LayoutContainer toolbarPanel = new HorizontalPanel();
        toolbarPanel.add(createToolbar());
        toolbarPanel.add(_title);
        mainPort.add(toolbarPanel, new BorderLayoutData(LayoutRegion.NORTH,25));

        _tabPanel.addStyleName("main-tab-panel");
        TabItem tab = new TabItem("Step Editor");
        tab.setWidth(960);
        tab.setLayout(new FitLayout());
        tab.add(_stepEditorViewer);
        tab.setScrollMode(Scroll.AUTO);
        _tabPanel.add(tab);
        
        //tab = new TabItem("Source View");
        //tab.setLayout(new FitLayout());
        //tab.setScrollMode(Scroll.AUTO);
        //tab.add(_textArea);
        //_tabPanel.add(tab);
        
        
        tab = new TabItem("Tutor View");
        tab.setLayout(new FitLayout());
        tab.add(_solutionViewer);
        tab.setScrollMode(Scroll.AUTO);
        _tabPanel.add(tab);
        
        mainPort.add(_tabPanel,new BorderLayoutData(LayoutRegion.CENTER));  
        
        mainPort.add(_status,new BorderLayoutData(LayoutRegion.SOUTH,25));

        String pid = SolutionEditor.getQueryParameter("pid");
        if(pid != null && pid.length() > 0) {
            __pidLoaded = pid;
        }

        _tabPanel.addListener(Events.Select, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if(__pidLoaded != null) {
                    TabItem item = _tabPanel.getSelectedItem();
                    if(item != null) {
                        if(_tabPanel.getSelectedItem().getText().equals("Tutor View")) {
                            _solutionViewer.loadSolution(__pidLoaded);
                        }
                        if(_tabPanel.getSelectedItem().getText().equals("Step Editor")) {
                            _stepEditorViewer.loadSolution(__pidLoaded);
                        }
                        
                    }
                }
            }
        });
        
        _title.setStyleName("main-title");
        RootPanel.get("main-content").add(mainPort);
    }
    
    private Widget createToolbar() {
        ToolBar tb = new ToolBar();
        
        tb.add(new Button("Create New",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                addSolutionIntoEditor();
            }
        }));

        
        tb.add(new Button("Load",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                loadSolutionIntoEditor();
            }
        }));
        
        tb.add(new Button("Refresh",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                _stepEditorViewer.loadSolution(__pidLoaded);
            }
        }));
        
        tb.add(new Button("Save",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                saveSolutionLoaded();
            }
        }));

        
        tb.add(new Button("Resources",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                showAllResources();
            }
        })); 
        
        return tb;
    }
    

    private void loadSolutionIntoEditor() {
        SolutionSearcherDialog.showSharedDialog(new Callback() {
            @Override
            public void solutionSelected(String pid) {
                _stepEditorViewer.loadSolution(pid);
            }
        });
    }
    
    private void showAllResources() {
        new SolutionResourceListDialog(__pidLoaded);
    }
    
    private void loadSolutionIntoEditor(final String pid) {
        Cookies.setCookie("last_pid", pid);
        GetSolutionAdminAction action = new GetSolutionAdminAction(Type.GET,pid);
        _status.setBusy("Loading solution ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionAdminResponse>() {
            public void onSuccess(SolutionAdminResponse solutionResponse) {
                __pidLoaded = pid;
                _textArea.setValue(solutionResponse.getXml());
                _status.clearStatus("");
                _title.setText("Loaded Solution: " + pid);
            }
            @Override
            public void onFailure(Throwable arg0) {
                _status.clearStatus("");
                arg0.printStackTrace();
                Window.alert(arg0.getLocalizedMessage());
            }
        });
    }
    
    
    private void addSolutionIntoEditor() {
        String pid;
        GetSolutionAdminAction action = new GetSolutionAdminAction(Type.CREATE,null);
        _status.setBusy("Loading solution ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionAdminResponse>() {
            public void onSuccess(SolutionAdminResponse solutionResponse) {
                __pidLoaded = solutionResponse.getPid();
                Cookies.setCookie("last_pid",solutionResponse.getPid());
                
                _stepEditorViewer.loadSolution(solutionResponse.getPid());
                _status.clearStatus("");
                _title.setText("Loaded Solution: " + solutionResponse.getPid());
            }
            @Override
            public void onFailure(Throwable arg0) {
                _status.clearStatus("");
                arg0.printStackTrace();
                Window.alert(arg0.getLocalizedMessage());
            }
        });
    }

    
    
    private void saveSolutionLoaded() {
        _stepEditorViewer.saveStepChanges();
    } 
    
    private void saveSolutionLoaded2() {
        SaveSolutionAdminAction action = new SaveSolutionAdminAction(__pidLoaded,_textArea.getValue());
        _status.setBusy("Saving solution ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData solutionResponse) {
                _status.clearStatus("");
            }
            @Override
            public void onFailure(Throwable arg0) {
                _status.clearStatus("");
                arg0.printStackTrace();
                Window.alert(arg0.getLocalizedMessage());
            }
        });
    }    
    
    /** Static routines used throughout app
     * 
     *  TODO: move to separate module
     *
     * @return
     */
    static CmServiceAsync getCmService() {
        return _cmService;
    }

    static CmServiceAsync _cmService;

    static private void setupServices() {
        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";

        _cmService = (CmServiceAsync) GWT.create(CmService.class);
        ((ServiceDefTarget) _cmService).setServiceEntryPoint(point + "services/cmService");
    }
    
    
    static {
        setupServices();
        _queryParameters = readQueryString();
    }
    /**
     * Return the parameter passed on query string
     * 
     * returns null if parameter not set
     * 
     * @param name
     * @return
     */
    static public String getQueryParameter(String name) {
        return _queryParameters.get(name);
    }
    
    static public int getQueryParameterInt(String name) {
        try {
            String val = getQueryParameter(name);
            if(val != null)
                return Integer.parseInt(val);
        }
        catch(Exception e) {
            /* silent */
        }
        return 0;
    }
    
    /**
     * Convert string+list to string+string of all URL parameters
     * 
     */
    static Map<String, String> _queryParameters;
    static private Map<String, String> readQueryString() {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, List<String>> query = Window.Location.getParameterMap();
        for (String s : query.keySet()) {
            m.put(s, query.get(s).get(0));
        }
        return m;
    }
    
    TabPanel _tabPanel = new TabPanel();    
    TextArea _textArea = new TextArea();
    SolutionViewer _solutionViewer = new SolutionViewer();
    SolutionStepEditor _stepEditorViewer = new SolutionStepEditor();
    Label _title = new Label();
    static public Status _status = new Status();
    
    
    /* all global definitions 
     * 
     */
    static public String __pidLoaded;    
}


class SolutionResourceModel extends BaseModel {
    public SolutionResourceModel(SolutionResource resource) {
        set("file", resource.getFile());
        set("url", resource.getUrlPath());
    }

}

