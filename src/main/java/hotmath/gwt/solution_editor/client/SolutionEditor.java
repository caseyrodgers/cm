package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.CmEventListener;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.shared.client.rpc.RetryActionManager;
import hotmath.gwt.solution_editor.client.SolutionSearcherDialog.Callback;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionAdminAction;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionAdminAction.Type;
import hotmath.gwt.solution_editor.client.rpc.SolutionAdminResponse;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class SolutionEditor implements EntryPoint {
    
    public SolutionEditor() {
        __instance = this;
    }

    @Override
    public void onModuleLoad() {
        
        publishNative(this);
        
        Viewport mainPort = new Viewport();        
        UserInfo userInfo = new UserInfo();
        UserInfo.setInstance(userInfo);
        RetryActionManager.getInstance();

        mainPort.setScrollMode(Scroll.AUTOY);
        LayoutContainer toolbarPanel = new HorizontalPanel();
        toolbarPanel.add(createToolbar());
        _mainPanel.setLayout(new BorderLayout());
        
        _mainPanel.add(createToolbar(), new BorderLayoutData(LayoutRegion.NORTH,30));
        _mainPanel.add(__status,new BorderLayoutData(LayoutRegion.SOUTH,25));
        _mainPanel.add(_stepEditorViewer, new BorderLayoutData(LayoutRegion.CENTER));        
        mainPort.setLayout(new FitLayout());
        mainPort.add(_mainPanel);

        __pidToLoad = SolutionEditor.getQueryParameter("pid");
        if(__pidToLoad == null)
            __pidToLoad = Cookies.getCookie("last_pid");
        
        
        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType().equals(EventTypes.SOLUTION_LOAD_COMPLETE)) {
                    _mainPanel.setHeading("Loaded: " + event.getEventData());
                }
            }
        });
        RootPanel.get("main-content").add(mainPort);
        if(__pidToLoad != null) {
            _stepEditorViewer.loadSolution(__pidToLoad);
        }
    }
    
    private Widget createToolbar() {
        
        HorizontalPanel tb = new HorizontalPanel();
        TableData td = new TableData();
        td.setMargin(5);

        
        tb.add(new Button("Create",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                addSolutionIntoEditor();
            }
        }),td);

        
        tb.add(new Button("Load",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                loadSolutionIntoEditor();
            }
        }),td);
        
        tb.add(new Button("Refresh",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                _stepEditorViewer.loadSolution(__pidToLoad);
            }
        }),td);
        
        _saveButton = new Button("Save",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                saveSolutionLoaded();
            }
        });
        _saveButton.addStyleName("solution-editor-save-button");
        tb.add(_saveButton,td);
        
        tb.add(new Button("Save As",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                SaveSolutionAsDialog.getSharedInstance().setCallback(new hotmath.gwt.solution_editor.client.SaveSolutionAsDialog.Callback() {
                    @Override
                    public void saveSolutionAs(String pid) {
                        _stepEditorViewer.saveStepChanges(pid);
                    }
                },__pidToLoad);
            }
        }),td);
        
        
       
        Menu viewMenu = new Menu();
        MenuItem mi = new MenuItem("Tutor",new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                showTutorView();
            }
        });
        viewMenu.add(mi);
        mi = new MenuItem("XML (on server)",new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                showSolutionXml(__pidToLoad);
            }
        });
        viewMenu.add(mi);

        Button viewBtn = new Button("View");
        viewBtn.setMenu(viewMenu);
        tb.add(viewBtn,td);

        
        tb.add(new Button("Resources",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                showAllResources();
            }
        }),td);
        
        tb.add(new Button("Help",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new ShowHelpWindow();
            }
        }));
        
        
        isActiveCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED,_stepEditorViewer._meta));                
            }
        });

        
        
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
        new SolutionResourceListDialog(__pidToLoad);
    }
    
    private void showSolutionXml(final String pid) {
        GetSolutionAdminAction action = new GetSolutionAdminAction(Type.GET,pid);
        __status.setBusy("Loading solution ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionAdminResponse>() {
            public void onSuccess(SolutionAdminResponse solutionResponse) {
                __status.clearStatus("");
                new ShowValueWindow("Raw XML From Server", solutionResponse.getXml(),false);
            }
            @Override
            public void onFailure(Throwable arg0) {
                __status.clearStatus("");
                arg0.printStackTrace();
                Window.alert(arg0.getLocalizedMessage());
            }
        });
    }
    
    
    private void showTutorView() {
        if(__pidToLoad!=null) {
            new SolutionViewerFrame(__pidToLoad);
        }
    }
    
    
    private void addSolutionIntoEditor() {
        GetSolutionAdminAction action = new GetSolutionAdminAction(Type.CREATE,null);
        __status.setBusy("Loading solution ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionAdminResponse>() {
            public void onSuccess(SolutionAdminResponse solutionResponse) {
                _stepEditorViewer.loadSolution(solutionResponse.getPid());
                __status.clearStatus("");
                _mainPanel.setHeading("Loaded Solution: " + solutionResponse.getPid());
            }
            @Override
            public void onFailure(Throwable arg0) {
                __status.clearStatus("");
                arg0.printStackTrace();
                Window.alert(arg0.getLocalizedMessage());
            }
        });
    }

    
    
    private void saveSolutionLoaded() {
        _stepEditorViewer.saveStepChanges(__pidToLoad);
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
    
    ContentPanel _mainPanel = new ContentPanel();    
    TextArea _textArea = new TextArea();
    SolutionStepEditor _stepEditorViewer = new SolutionStepEditor();
    CheckBox isActiveCheckBox = new CheckBox();
    
    Button _saveButton;
    
    static public Status __status = new Status();
    static SolutionEditor __instance;
    
    /* all global definitions 
     * 
     */
    static public String __pidToLoad;

    
    static {
        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                /** Track globally each time a new solution is loaded 
                 */
                if(event.getEventType().equals(EventTypes.SOLUTION_LOAD_COMPLETE)) {
                    __pidToLoad = (String)event.getEventData();
                    Cookies.setCookie("last_pid", __pidToLoad);
                    __instance._saveButton.setEnabled(false);
                }
                else if(event.getEventType().equals(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED)) {
                    __instance._saveButton.setEnabled(true);
                }
                else if(event.getEventType().equals(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_SAVED)) {
                    __instance._saveButton.setEnabled(false);
                }
            }
        });
    }
    
    
    static public void tutorWidgetClicked_gwt() {
        SolutionStepEditor.__instance.showWidgetEditor();
    }
    
    native void publishNative(SolutionEditor se) /*-{
       $wnd.tutorWidgetClicked_gwt =   @hotmath.gwt.solution_editor.client.SolutionEditor::tutorWidgetClicked_gwt();
    }-*/;
}


class SolutionResourceModel extends BaseModel {
    public SolutionResourceModel(SolutionResource resource) {
        set("file", resource.getFile());
        set("url", resource.getUrlPath());
    }

}

