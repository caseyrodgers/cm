package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.model.SearchSuggestion;
import hotmath.gwt.cm_core.client.model.TopicSearchResults;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_core.client.util.CmBusyManager.BusyHandler;
import hotmath.gwt.cm_core.client.util.CmBusyManager.BusyState;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.TopicMatch;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchApp;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.ui.MyTextButton;
import hotmath.gwt.cm_tools.client.ui.SearchListViewTemplate.SearchBundle;
import hotmath.gwt.cm_tools.client.ui.SearchListViewTemplate.SearchStyle;
import hotmath.gwt.cm_tools.client.ui.search.TopicExplorer.TopicExplorerCallback;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
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
    
    
	SimpleContainer _explorerWrapper = new SimpleContainer();
    TextField _inputBox = new TextField();
    Grid<TopicMatch> _grid;
    private ContentPanel _westPanel;
    private BorderLayoutContainer _centerPanel;
    CenterLayoutContainer _centerPanelEmpty;

    
    public interface SeachSuggestionChoosen {
        void choosen(SearchSuggestion suggestion);
    }

    private List<SearchSuggestion> _currentSuggestions;

    TextButton _suggestionButton = new MyTextButton("?", new SelectHandler() {

        @Override
        public void onSelect(SelectEvent event) {
            showSearchSuggestions();
        }
    }, "Search suggestions");
    
    
    private BorderLayoutData _centerData;

    private BorderLayoutData _westData;
    public SearchPanel() {
        setNorthWidget(createHeader(), new BorderLayoutData(60));
        
        BorderLayoutContainer blcI = new BorderLayoutContainer();
        blcI.setCenterWidget(createListView());
//        blcI.setSouthWidget(new TextButton("Open", new SelectHandler() {
//            @Override
//            public void onSelect(SelectEvent event) {
//                exploreSelectedTopic();
//            }
//        }), new BorderLayoutData(30));
        
        _westPanel = new ContentPanel();
//        _westPanel.addTool(new TextButton("Explore", new SelectHandler() {
//            @Override
//            public void onSelect(SelectEvent event) {
//                exploreSelectedTopic();
//            }
//        }));
        _westPanel.setWidget(blcI);
        _westPanel.setEnabled(false);
        
        _westData = new BorderLayoutData(250);
        _westData.setSplit(true);
        _westData.setCollapsible(true);
        
        _centerData = new BorderLayoutData();
        _centerData.setSplit(true);
        _centerData.setCollapsible(true);
        
        _centerPanelEmpty = new CenterLayoutContainer();
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.setWidth(320);
        String html = "<p style='font-size: 1.3em;padding: 5px;color: #666;'>" +
                      "Enter a word or phrase in the textbox. Then select a lesson to explore the text, video, activity and practice problems. " +
                      "</p>";
        flow.add(new HTML(html));
        _centerPanelEmpty.setWidget(flow);

        
        _centerPanel = new BorderLayoutContainer();
        _centerPanel.setCenterWidget(_explorerWrapper);
        enableMainArea(false);
        
        
        
        startInputWatcherTimer();
        
    }
    
    /** start a timer to watch the _inputText for changes.
     * 
     * If there are changes automatically execute the search .. 
    
     */
    String _lastSearchValue;
    Timer _watchTimer=null;
    private void startInputWatcherTimer() {
        if(_watchTimer==null) {
            _watchTimer = new Timer() {
                @Override
                public void run() {
                    if(isVisible()) {
                        System.out.println("Checking search box for changes...");
                        String thisSearch = _inputBox.getCurrentValue();
                        if(_lastSearchValue != null) {
                            if(thisSearch != null) {
                                if(!thisSearch.equals(_lastSearchValue)) {
                                    doSearch(thisSearch);
                                }
                            }
                        }
                        _lastSearchValue = thisSearch;
                    }
                }
            };
            _watchTimer.scheduleRepeating(2000);
        }
    }

    interface GridProps extends PropertyAccess<TopicMatch> {
        ModelKeyProvider<TopicMatch> topicFile();
        ValueProvider<TopicMatch, String> topicName();
    }
    
    interface ListViewTemplate extends XTemplates {
        // @XTemplate("<div class='{style.searchItem}'><h3><span>{post.date:date(\"M/d/yyyy\")}<br />by {post.author}</span>{post.title}</h3>{post.excerpt}</div>")
        // @XTemplate("<div class='{style.searchItem}'><h3>{post.name}</span></h3>{post.excerpt}</div>")
        @XTemplate("<div class='{style.searchItem}'>{post.topicName}</span></div>")
        SafeHtml render(TopicMatch post, SearchStyle style);
    }
    final ListViewTemplate template = GWT.create(ListViewTemplate.class);
    
    GridProps props = GWT.create(GridProps.class);
    private HTML _searchMessage; 
    private HorizontalLayoutContainer _suggestionsContainer;
    private Widget createListView() {
        final SearchBundle b = GWT.create(SearchBundle.class);
        b.css().ensureInjected();
        final ListStore<TopicMatch> gstore = new ListStore<TopicMatch>(props.topicFile());
        List<ColumnConfig<TopicMatch, ?>> cols = new ArrayList<ColumnConfig<TopicMatch,?>>();
        ColumnConfig<TopicMatch, String> col = new ColumnConfig<TopicMatch, String>(props.topicName(), 160,  "Lesson");
        col.setCell(new AbstractCell<String>() {
            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
                TopicMatch tm = gstore.getAll().get(context.getIndex());
                String tip = tm.getTopicName() + " [" + tm.getMatchWeight() + "]";
                tip = tip.replace("'",  "");
                sb.appendHtmlConstant("<span class='topic-list-item' qtitle='Topic' qtip='" + tip + "'>" + value + "</span>");
            }
        });
        
        cols.add(col);
        
        ColumnModel<TopicMatch> colModel = new ColumnModel<TopicMatch>(cols);
        
        _grid = new Grid<TopicMatch>(gstore, colModel);
        _grid.getView().setAutoFill(true);
        _grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        _grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<TopicMatch>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<TopicMatch> event) {
               showSelectedReview();
            }
        });
        
        
        new QuickTip(_grid);
        
        return _grid;
    }
    
    TopicExplorerCallback _theCallback = new TopicExplorerCallback() {
        @Override
        public void resourceIsLoaded() {
            _explorerWrapper.forceLayout();
        }
    };
    
    
    private void showSearchSuggestions() {
        new SearchSuggestionPopup(_currentSuggestions, new SeachSuggestionChoosen() {
            @Override
            public void choosen(SearchSuggestion suggestion) {
                if(suggestion != null) {
                    _inputBox.setText(suggestion.getSuggestion());
                    doSearch(suggestion.getSuggestion());
                }
            }
        }).show(_suggestionsContainer);
    }
    protected void showSelectedReview() {
    	TopicMatch si = _grid.getSelectionModel().getSelectedItem();
    	if(si != null) {
    	    TopicExplorer topicExplorer = new TopicExplorer(si.getTopic(), _theCallback);
    	    _explorerWrapper.setWidget(topicExplorer);
    	    _explorerWrapper.forceLayout();
    		// _reviewPanel.loadReview(new InmhItemData(CmResourceType.REVIEW, si.getTopic().getFile(), si.getTopic().getName()));
    	}
	}

	protected void exploreSelectedTopic() {
		
		// InmhItemData item = null;// _reviewPanel.getItem();
	    TopicMatch topic = _grid.getSelectionModel().getSelectedItem();
        if(topic == null) {
            CmMessageBox.showAlert("No lesson selected.");
        }
        else {
			TopicExplorerManager.getInstance().exploreTopic(topic.getTopic());
        }
    }

	
	interface ComboProps extends PropertyAccess<String> {

	    @Path("suggestion")
        ModelKeyProvider<SearchSuggestion> key();
        LabelProvider<SearchSuggestion> suggestion();
	}
	
	ComboProps comboProps = GWT.create(ComboProps.class);
    private MyFieldLabel searchField;
    private ComboBox<SearchSuggestion> _searchCombo;
    private Anchor _searchAnchor;
	
    private Widget createHeader() {
        _searchMessage = new HTML();
        _suggestionsContainer = new HorizontalLayoutContainer();
        _suggestionsContainer.addStyleName("suggestions-container");
        
        _searchAnchor = new Anchor("There are search suggestions");
        _searchAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showSearchSuggestions();
            }
        });
        
        // String cssStyle = "position: absolute;top: 23px;left: 140px;width: 200px;";
        //_suggestionsContainer.getElement().setAttribute("style",  cssStyle);
        
        FramedPanel fp = new FramedPanel();
        fp.setHeaderVisible(false);
        
        
//        ListStore<SearchSuggestion> comboStore = new ListStore<SearchSuggestion>(comboProps.key());
//        _searchCombo = new ComboBox<SearchSuggestion>(comboStore,comboProps.suggestion());
//        comboStore.add(new SearchSuggestion("Test 1"));
//        comboStore.add(new SearchSuggestion("Test 2"));
        
        TextButton searchBtn = new TextButton("Search!", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                searchForMatches();
            }
        });

        _inputBox.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                
                if(event.getNativeEvent().getKeyCode() == 13) {
                    searchForMatches();
                }
                
                if(_searchMessage.getText().length() > 0) {
                   // _searchMessage.setText("");
                }
            }
        });
        
        _inputBox.setToolTip("Enter a word or phrase in the textbox. Then select a lesson to explore the text, video, activity and practice problems.");
        
        HorizontalLayoutContainer hl = new HorizontalLayoutContainer();
        hl.setHeight(20);

        hl.add(new MyFieldLabel(_inputBox, "Lesson Search", 85, 250));
        
//        HorizontalLayoutData hld = new HorizontalLayoutData();
//        _suggestionButton.setEnabled(false);
//        hl.add(_suggestionButton,hld );
        
        HorizontalLayoutData hld = new HorizontalLayoutData(20,20);
        hld.setMargins(new Margins(0,0,0,4));
        hl.add(searchBtn, hld);
        
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.add(hl);
        flow.add(_searchMessage);
        flow.add(_suggestionsContainer);
        fp.setWidget(flow);
        
        
        return fp;
    }
    
    
    private void searchForMatches() {
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
        if(CmCore.isDebug()) {
            Info.display("Searching", "Searching for matches ..");
        }

        int uid = UserInfoBase.getInstance().getUid();
        SearchApp appType = UserInfoBase.getInstance().isAdmin()?SearchApp.CM_ADMIN:SearchApp.CM_STUDENT;
        SearchTopicAction action = new SearchTopicAction(searchFor,appType,uid);
        
        showSearchMessage("Searching ...");
        CmBusyManager.setBusy(true);
        CmRpcCore.getCmService().execute(action, new AsyncCallback<TopicSearchResults>() {
            @Override
            public void onSuccess(TopicSearchResults result) {
                CmBusyManager.setBusy(false);
                
                CmList<TopicMatch> topics = result.getTopics();
                Log.debug("search ('" + searchFor + "') matches: " + topics.size());
                _grid.getStore().clear();
                _grid.getStore().clearSortInfo();
                
                _grid.getStore().addAll(topics);

                
                _suggestionsContainer.clear();
                _currentSuggestions = result.getSuggestions();
                if(result.getSuggestions().size() > 0) {
                    _suggestionButton.setEnabled(true);
                    
                    int cnt = result.getSuggestions().size();
                    String msg = "Here ";
                    msg += (cnt == 1)?"is ":"are ";
                    msg += cnt;
                    msg += " search suggestion";
                    msg += (cnt > 1)?"s":"";
                    _searchAnchor.setText(msg);
                    _suggestionsContainer.add(_searchAnchor);
                }
                else {
                    _suggestionButton.setEnabled(false);
                }
                
                if(topics.size() > 0) {
                    _grid.getSelectionModel().select(topics.get(0), false);
                    enableMainArea(true);
                    
                    showSearchMessage("Found " + topics.size() + " " + (topics.size()==1?"match":"matches"));
                }
                else {
                    enableMainArea(false);
                    showSearchMessage("No matches found.");
                }
            }
            
            
            
            public void onFailure(Throwable error) {
                CmBusyManager.resetBusy();
                error.printStackTrace();
                Log.error("Error", error);

                CmMessageBox.showAlert("There was a problem processing your search.  Check Help for some tips.");
            }
        });
    }
    
    
    protected void showSearchMessage(String message) {
        _searchMessage.setHTML("<div style='margin-top: 3px;color: red;font-weight: bold;'>" + message + "</div>");
        forceLayout();
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
        CmBusyManager.setBusyHandler(new BusyHandler() {
            
            @Override
            public void showMask(BusyState state) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void hideMask() {
                // TODO Auto-generated method stub
                
            }
        });
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

    public void setInputFocus() {
        _inputBox.focus();
    }
   

}
