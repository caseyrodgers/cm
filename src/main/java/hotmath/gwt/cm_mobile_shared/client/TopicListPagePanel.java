package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetMobileTopicListAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
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

    FlowPanel matches = new FlowPanel();
    TextBox _searchText;
    
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
        searchBox.add(new Label("Search for lessons: "));
        searchBox.add(_searchText);
        searchBox.add(new Button("History",new ClickHandler() {
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
        
        
        top.add(new TextBox());
        top.add(new TextBox());
        top.add(new TextBox());
        
        topicPanel.clear();
        topicPanel.add(top);
        
        topicPanel.add(matches);
        
        
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
    
    List<String> previousEntries = null;
    private List<String> getPreviousEntries() {
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
        }
        return previousEntries;
    }

    /** Add to previous list at bottom.  If already exists
     *  then value is removed first and added to make sure it
     *  shows up as last used.
     * @param entry
     */
    private void addToPreviousEntries(String entry) {
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
            matches = this.topics;
        }
        else {
            for(int i=0,t=this.topics.size();i<t;i++) {
                Topic t1 = this.topics.get(i);
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
        if(this.topics != null) {
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
                TopicListPagePanel.this.topics = convertValueListIntoCmList(valueList);
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
    
    private Panel createSearchPanel() {
        final GenericContainerTag ulTag = new GenericContainerTag("div");
        ulTag.setStyleName("search-bar");
        String letters = "abcdefghijklmnlop";
        for(int i=0,t=letters.length();i<t;i++) {
            GenericTextTag<String> li = new GenericTextTag<String>("span",letters.substring(i,i+1));
            li.addHandler(new TouchClickHandler<String>() {
                @Override
                public void touchClick(TouchClickEvent<String> event) {
                    
                    /** turn off current selection
                     * 
                     */
                    for(int w=0,wt=ulTag.getWidgetCount();w<wt;w++) {
                        Widget widget = ulTag.getWidget(w);
                        widget.getElement().removeClassName("selected");
                    }
                    event.getTarget().getElement().addClassName("selected");
                    String txt = event.getTarget().getText();
                    searchForMatches(txt);
                }
            });
            ulTag.add(li);
        }
        return ulTag;
    }
}
