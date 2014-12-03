package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.TopicMatch;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.SearchListViewTemplate.SearchBundle;
import hotmath.gwt.cm_tools.client.ui.SearchListViewTemplate.SearchStyle;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

/** panel that has a static text input field, 
 *  a list of matches below
 *  
 * @author casey
 *
 */



public class SearchPanel extends BorderLayoutContainer {
	ReviewPanel _reviewPanel = new ReviewPanel(new CallbackOnComplete() {
        @Override
        public void isComplete() {
            exploreSelectedTopic();
        }
    });
    TextField _inputBox = new TextField();
    Grid<TopicMatch> _grid;
    
    public SearchPanel() {
        setNorthWidget(createHeader(), new BorderLayoutData(40));
        
        BorderLayoutContainer blcI = new BorderLayoutContainer();
        blcI.setCenterWidget(createListView());
        blcI.setSouthWidget(new TextButton("Explore Topic", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                exploreSelectedTopic();
            }
        }), new BorderLayoutData(30));
        
        
        BorderLayoutData eastData = new BorderLayoutData(350);
        eastData.setSplit(true);
        eastData.setCollapsible(true);
        
        ContentPanel cp = new ContentPanel();
        cp.setWidget(blcI);
        setWestWidget(cp, eastData);
        
        BorderLayoutData centerData = new BorderLayoutData();
        centerData.setSplit(true);
        //centerData.setCollapsible(true);
        
        setCenterWidget(_reviewPanel, centerData);
    }
    
    interface Props extends PropertyAccess<TopicMatch> {
        @Path("topic.file")
        ModelKeyProvider<TopicMatch> file();
        ValueProvider<TopicMatch, String> topicName();
    }
    
    
    
    interface ListViewTemplate extends XTemplates {
        // @XTemplate("<div class='{style.searchItem}'><h3><span>{post.date:date(\"M/d/yyyy\")}<br />by {post.author}</span>{post.title}</h3>{post.excerpt}</div>")
        // @XTemplate("<div class='{style.searchItem}'><h3>{post.name}</span></h3>{post.excerpt}</div>")
        @XTemplate("<div class='{style.searchItem}'>{post.topicName}</span></div>")
        SafeHtml render(TopicMatch post, SearchStyle style);
    }
    final ListViewTemplate template = GWT.create(ListViewTemplate.class);
    
    Props props = GWT.create(Props.class); 
    private Widget createListView() {
        final SearchBundle b = GWT.create(SearchBundle.class);
        b.css().ensureInjected();
        final ListStore<TopicMatch> gstore = new ListStore<TopicMatch>(props.file());
        List<ColumnConfig<TopicMatch, ?>> cols = new ArrayList<ColumnConfig<TopicMatch,?>>();
        ColumnConfig<TopicMatch, String> col = new ColumnConfig<TopicMatch, String>(props.topicName(), 160,  "Topic");
        col.setCell(new AbstractCell<String>() {
            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
                TopicMatch tm = gstore.getAll().get(context.getIndex());
                
                sb.appendHtmlConstant("<h1 qtitle='Topic' qtip='" + value + "'>" + value + "</h1>");
            }
        });
        
        cols.add(col);
        
        ColumnModel<TopicMatch> colModel = new ColumnModel<TopicMatch>(cols);
        
        _grid = new Grid<TopicMatch>(gstore, colModel);
        _grid.getView().setAutoFill(true);
        _grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        _grid.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                exploreSelectedTopic();
            }
        }, DoubleClickEvent.getType());

        
        _grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<TopicMatch>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<TopicMatch> event) {
                showSelectedReview();
            }
        });
        
        
        new QuickTip(_grid);
        
        return _grid;
    }
    
    protected void showSelectedReview() {
    	TopicMatch si = _grid.getSelectionModel().getSelectedItem();
    	if(si != null) {
    		_reviewPanel.loadReview(si.getTopic().getFile(), si.getTopic().getName());
    	}
	}

	protected void exploreSelectedTopic() {
        TopicMatch topic = _grid.getSelectionModel().getSelectedItem();
        if(topic == null) {
            CmMessageBox.showAlert("No topic selected.");
        }
        else {
            TopicExplorerManager.getInstance().exploreTopic(topic.getTopic());
        }
    }

    private Widget createHeader() {
        FramedPanel fp = new FramedPanel();
        fp.setHeaderVisible(false);
        
        
        BorderLayoutContainer blc = new BorderLayoutContainer();
        blc.setCenterWidget(new FieldLabel(_inputBox, "Topic Search"));
        BorderLayoutData bld = new BorderLayoutData(50);
        bld.setMargins(new Margins(0,0,0,10));
        blc.setEastWidget(new TextButton("Search!", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                searchForMatches();
            }
        }),bld);
        fp.setWidget(blc);
        
        _inputBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                searchForMatches();
            }
        });
        
        _inputBox.setToolTip("Enter a word or phrase associated with a Topic.   Press ENTER or click the Search button to find matches.");
        return fp;
    }
    
    
    private void searchForMatches() {
        String searchFor = _inputBox.getCurrentValue();
        if(searchFor == null || searchFor.length() < 2) {
            CmMessageBox.showAlert("Enter at least two letters.");
        }
        else {
            doSearch(searchFor);
        }
    }
    
    
    private void doSearch(final String searchFor) {
        SearchTopicAction action = new SearchTopicAction(searchFor);
        
        CmBusyManager.setBusy(true);
        CmShared.getCmService().execute(action, new AsyncCallback<CmList<TopicMatch>>() {
            @Override
            public void onSuccess(CmList<TopicMatch> result) {
                CmBusyManager.setBusy(false);
                Log.debug("search ('" + searchFor + "') matches: " + result.size());
                _grid.getStore().clear();
                _grid.getStore().clearSortInfo();
                
                _grid.getStore().addAll(result);
                
                if(result.size() > 0) {
                    _grid.getSelectionModel().select(result.get(0), false);
                }
            }
            
            @Override
            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                caught.printStackTrace();
                Log.error("Error", caught);
                CmMessageBox.showAlert("There was a problem getting matches");
            }
        });
    }
    
    
    static public void startTest() {
        new GwtTester(new TestWidget() {
           @Override
           public void runTest() {
               GWindow w = new GWindow(true);
               w.setPixelSize(600, 480);
               w.setWidget(new SearchPanel());
               w.setVisible(true);
           }
       });
    }
   

}
