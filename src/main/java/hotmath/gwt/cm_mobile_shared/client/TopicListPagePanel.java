package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.rpc.GetMobileTopicListAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.ViewSettings;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

import java.util.ArrayList;

import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;


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
    private void drawGui(CmList<Topic> topics) {
        
        FlowPanel top = new FlowPanel();
        
        GenericContainerTag searchBox = new GenericContainerTag("div");
        final TextBox textBox = new TextBox();
        textBox.addKeyUpHandler(new KeyUpHandler() {
            
            @Override
            public void onKeyUp(KeyUpEvent event) {
                searchForMatches(textBox.getValue());                
            }
        });

        searchBox.add(new Label("Search for lessons: "));
        searchBox.add(textBox);
        top.add(searchBox);
        
        topicPanel.clear();
        topicPanel.add(top);
        
        topicPanel.add(matches);
        
        if(true)
            return;

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
                        String tag = "topic:" + topic.getFile();
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
        
        /** see if in local storage
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
        
        CatchupMathMobileShared.getCmService().execute(topicAction, new AsyncCallback<CmList<Topic>>() {
            @Override
            public void onSuccess(CmList<Topic> topics) {
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