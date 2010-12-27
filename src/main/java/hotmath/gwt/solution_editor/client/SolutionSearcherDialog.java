package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.solution_editor.client.rpc.SearchForSolutionsAction;

import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SolutionSearcherDialog extends Window {
    Callback callBack;
    String pid;
    Label _matches = new Label();

    private SolutionSearcherDialog() {
        pid = Cookies.getCookie("last_pid");
        setSize(500, 400);
        addStyleName("solution-searcher-dialog");
        setHeading("Solution Searcher Dialog");

        setLayout(new BorderLayout());

        HorizontalPanel top = new HorizontalPanel();
        top.add(new Html("<span class='label'>Search PIDs: </span>"));
        top.add(_searchField);
        top.add(new Button("Search", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                doSearch();
            }
        }));
        
        _searchField.setWidth(150);
        _searchField.addKeyListener(new KeyListener() {
            @Override
            public void componentKeyUp(ComponentEvent event) {
                int kc = event.getKeyCode();
                if(kc == 13)
                    doSearch();
            }
        });

        add(top, new BorderLayoutData(LayoutRegion.NORTH,40));
        
        ListStore<SolutionSearchModel> store = new ListStore<SolutionSearchModel>();
        _listResults.setStore(store);
        _listResults.setTemplate(getTemplate());
        add(_listResults, new BorderLayoutData(LayoutRegion.CENTER));
        

        _listResults.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                doSelect();
            }
        });
        
        add(_matches, new BorderLayoutData(LayoutRegion.SOUTH,5));
        
        addButton(new Button("Select", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                doSelect();
            }
        }));

        
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        
        setVisible(true);
    }
    
    private void setCallback(Callback callBack) {
        this.callBack = callBack;
    }

    ListView<SolutionSearchModel> _listResults = new ListView<SolutionSearchModel>();
    TextField<String> _searchField = new TextField<String>();


    private void doSelect() {
        String text = _listResults.getSelectionModel().getSelectedItem().getPid();
        this.callBack.solutionSelected(text);
        this.hide();
    }

    private void doSearch() {
        String text = _searchField.getValue();
        
        SolutionEditor._status.setText("Searching for: " + text);
        if (text != null && text.length() > 0) {
            
            SearchForSolutionsAction action = new SearchForSolutionsAction(text);
            SolutionEditor.getCmService().execute(action, new AsyncCallback<CmList<SolutionSearchModel>>() {
                @Override
                public void onSuccess(CmList<SolutionSearchModel> result) {
                    _matches.setText("Found: " + result.size());
                    SolutionEditor._status.setText("");
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

    private native String getTemplate() /*-{ 
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
        if(__searcherDialog == null) {
            __searcherDialog = new SolutionSearcherDialog();
        }
        __searcherDialog.setCallback(callback);
        __searcherDialog.setVisible(true);
    }
}
