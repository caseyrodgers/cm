package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.TopicMatch;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.ReviewPanel.ReviewPanelCallback;
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
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
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
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
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
	ReviewPanel _reviewPanel = new ReviewPanel(new ReviewPanelCallback() {
        @Override
        public void exporeTopic(InmhItemData item) {
            TopicExplorerManager.getInstance().exploreTopic(new hotmath.gwt.cm_rpc.client.model.Topic(item.getTitle(), item.getFile(), ""));
        }
    });
	
    TextField _inputBox = new TextField();
    Grid<TopicMatch> _grid;
    private ContentPanel _westPanel;
    private BorderLayoutContainer _centerPanel;
    CenterLayoutContainer _centerPanelEmpty;

    private BorderLayoutData _centerData;

    private BorderLayoutData _westData;
    public SearchPanel() {
        setNorthWidget(createHeader(), new BorderLayoutData(60));
        
        BorderLayoutContainer blcI = new BorderLayoutContainer();
        blcI.setCenterWidget(createListView());
        blcI.setSouthWidget(new TextButton("Explore Lesson", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                exploreSelectedTopic();
            }
        }), new BorderLayoutData(30));
        
        _westPanel = new ContentPanel();
        _westPanel.setHeadingHtml("Click Explore see all lesson resources");
        _westPanel.addTool(new TextButton("Explore", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                exploreSelectedTopic();
            }
        }));
        _westPanel.setWidget(blcI);
        _westPanel.setEnabled(false);
        
        _westData = new BorderLayoutData(350);
        _westData.setSplit(true);
        _westData.setCollapsible(true);
        
        _centerData = new BorderLayoutData();
        _centerData.setSplit(true);
        //centerData.setCollapsible(true);
        
        _centerPanelEmpty = new CenterLayoutContainer();
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.setPixelSize(320,  50);
        String html = "<p style='padding: 5px;color: #666;'>" +
                      "Enter just a few letters of the lesson " +
                      "you are searching for in the textbox above.  Once your lesson is found you can 'explore' its resources." +
                      "</p>";
        flow.add(new HTML(html));
        _centerPanelEmpty.setWidget(flow);

        
        _centerPanel = new BorderLayoutContainer();
        _centerPanel.setCenterWidget(_reviewPanel);
        _centerPanel.setSouthWidget(new HTML("<div style='text-align: center;margin-top: 5px;font-size: .9em;font-style: italic'>Press Explore Lesson to see all the lesson resources</div>"), new BorderLayoutData(30));
        
        enableMainArea(false);
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
    private HTML _searchMessage; 
    private Widget createListView() {
        final SearchBundle b = GWT.create(SearchBundle.class);
        b.css().ensureInjected();
        final ListStore<TopicMatch> gstore = new ListStore<TopicMatch>(props.file());
        List<ColumnConfig<TopicMatch, ?>> cols = new ArrayList<ColumnConfig<TopicMatch,?>>();
        ColumnConfig<TopicMatch, String> col = new ColumnConfig<TopicMatch, String>(props.topicName(), 160,  "Lesson");
        col.setCell(new AbstractCell<String>() {
            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
                TopicMatch tm = gstore.getAll().get(context.getIndex());
                
                String tip = value + " [" + tm.getMatchWeight() + "]";
                sb.appendHtmlConstant("<span class='topic-list-item' qtitle='Topic' qtip='" + tip + "'>" + value + "</span>");
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
    		_reviewPanel.loadReview(new InmhItemData(CmResourceType.REVIEW, si.getTopic().getFile(), si.getTopic().getName()));
    	}
	}

	protected void exploreSelectedTopic() {
        TopicMatch topic = _grid.getSelectionModel().getSelectedItem();
        if(topic == null) {
            CmMessageBox.showAlert("No lesson selected.");
        }
        else {
            TopicExplorerManager.getInstance().exploreTopic(topic.getTopic());
        }
    }

    private Widget createHeader() {
        FramedPanel fp = new FramedPanel();
        fp.setHeaderVisible(false);
        
        
        BorderLayoutContainer blc = new BorderLayoutContainer();
        blc.setCenterWidget(new FieldLabel(_inputBox, "Lesson Search"));
        BorderLayoutData bld = new BorderLayoutData(50);
        bld.setMargins(new Margins(0,0,0,10));
        blc.setEastWidget(new TextButton("Search!", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                searchForMatches();
            }
        }),bld);
        _searchMessage = new HTML();
        blc.setSouthWidget(_searchMessage, new BorderLayoutData(25));
        
        fp.setWidget(blc);
        
        _inputBox.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                
                if(event.getNativeEvent().getKeyCode() == 13) {
                    searchForMatches();
                }
                
                if(_searchMessage.getText().length() > 0) {
                    _searchMessage.setText("");
                }
            }
        });
        
        _inputBox.setToolTip("Enter a word or phrase associated with a Lesson.   Press ENTER or click the Search button to find matches.");
        return fp;
    }
    
    
    private void searchForMatches() {
        if(CmCore.isDebug()) {
            Info.display("Searching", "Searching for matches ..");
        }
        showSearchMessage("");
        String searchFor = _inputBox.getCurrentValue();
        if(searchFor == null || searchFor.length() < 2) {
            showSearchMessage("Enter at least two letters.");
        }
        else {
            doSearch(searchFor);
        }
    }
    
    
    private void doSearch(final String searchFor) {
        SearchTopicAction action = new SearchTopicAction(searchFor);
        
        showSearchMessage("Searching ...");
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
                    enableMainArea(true);
                    
                    showSearchMessage("Found " + result.size() + " " + (result.size()==1?"match":"matches"));
                }
                else {
                    enableMainArea(false);
                    showSearchMessage("No matches found.");
                }
            }
            
            @Override
            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                caught.printStackTrace();
                Log.error("Error", caught);
                CmMessageBox.showAlert("There was a problem getting matches" + caught.getMessage());
            }
        });
    }
    
    
    protected void showSearchMessage(String message) {
        _searchMessage.setHTML("<div style='margin-top: 3px;color: red;font-weight: bold;'>" + message + "</div>");
    }

    protected void enableMainArea(boolean b) {
        if(!b) {
            remove(_westPanel);
            setCenterWidget(_centerPanelEmpty);
        }
        else {
            _centerPanel.setEnabled(true);
            _westPanel.setEnabled(true);
            
            setWestWidget(_westPanel, _westData);
            setCenterWidget(_centerPanel, _centerData);
        }
        
        forceLayout();
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
