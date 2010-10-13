package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.CmEventListener;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetMobileTopicListAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.ViewSettings;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

import java.util.ArrayList;
import java.util.List;

import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TopicListPagePanel extends AbstractPagePanel {
    TopicListPage page;
    FlowPanel topicPanel = new FlowPanel();
    static CmList<Topic> topics;
    public TopicListPagePanel(TopicListPage page) {
        this.page = page;

        topicPanel.add(new HTML("Loading topic list ..."));
        initWidget(topicPanel);
        
        loadTopicList();
    }

    TabPanel _tabPanel = new DecoratedTabPanel();
    FlowPanel matches = new FlowPanel();
    TextBox _searchText;
    HistoryPanel _historyPanel = new HistoryPanel();
    LessonsPrescribedPanel _assignedPanel = new LessonsPrescribedPanel();
    
    /** Draw the gui showing the topic list
     * 
     * @param topics
     */
    private void drawGui(CmList<Topic> topics) {
        
        FlowPanel top = new FlowPanel();
        
        final GenericContainerTag searchBox = new GenericContainerTag("div");
        searchBox.setStyleName("search-box");
        _searchText = new TextBox();
        _searchText.addKeyUpHandler(new KeyUpHandler() {
            
            @Override
            public void onKeyUp(KeyUpEvent event) {
                searchForMatches(_searchText.getValue());                
            }
        });
        
        String html = "<p>Type in the search field to narrow down the list of lessons.</p>";
        searchBox.add(new HTML(html));
        searchBox.add(_searchText);
        searchBox.add(new Button(">>",new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final PopupPanel popup = new PopupPanel();
                popup.setAutoHideEnabled(true);
                popup.setModal(true);
                
                FlowPanel fp = new FlowPanel();
                
                final ListBox lb = new ListBox();
                lb.getElement().setId("search-list-items");
                lb.addItem("-- Previous Entries --");
                List<String> previousEntries = getPreviousEntries();
                for(int i=0,t=previousEntries.size();i<t;i++) {
                    lb.addItem(previousEntries.get(i));
                }
                lb.setVisibleItemCount(4);
                lb.setWidth("300px;");
                lb.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        String txt = lb.getItemText(lb.getSelectedIndex());
                        popup.hide();
                        _searchText.setText(txt);
                        searchForMatches(txt);
                    }
                });
                fp.add(lb);

                popup.setWidget(lb);
                popup.center();
                
                popup.show();
            }
        }));;
        
        top.add(searchBox);
        
        topicPanel.clear();
        _tabPanel.clear();
        topicPanel.add(_tabPanel);
        
        FlowPanel searchPanel = new FlowPanel();
        searchPanel.add(top);
        searchPanel.add(matches);
        _tabPanel.add(searchPanel, "Search");
        _tabPanel.add(_historyPanel, "History");
        _tabPanel.add(_assignedPanel, "Assigned");
        _tabPanel.setAnimationEnabled(true);

        _tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                if(event.getSelectedItem() == 1) {
                    /** history tab */
                    Widget w = _tabPanel.getWidget(event.getSelectedItem());
                    if(w == _historyPanel) {
                        if(!_historyPanel.isInitialized()) {
                            _historyPanel.initialize();
                        }
                    }
                }
                else if(event.getSelectedItem() == 2) {
                    /** assigned tab */
                    Widget w = _tabPanel.getWidget(event.getSelectedItem());
                    if(w == _assignedPanel) {
                        if(!_assignedPanel.isInitialized()) {
                            _assignedPanel.initialize();
                        }
                    }
                }
            }
        });
        
        _tabPanel.selectTab(0);

        
        
        /** Try to initialize to last search 
         * 
         */
        if(getPreviousEntries().size() > 0) {
            _searchText.setValue(getPreviousEntries().get(getPreviousEntries().size()-1));
            searchForMatches(_searchText.getValue());
        }
        else {
            searchForMatches("");
        }

        Timer t = new Timer() {
            @Override
            public void run() {
                _searchText.getElement().focus();
            }
        };
        t.schedule(1000);
    }
    
    static List<String> previousEntries = null;
    static String PRESCRIBED_LESSONS = "<<< Your Lessons >>>";
    static CmList<Topic> prescribedLessons=null;
    
    /** Create and maintain static list of previous
     *  search entries.  Refreshes on LOGIN event.
     *  
     *  If user is logged in special token is added
     *  to signify that the user's assigned lessons 
     *  should be assigned.
     *  
     *  
     * @return
     */
    static private List<String> getPreviousEntries() {
        if(previousEntries == null) {
            previousEntries = new ArrayList<String>();
            
            if(Storage.isSupported()) {
               String entries = Storage.getLocalStorage().getItem("prev-entries");
               if(entries != null) {
                   String l[] = entries.split("\n");
                   for(int i=0,t=l.length;i<t;i++) {
                       previousEntries.add(l[i]);
                   }
               }
            }
            if(CatchupMathMobileShared.getUser() != null) {
                previousEntries.add(PRESCRIBED_LESSONS);
            }            
        }
        return previousEntries;
    }

    /** Add to previous list at bottom.  If already exists
     *  then value is removed first and added to make sure it
     *  shows up as last used.
     * @param entry
     */
    static private void addToPreviousEntries(String entry) {
        List<String> pe = getPreviousEntries();
        if(pe.contains(entry)) {
            pe.remove(entry);
        }
        pe.add(entry);
        
        if(Storage.isSupported()) {
            String data="";
            for(int i=0,t=previousEntries.size();i<t;i++) {
                if(data.length() > 0)
                    data += "\n";
                data += previousEntries.get(i);
            } 
            Storage.getLocalStorage().setItem("prev-entries", data);
        }
    }
    
    private void searchForMatches(String searchFor) {
        CmList<Topic> matches = new CmArrayList<Topic>();
        if(searchFor == null || searchFor.length() == 0) {
            /** match all */
            matches = topics;
        }
        else {
            
            if(searchFor.equals(PRESCRIBED_LESSONS)) {
                if(prescribedLessons == null) {
                    Window.alert("No prescribed lessons have been assigned");
                }
                else {
                    showForMatches(prescribedLessons);
                }
                return;
            }
            
            
            for(int i=0,t=topics.size();i<t;i++) {
                Topic t1 = topics.get(i);
                if(t1.getName().toLowerCase().indexOf(searchFor.toLowerCase()) > -1) {
                    matches.add(t1);
                }
            }
        }
        showForMatches(matches);
    }
    
    /** Update the list of matching lessons
     * 
     * @param topicMatch
     */
    private void showForMatches(CmList<Topic> topicMatch) {
        
       GenericContainerTag ul = new GenericContainerTag("ul");
       ul.addStyleName("touch");
        for(int i=0,t=topicMatch.size();i<t;i++) {
            final Topic topic = topicMatch.get(i);
            
            GenericTextTag<String> li = new GenericTextTag<String>("li",topic.getName());
            
            li.addHandler(new TouchClickEvent.TouchClickHandler<String>() {
                @Override
                public void touchClick(TouchClickEvent<String> e) {
                    if (!ViewSettings.AnimationRunning) {
                        
                        /** add the current search term to previous entries
                         * 
                         */
                        if(!_searchText.getText().equals(PRESCRIBED_LESSONS))
                            addToPreviousEntries(_searchText.getText());
                        
                        String tag = "topic:" + topic.getFile() + ":" + System.currentTimeMillis();
                        History.newItem(tag);
                    }
                }
            });
            li.setStyleName("group");
            ul.add(li);
        }
        matches.clear();
        matches.add(ul);        
    }
    
    private void loadTopicList() {

        /** Has already been drawn ?
         * 
         */
        if(topics != null) {
            drawGui(topics);
            return;
        }
        
        /** see if list of lessons is in local storage
         * 
         */
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
            String valueList = localStorage.getItem("value_list");
            if(valueList != null) {
                topics = convertValueListIntoCmList(valueList);
                drawGui(topics);
                return;
            }
        }
        
        /** has not been read or processed yet must contact server
         * 
         */
        
        GetMobileTopicListAction topicAction = new GetMobileTopicListAction();
        
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_START));
        CatchupMathMobileShared.getCmService().execute(topicAction, new AsyncCallback<CmList<Topic>>() {
            @Override
            public void onSuccess(CmList<Topic> topics) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                TopicListPagePanel.topics = topics;
                

                /** convert to text and store in
                 *  local storage if available.
                 */
                if(Storage.isSupported()) {
                    String valueList=convertCmListToString(topics);
                    Storage localStorage = Storage.getLocalStorage();
                    localStorage.setItem("value_list",valueList);
                }
                
                
                drawGui(topics);
            }
            @Override
            public void onFailure(Throwable arg0) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                arg0.printStackTrace();
                Window.alert(arg0.getMessage());
            }
        });
    }
    
    
    private CmList<Topic> convertValueListIntoCmList(String valueList) {
        String lines[] = valueList.split("\n");
        CmList<Topic> topics = new CmArrayList<Topic>();
        for(int i=0,t=lines.length;i<t;i++) {
            String p[] = lines[i].split("\\|");
            Topic topic = new Topic(p[0],p[1]);
            topics.add(topic);
        }
        return topics;
    }
    
    private String convertCmListToString(CmList<Topic> topics) {
        String str="";
        for(int i=0,t=topics.size();i<t;i++) {
            final Topic topic = topics.get(i);
            str += topic.getName() + "|" + topic.getFile() + "\n";
        }
        return str;
    }
}
