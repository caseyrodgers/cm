package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.CmEventListener;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.solution_editor.client.rpc.SearchForSolutionsAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SolutionSearcherDialog extends Window {
    Callback callBack;
    String pid;
    Label _matches = new Label();

    TabPanel _tabPanel = new TabPanel();
    RecentTab _tabRecent;
    TextField<String> _searchField = new TextField<String>();
    TextField<String> _searchFieldFull = new TextField<String>();


    private SolutionSearcherDialog() {
        pid = Cookies.getCookie("last_pid");
        setSize(500, 400);
        addStyleName("solution-searcher-dialog");
        setHeading("Solution Searcher Dialog");

        TabItem tabItem = new TabItem("Search");
        tabItem.setLayout(new BorderLayout());
        
        FormPanel fPanel = new FormPanel();
        fPanel.setFieldWidth(300);
        fPanel.setLabelWidth(100);
        fPanel.addButton(new Button("Search", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                doSearch();
            }
        }));

        _searchField.setFieldLabel("Search PIDs");
        _searchField.addKeyListener(new KeyListener() {
            @Override
            public void componentKeyUp(ComponentEvent event) {
                int kc = event.getKeyCode();
                if (kc == 13)
                    doSearch();
            }
        });
        fPanel.add(_searchField);
        
        _searchFieldFull.setFieldLabel("Text Search");
        _searchFieldFull.addKeyListener(new KeyListener() {
            @Override
            public void componentKeyUp(ComponentEvent event) {
                int kc = event.getKeyCode();
                if (kc == 13)
                    doSearch();
            }
        });        
        fPanel.add(_searchFieldFull);
        
        tabItem.add(fPanel, new BorderLayoutData(LayoutRegion.NORTH, 95));
        fPanel.setFrame(false);
        fPanel.setBodyBorder(false);
        fPanel.setHeaderVisible(false);

        ListStore<SolutionSearchModel> store = new ListStore<SolutionSearchModel>();
        _listResults.setStore(store);
        _listResults.setTemplate(getTemplate());
        tabItem.add(_listResults, new BorderLayoutData(LayoutRegion.CENTER));

        _listResults.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                doSelect(_listResults.getSelectionModel().getSelectedItem().getPid());
            }
        });

        tabItem.add(_matches, new BorderLayoutData(LayoutRegion.SOUTH, 5));

        addButton(new Button("Select", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if (_tabPanel.getSelectedItem().getText().equals("Recent")) {
                    doSelect(_tabRecent._listResults.getSelectionModel().getSelectedItem().getPid());
                } else {
                    doSelect(_listResults.getSelectionModel().getSelectedItem().getPid());
                }
            }
        }));

        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));

        setLayout(new FitLayout());
        _tabPanel.add(tabItem);

        tabItem = new TabItem("Recent");
        _tabRecent = new RecentTab();
        tabItem.setLayout(new FitLayout());
        tabItem.add(_tabRecent);

        _tabPanel.add(tabItem);

        add(_tabPanel);

        _tabPanel.addListener(Events.Select, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if (_tabPanel.getSelectedItem().getText().equals("Recent")) {
                    _tabRecent.refresh();
                }
            }
        });

        setVisible(true);
    }

    private void setCallback(Callback callBack) {
        this.callBack = callBack;
    }

    ListView<SolutionSearchModel> _listResults = new ListView<SolutionSearchModel>();
    public void doSelect(String pid) {
        this.callBack.solutionSelected(pid);
        this.hide();
    }

    private void doSearch() {
        String text = _searchField.getValue();
        String textFull = _searchFieldFull.getValue();

        SolutionEditor.__status.setText("Searching for: " + text + ", text: " + textFull);
        if (text != null && text.length() > 0) {

            SearchForSolutionsAction action = new SearchForSolutionsAction(text, textFull);
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
        _listResults.getStore().removeAll();
        _listResults.getStore().add(models);

        layout();
    }

    static public native String getTemplate() /*-{ 
                                              return [ 
                                              '<tpl for="."><div class="x-view-item">', 
                                              '<h3><span>{pid}</span></h3>', 
                                              '</div></tpl>' 
                                              ].join(""); 
                                              }-*/;

    public interface Callback {
        void solutionSelected(String pid);
    }

    static SolutionSearcherDialog __searcherDialog;

    static public void showSharedDialog(Callback callback) {
        if (__searcherDialog == null) {
            __searcherDialog = new SolutionSearcherDialog();
        }
        __searcherDialog.setCallback(callback);
        __searcherDialog.setVisible(true);
    }
}

class RecentTab extends LayoutContainer {
    ListView<SolutionSearchModel> _listResults = new ListView<SolutionSearchModel>();

    public RecentTab() {

        setLayout(new FitLayout());
        ListStore<SolutionSearchModel> store = new ListStore<SolutionSearchModel>();
        _listResults.setStore(store);
        _listResults.setTemplate(SolutionSearcherDialog.getTemplate());

        _listResults.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                SolutionSearcherDialog.__searcherDialog.doSelect(_listResults.getSelectionModel().getSelectedItem()
                        .getPid());
            }
        });
        add(_listResults);
    }

    public void refresh() {
        String recent = Storage.getLocalStorage().getItem("recent");
        List<SolutionSearchModel> models = getModels(recent);
        _listResults.getStore().removeAll();
        _listResults.getStore().add(models);

        layout();
    }

    private List<SolutionSearchModel> getModels(String recent) {

        List<SolutionSearchModel> models = new ArrayList<SolutionSearchModel>();
        String recentList = Storage.getLocalStorage().getItem("recent");
        if (recentList != null) {
            String list[] = recentList.split("\\|");
            for (int i = 0; i < list.length; i++) {
                if (list[i] != null && list[i].length() > 0)
                    models.add(new SolutionSearchModel(list[i]));
            }
        }
        return models;
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