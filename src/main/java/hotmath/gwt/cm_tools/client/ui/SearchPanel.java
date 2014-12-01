package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.SearchListViewTemplate.SearchBundle;
import hotmath.gwt.cm_tools.client.ui.SearchListViewTemplate.SearchStyle;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

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
    ListView<Topic, Topic> _listView;
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
        // eastData.setCollapsible(true);
        setEastWidget(_reviewPanel, eastData);
        
        BorderLayoutData centerData = new BorderLayoutData();
        centerData.setSplit(true);
        // centerData.setCollapsible(true);
        
        setCenterWidget(blcI, centerData);
    }
    
    interface Props extends PropertyAccess<Topic> {
        ModelKeyProvider<Topic> file();
        ValueProvider<Topic, Topic> name();
    }
    
    
    
    interface ListViewTemplate extends XTemplates {
        // @XTemplate("<div class='{style.searchItem}'><h3><span>{post.date:date(\"M/d/yyyy\")}<br />by {post.author}</span>{post.title}</h3>{post.excerpt}</div>")
        // @XTemplate("<div class='{style.searchItem}'><h3>{post.name}</span></h3>{post.excerpt}</div>")
        @XTemplate("<div class='{style.searchItem}'><h3>{post.name}</span></h3></div>")
        SafeHtml render(Topic post, SearchStyle style);
    }
    final ListViewTemplate template = GWT.create(ListViewTemplate.class);
    
    Props props = GWT.create(Props.class); 
    private Widget createListView() {
        ListStore<Topic> store = new ListStore<Topic>(props.file());
        
        final SearchBundle b = GWT.create(SearchBundle.class);
        b.css().ensureInjected();

        _listView = new ListView<Topic, Topic>(store, new IdentityValueProvider<Topic>());
        _listView.setCell(new AbstractCell<Topic>() {
            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, Topic value, SafeHtmlBuilder sb) {
                sb.append(template.render(value,b.css()));
            }
        });
        
        _listView.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                exploreSelectedTopic();
            }
        }, DoubleClickEvent.getType());
        
        
        _listView.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<Topic>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<Topic> event) {
				showSelectedReview();
			}
        });
        return _listView;
    }
    
    protected void showSelectedReview() {
    	Topic si = _listView.getSelectionModel().getSelectedItem();
    	if(si != null) {
    		_reviewPanel.loadReview(si.getFile(), si.getName());
    	}
	}

	protected void exploreSelectedTopic() {
        Topic topic = _listView.getSelectionModel().getSelectedItem();
        if(topic == null) {
            CmMessageBox.showAlert("No topic selected.");
        }
        else {
            TopicExplorerManager.getInstance().exploreTopic(topic);
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
        CmShared.getCmService().execute(action, new AsyncCallback<CmList<Topic>>() {
            @Override
            public void onSuccess(CmList<Topic> result) {
                CmBusyManager.setBusy(false);
                Log.debug("search ('" + searchFor + "') matches: " + result.size());
                _listView.getStore().clear();
                _listView.getStore().addAll(result);
                
                if(result.size() > 0) {
                    _listView.getSelectionModel().select(result.get(0), false);
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
