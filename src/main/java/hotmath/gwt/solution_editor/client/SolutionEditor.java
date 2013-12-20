package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.CmEventListener;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.DeleteSolutionAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmService;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.shared.client.rpc.RetryActionManager;
import hotmath.gwt.solution_editor.client.SolutionSearcherDialog.Callback;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionAdminAction;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionAdminAction.Type;
import hotmath.gwt.solution_editor.client.rpc.SolutionAdminResponse;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

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

        HorizontalLayoutContainer toolbarPanel = new HorizontalLayoutContainer();
        toolbarPanel.add(createToolbar());

        BorderLayoutContainer borderContainer = new BorderLayoutContainer();

        _mainPanel.setWidget(borderContainer);

        borderContainer.setNorthWidget(createToolbar(), new BorderLayoutData(30));
        borderContainer.setSouthWidget(__status, new BorderLayoutData(25));
        borderContainer.setCenterWidget(_stepEditorViewer);

        mainPort.setWidget(_mainPanel);

        __pidToLoad = SolutionEditor.getQueryParameter("pid");
        if (__pidToLoad == null)
            __pidToLoad = Cookies.getCookie("last_pid");

        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if (event.getEventType().equals(EventTypes.SOLUTION_LOAD_COMPLETE)) {
                    _mainPanel.setHeadingText("Loaded: " + event.getEventData());
                }
            }
        });
        RootPanel.get("main-content").add(mainPort);
        if (__pidToLoad != null) {
            _stepEditorViewer.loadSolution(__pidToLoad.split("$")[0]); // strip
                                                                       // off
                                                                       // any
                                                                       // context
                                                                       // reference
        }
    }

    private Widget createToolbar() {

        HorizontalLayoutContainer tb = new HorizontalLayoutContainer();

        tb.add(new TextButton("Create", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                addSolutionIntoEditor();
            }
        }));

        tb.add(new TextButton("Load", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                loadSolutionIntoEditor();
            }
        }));

        tb.add(new TextButton("Delete", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                deleteThisSolution();
            }
        }));

        tb.add(new TextButton("Refresh", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _stepEditorViewer.loadSolution(__pidToLoad);
            }
        }));

        _saveButton = new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                saveSolutionLoaded();
            }
        });

        _saveButton.addStyleName("solution-editor-save-button");
        tb.add(_saveButton, new HorizontalLayoutData(5, 5));

        tb.add(new TextButton("Save As", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                SaveSolutionAsDialog.getSharedInstance().setCallback(new hotmath.gwt.solution_editor.client.SaveSolutionAsDialog.Callback() {
                    @Override
                    public void saveSolutionAs(String pid) {
                        _stepEditorViewer.saveStepChanges(pid);
                    }
                }, __pidToLoad);
            }
        }));

        tb.add(createGenerateContextButton());

        Menu viewMenu = new Menu();
        MenuItem mi = new MenuItem("Tutor", new SelectionHandler<MenuItem>() {

            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                showTutorView();
            }
        });
        viewMenu.add(mi);

        mi = new MenuItem("XML (on server)", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                showSolutionXml(__pidToLoad);
            }
        });
        viewMenu.add(mi);

        TextButton viewBtn = new TextButton("View");
        viewBtn.setMenu(viewMenu);
        tb.add(viewBtn);

        tb.add(new TextButton("Resources", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showAllResources();
            }
        }));

        tb.add(new TextButton("Help", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new ShowHelpWindow();
            }
        }));

        isActiveCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED, _stepEditorViewer._meta));
            }
        });
        return tb;
    }

    protected void deleteThisSolution() {
        if (__pidToLoad == null) {
            CmMessageBox.showAlert("No solution loaded");
            return;
        }
        CmMessageBox.confirm("Delete Solution", "Are you sure you want to delete this problem?", new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
                if (yesNo) {
                    deleteSolutionOnServer();
                }
            }
        });
    }

    protected void deleteSolutionOnServer() {
        DeleteSolutionAction action = new DeleteSolutionAction(__pidToLoad);
        SolutionEditor.getCmService().execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData data) {
                CmMessageBox.showAlert("Solution was deleted successfully");
            }

            @Override
            public void onFailure(Throwable arg0) {
                Log.error("Error deleting solution: " + arg0);
                CmMessageBox.showAlert(arg0.getLocalizedMessage());
            }
        });
    }

    private Widget createGenerateContextButton() {
        TextButton Button = new TextButton("Variable Contexts", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                generateContexts();
            }
        });
        return Button;
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
        GetSolutionAdminAction action = new GetSolutionAdminAction(Type.GET, pid);
        __status.setBusy("Loading solution ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionAdminResponse>() {
            public void onSuccess(SolutionAdminResponse solutionResponse) {
                __status.clearStatus("");
                new ShowValueWindow("Raw XML From Server", solutionResponse.getXml(), false);
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
        if (__pidToLoad != null) {
            new SolutionViewerFrame(__pidToLoad);
        }
    }

    private void addSolutionIntoEditor() {
        GetSolutionAdminAction action = new GetSolutionAdminAction(Type.CREATE, null);
        __status.setBusy("Loading solution ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionAdminResponse>() {
            public void onSuccess(SolutionAdminResponse solutionResponse) {
                _stepEditorViewer.loadSolution(solutionResponse.getPid());
                __status.clearStatus("");
                _mainPanel.setHeadingText("Loaded Solution: " + solutionResponse.getPid());
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

    /**
     * Static routines used throughout app
     * 
     * TODO: move to separate module
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
            if (val != null)
                return Integer.parseInt(val);
        } catch (Exception e) {
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

    TextButton _saveButton;

    static public Status __status = new Status();
    static SolutionEditor __instance;

    /*
     * all global definitions
     */
    static public String __pidToLoad;

    static {
        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                /**
                 * Track globally each time a new solution is loaded
                 */
                if (event.getEventType().equals(EventTypes.SOLUTION_LOAD_COMPLETE)) {
                    __pidToLoad = (String) event.getEventData();
                    Cookies.setCookie("last_pid", __pidToLoad);
                    __instance._saveButton.setEnabled(false);
                } else if (event.getEventType().equals(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED)) {
                    __instance._saveButton.setEnabled(true);
                } else if (event.getEventType().equals(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_SAVED)) {
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

    private void generateContexts() {
        new SolutionContextCreatorDialog(__pidToLoad);
    }
}

class SolutionResourceModel extends BaseModel{
    private String file;
    private String url;
    public SolutionResourceModel(SolutionResource resource) {
        this.file = resource.getFile();
        this.url = resource.getUrlPath();
    }
    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
}
