package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.CmEventListener;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_core.client.util.CmAlertify.PromptCallback;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.solution_editor.client.list.ListSolutionSearch;
import hotmath.gwt.solution_editor.client.rpc.ReplaceTextSolutionsAction;
import hotmath.gwt.solution_editor.client.rpc.SearchForSolutionsAction;

import java.util.ArrayList;
import java.util.List;

import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.TextField;


public class SolutionSearcherDialog {
    Callback callBack;
    String pid;
    Label _matches = new Label();

    TabPanel _tabPanel = new TabPanel();
    RecentTab _tabRecent;
    TextField _searchField = new TextField();
    TextField _searchFieldFull = new TextField();
    CheckBox _includeInActive = new CheckBox();
    TextButton replaceButton = new TextButton("Replace");


    private SolutionSearcherDialog() {
        pid = Cookies.getCookie("last_pid");
    }

    
    public void showWindow() {
        if(_window == null) {
            buildWindow();
        }
        _window.setVisible(true);
    }

    Window _window;
    private void buildWindow() {
        _window = new Window();
        _window.setPixelSize(500, 400);
        _window.addStyleName("solution-searcher-dialog");
        _window.setHeadingText("Solution Searcher Dialog");

        TabItemConfig searchTabItem = new TabItemConfig("Search");
        
        FramedPanel fPanel = new FramedPanel();
        
        fPanel.addButton(new MyFieldLabel(_includeInActive, "Include Inactive", 94, 10));
        fPanel.addButton(new TextButton("Search", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
                doSearch();
            }
        }));

        //_searchField.setFieldLabel("Search PIDs");
        _searchField.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
                int kc = event.getNativeEvent().getKeyCode();
                if (kc == 13)
                    doSearch();
            }
        });
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.add(new MyFieldLabel(_searchField, "Search PIDs", 80, 300));
        
        //_searchFieldFull.setFieldLabel("Text Search");
        _searchFieldFull.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
                int kc = event.getNativeEvent().getKeyCode();
                if (kc == 13)
                    doSearch();
            }
        });        
        flow.add(new MyFieldLabel(_searchFieldFull, "Text Search", 80, 300));
        
        fPanel.setBodyBorder(false);
        fPanel.setHeaderVisible(false);
        
        fPanel.setWidget(flow);

        
        BorderLayoutContainer searchTab = new BorderLayoutContainer();
        searchTab.setNorthWidget(fPanel, new BorderLayoutData(95));

        _listResults = new ListSolutionSearch();
        
        FlowLayoutContainer flowScroll = new FlowLayoutContainer();
        flowScroll.setScrollMode(ScrollMode.AUTO);
        FocusPanel focusPanel = new FocusPanel();
        focusPanel.setWidget(_listResults);
        flowScroll.add(focusPanel);
        
        focusPanel.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
                doSelect(_listResults.getSelectionModel().getSelectedItem().getPid());
            }
        });
        
        searchTab.setCenterWidget(flowScroll);

        searchTab.setSouthWidget(_matches, new BorderLayoutData(20));
        
        replaceButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) { 
                
                String searchText = _searchFieldFull.getCurrentValue();
                if(searchText == null || searchText.length() == 0) {
                    CmMessageBox.showAlert("You must enter search text before doing a replace.  This will be the text that is replaced.");
                    return;
                }
                
                List<SolutionSearchModel> res = _listResults.getStore().getAll();
                if(res.size() == 0) {
                    CmMessageBox.showAlert("No search results to replace");
                }
                else {
                    doSearchReplace(res, searchText);
                }
            }
        });
        _window.addButton(replaceButton);

        _window.addButton(new TextButton("View", new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
            	String pid=null;
                if (_tabPanel.getActiveWidget() ==_tabRecent) {
                    pid = _tabRecent._listResults.getSelectionModel().getSelectedItem().getPid();
                } else {
                    pid = _listResults.getSelectionModel().getSelectedItem().getPid();
                }
                doView(pid);
            }
        }));

        _window.addButton(new TextButton("Select", new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
                if (_tabPanel.getActiveWidget() == _tabRecent) {
                    doSelect(_tabRecent._listResults.getSelectionModel().getSelectedItem().getPid());
                } else {
                    doSelect(_listResults.getSelectionModel().getSelectedItem().getPid());
                }
            }
        }));

        _window.addButton(new TextButton("Close", new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
                _window.hide();
            }
        }));
        

        // _window.setLayout(new FitLayout());
        _tabPanel.add(searchTab, searchTabItem);

        TabItemConfig recentTabItem = new TabItemConfig("Recent");
        _tabRecent = new RecentTab();

        _tabPanel.add(_tabRecent, recentTabItem);

        _window.setWidget(_tabPanel);

        _tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(SelectionEvent<Widget> event) {
                if (_tabPanel.getActiveWidget() == _tabRecent) {
                    _tabRecent.refresh();
                }
            }
        });
    }
    
    String _lastValue;
    protected void doSearchReplace(final List<SolutionSearchModel> res, final String searchFor) {
        CmMessageBox.prompt("Replace '" + searchFor + "' in " + res.size() + " solution(s)", null,_lastValue, new PromptCallback() {           
            @Override
            public void promptValue(final String replaceWith) { 
                if(replaceWith == null || replaceWith.length() == 0) {
                    return;
                }
                
                _lastValue = replaceWith;
                
                CmMessageBox.confirm("Search/Replace",  "Are you sure you want to replace '" + searchFor + "' with '" + replaceWith + "' in all " + res.size() + " solution(s)?", new ConfirmCallback() {
                    
                    @Override
                    public void confirmed(boolean yesNo) {
                        if(yesNo) {
                            CmList<SolutionSearchModel> probs = new CmArrayList<SolutionSearchModel>();
                            probs.addAll(res);
                            ReplaceTextSolutionsAction action = new ReplaceTextSolutionsAction(probs, searchFor, replaceWith);
                            SolutionEditor.getCmService().execute(action, new AsyncCallback<RpcData>() {
                                @Override
                                public void onSuccess(RpcData result) {
                                    CmMessageBox.showAlert("Replace was succesful");
                                    SolutionEditor.__instance._stepEditorViewer.loadSolution(SolutionEditor.__pidToLoad);
                                }

                                @Override
                                public void onFailure(Throwable caught) {
                                    com.google.gwt.user.client.Window.alert("Error: " + caught);
                                }

                            });
                        }
                    }
                });
            }
        });
        
    }


    private void setCallback(Callback callBack) {
        this.callBack = callBack;
    }

    ListView<SolutionSearchModel, String> _listResults;
    public void doSelect(String pid) {
        this.callBack.solutionSelected(pid);
        this._window.hide();
    }
    
    private void doView(String pid) {
    	new SolutionViewerFrame(pid);
    }

    private void doSearch() {
        String text = _searchField.getValue();
        String textFull = _searchFieldFull.getValue();
        boolean includeInActive=_includeInActive.getValue();

        SolutionEditor.__status.setText("Searching for: " + text + ", text: " + textFull);
        if ((text != null && text.length() > 0) || textFull != null && textFull.length() > 0) {

            SearchForSolutionsAction action = new SearchForSolutionsAction(text, textFull, includeInActive);
            SolutionEditor.getCmService().execute(action, new AsyncCallback<CmList<SolutionSearchModel>>() {
                @Override
                public void onSuccess(CmList<SolutionSearchModel> result) {
                    _matches.setText("Found: " + result.size());
                    SolutionEditor.__status.setText("");
                    showResults(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    com.google.gwt.user.client.Window.alert("Error: " + caught);
                }

            });
        }
    }

    private void showResults(List<SolutionSearchModel> models) {
        _listResults.getStore().clear();
        _listResults.getStore().addAll(models);
    }


    public interface Callback {
        void solutionSelected(String pid);
    }

    static SolutionSearcherDialog __searcherDialog;

    static public SolutionSearcherDialog getInstance(Callback callback) {
        if (__searcherDialog == null) {
            __searcherDialog = new SolutionSearcherDialog();
        }
        if(callback != null) {
            __searcherDialog.setCallback(callback);
        }
        
        return __searcherDialog;
    }
    
    
    

    static {
        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if (event.getEventType().equals(EventTypes.SOLUTION_LOAD_COMPLETE)) {
                    RecentPidStack pidStack = new RecentPidStack();
                    pidStack.addPid(SolutionEditor.__pidToLoad);
                    pidStack.save();
                }
            }
        });
    }




    public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                new SolutionSearcherDialog().showWindow();
            }
        });
    }    
    
}

class RecentTab extends SimplePanel {
    ListView<SolutionSearchModel, String> _listResults = new ListSolutionSearch();

    public RecentTab() {
        _listResults.addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                SolutionSearchModel item = _listResults.getSelectionModel().getSelectedItem();
                if (item != null) {
                	SolutionSearcherDialog.__searcherDialog.doSelect(item.getPid());
                }
            }
        }, DoubleClickEvent.getType());
        setWidget(_listResults);
    }

    public void refresh() {
        String recent = Storage.getLocalStorage().getItem("recent");
        List<SolutionSearchModel> models = getModels(recent);
        _listResults.getStore().clear();
        _listResults.getStore().addAll(models);
    }

    private List<SolutionSearchModel> getModels(String recent) {

        List<SolutionSearchModel> models = new ArrayList<SolutionSearchModel>();
        String recentList = Storage.getLocalStorage().getItem("recent");
        if (recentList != null) {
            String list[] = recentList.split("\\|");
            for (int i = 0; i < list.length; i++) {
                if (list[i] != null && list[i].length() > 0) {
                    String p[] = list[i].split("\\|");
                    String pid = p[0];
                    boolean isActive = p.length==2&&p[1].equals("true")?true:false;
                    models.add(new SolutionSearchModel(pid,isActive));
                }
            }
        }
        return models;
    }

}

/**
 * Control a recent visited list
 * 
 * @author casey
 * 
 */
class RecentPidStack {
    List<String> pids = new ArrayList<String>();

    public RecentPidStack() {
        String recentList = Storage.getLocalStorage().getItem("recent");
        if (recentList == null)
            recentList = "";

        String list[] = recentList.split("\\|");
        recentList = "";
        for (int i = 0; i < list.length; i++) {
            if (i > 20)
                break;
            pids.add(list[i]);
        }
    }

    public void addPid(String pid) {
        if (pids.contains(pid)) {
            pids.remove(pid);
        }
        pids.add(0, pid);  /** last visited should be first */
    }

    public void save() {
        String recentList = "";
        for (int i = 0, t = pids.size(); i < t; i++) {
            if (recentList.length() > 0) {
                recentList += "|";
            }
            recentList += pids.get(i);
        }
        Storage.getLocalStorage().setItem("recent", recentList);
    }
}