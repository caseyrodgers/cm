package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.rpc.GetMobileTopicListAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.ViewSettings;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;


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
    
    private void drawGui(CmList<Topic> topics) {
        GenericContainerTag ul = new GenericContainerTag("ul");
        ul.addStyleName("touch");
        for(int i=0,t=topics.size();i<t;i++) {
            final Topic topic = topics.get(i);
            
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
        topicPanel.clear();
        topicPanel.add(ul);
    }
    
    private void loadTopicList() {
        if(this.topics != null) {
            drawGui(topics);
            return;
        }
        GetMobileTopicListAction topicAction = new GetMobileTopicListAction();
        
        CatchupMathMobileShared.getCmService().execute(topicAction, new AsyncCallback<CmList<Topic>>() {
            @Override
            public void onSuccess(CmList<Topic> topics) {
                TopicListPagePanel.this.topics = topics;
                drawGui(topics);
            }
            @Override
            public void onFailure(Throwable arg0) {
                arg0.printStackTrace();
                Window.alert(arg0.getMessage());
            }
        });
    }
    
}