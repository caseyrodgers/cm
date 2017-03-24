package hotmath.gwt.solution_editor.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
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
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.CmEventListener;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.cm_core.client.model.SearchSuggestion;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.SolutionAdminResponse;
import hotmath.gwt.cm_rpc.client.model.SpellCheckResults;
import hotmath.gwt.cm_rpc.client.rpc.DeleteSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAdminAction;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAdminAction.Type;
import hotmath.gwt.cm_rpc.client.rpc.SpellCheckAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmService;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.rpc.RetryActionManager;
import hotmath.gwt.solution_editor.client.SolutionSearcherDialog.Callback;


public class SolutionEditor implements EntryPoint {

    public SolutionEditor() {
        __instance = this;
    }
    
    @Override
    public void onModuleLoad() {
        GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {

            	Window.alert("Uncaught Exception: " + e.toString());
                e.printStackTrace();

                try {
                    String nameAndTime = getClass().getName() + ": Uncaught exception: " + new Date();
                    /**
                    CmRpcCore.getCmService().execute(

                            new LogRetryActionFailedAction("uncaught exception", UserInfo.getInstance().getUid(), nameAndTime, null, CmShared
                                    .getStackTraceAsString(e)), new AsyncCallback<RpcData>() {
                                @Override
                                public void onSuccess(RpcData result) {
                                    CmLogger.info("Retry operation logged");
                                }

                                @Override
                                public void onFailure(Throwable exe) {
                                    if (CmCore.isDebug() == true)
                                        Window.alert("Error sending info about uncaught exception: " + exe);
                                }
                            });
                   */                            
                } catch (Exception x) {
                    CmLogger.error("Uncaught exception: " + x.getMessage(), x);
                }
            }
        });

    	
    	

        publishNative(this);

        Viewport mainPort = new Viewport();
        UserInfo userInfo = new UserInfo();
        UserInfo.setInstance(userInfo);
        RetryActionManager.getInstance();

        // mainPort.setScrollMode(Scroll.AUTOY);
        
        
        HorizontalLayoutContainer toolbarPanel = new HorizontalLayoutContainer();
        toolbarPanel.add(createToolbar());
        
        BorderLayoutContainer borderMain = new BorderLayoutContainer();

        borderMain.setNorthWidget(createToolbar(), new BorderLayoutData(30));
        
        borderMain.setSouthWidget(__status,new BorderLayoutData(25));
        borderMain.setCenterWidget(_stepEditorViewer);
        
        _mainPanel.setWidget(borderMain);

        __pidToLoad = SolutionEditor.getQueryParameter("pid");
        if(__pidToLoad == null) {
            __pidToLoad = Cookies.getCookie("last_pid");
        }
        
        __pidToLoad = __pidToLoad != null?__pidToLoad:null;

        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType().equals(EventTypes.SOLUTION_LOAD_COMPLETE)) {
                    _mainPanel.setHeadingText("Loaded: " + event.getEventData());
                }
            }
        });
        
        SolutionSearcherDialog.getInstance(null);
        
       // RootPanel.get("main-content").add(mainPort);
        if(__pidToLoad != null) {
            _stepEditorViewer.loadSolution(__pidToLoad.split("$")[0]);  // strip off any context reference
        }
        
        mainPort.setWidget(_mainPanel);
        
        CmBusyManager.showLoading(false);
        
        Login.getInstance().makeSureLoggedIn();
        
        
        
        
        final FileUploadField fileUpload = new FileUploadField();
        final FormPanel form = new FormPanel();
        form.setEncoding(FormPanel.Encoding.MULTIPART);
        form.setMethod(FormPanel.Method.POST);
        form.add(fileUpload);

        RootPanel.get("main-content").add(form);
        
        
        
        
//        List<SolutionSearchModel> ss = new ArrayList<SolutionSearchModel>();
//        ss.add(new SolutionSearchModel("test_casey_1_1_1_2",true));
//        ss.add(new SolutionSearchModel("notexist",true));
//        ss.add(new SolutionSearchModel("notexist2",true));
//        new SearchReplaceDialog().showDialog(ss);
    }

    private Widget createToolbar() {

        ToolBar tb = new ToolBar();
        tb.setBorders(true);
//        HorizontalLayoutContainer tb = new HorizontalLayoutContainer();
//        HorizontalLayoutData td = new HorizontalLayoutData();
//        td.setMargins(new Margins(2));


        tb.add(new TextButton("Create",new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                addSolutionIntoEditor();
            }
        }));


        tb.add(new TextButton("Load",new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                loadSolutionIntoEditor();
            }
        }));

        tb.add(new TextButton("Delete",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                deleteThisSolution();
            }
        }));

        tb.add(new TextButton("Refresh",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _stepEditorViewer.loadSolution(__pidToLoad);
            }
        }));

        _saveButton = new TextButton("Save",new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                saveSolutionLoaded();
            }
        });
        _saveButton.addStyleName("solution-editor-save-button");
        tb.add(_saveButton);

        tb.add(new TextButton("Save As",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                SaveSolutionAsDialog.getSharedInstance().setCallback(new hotmath.gwt.solution_editor.client.SaveSolutionAsDialog.Callback() {
                    @Override
                    public void saveSolutionAs(String pid) {
                        _stepEditorViewer.saveStepChanges(pid);
                    }
                },__pidToLoad);
            }
        }));

        tb.add(createGenerateContextButton());
        
        
        tb.add(new TextButton("Spell Check", new SelectHandler() {         
            @Override
            public void onSelect(SelectEvent event) {
                spellCheckCurrentView();
            }
        }));
        

        Menu viewMenu = new Menu();
        MenuItem mi = new MenuItem("Tutor",new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                showTutorView();
            }
        });

        
        viewMenu.add(mi);
        mi = new MenuItem("XML (on server)",new SelectionHandler<MenuItem>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<MenuItem> event) {
                showSolutionXml(__pidToLoad);
            }
        });
        viewMenu.add(mi);

        TextButton viewBtn = new TextButton("View");
        viewBtn.setMenu(viewMenu);
        tb.add(viewBtn);

        tb.add(new TextButton("Resources",new SelectHandler() {
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
                EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED,_stepEditorViewer._meta));
            }
        });


        tb.add(new TextButton("Login", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
				Login.getInstance().login();
			}
		}));
        
        return tb;
    }


    protected void spellCheckCurrentView() {

        CmBusyManager.showLoading(true);
        String stepText = _stepEditorViewer.getSolutionStepsText();
        SpellCheckAction action = new SpellCheckAction(stepText);
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SpellCheckResults>() {
            public void onSuccess(SpellCheckResults result) {
                CmBusyManager.showLoading(false);
                String words = "";
                for(SearchSuggestion w: result.getCmList()) {
                    words += " " + w.getWord();
                }
                
                String message="";
                if(words.length() == 0) {
                    message = "No spelling errors found";
                }
                else {
                    message = "Possible misspelled words: <br/><br/>" + words;
                }
                CmMessageBox.showAlert(message);
            }
            @Override
            public void onFailure(Throwable arg0) {
                CmBusyManager.showLoading(false);
                Log.error("Error deleting solution: " + arg0);
                CmMessageBox.showAlert(arg0.getLocalizedMessage());
            }
        });
    }

    protected void deleteThisSolution() {
        if(__pidToLoad == null) {
            CmMessageBox.showAlert("No solution loaded");
            return;
        }
        CmMessageBox.confirm("Delete Solution",  "Are you sure you want to delete this problem?", new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
                if(yesNo) {
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
        SolutionSearcherDialog.getInstance(new Callback() {
            @Override
            public void solutionSelected(String pid) {
                _stepEditorViewer.loadSolution(pid);
            }
        }).showWindow();
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

    TextButton _saveButton;

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



    private void generateContexts() {
        new SolutionContextCreatorDialog(__pidToLoad);
    }
}
