package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.CmEventListener;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class LessonsPrescribedPanel extends FlowPanel {

    boolean initialized=false;
    static LessonsPrescribedPanel __instance;
    
    public LessonsPrescribedPanel() {
        __instance=this;
    }

    public boolean isInitialized() {
        return initialized;
    }
    
    
    public void initialize() {
        if(__prescribedLessons == null) {
            clear();
            add(new LoginForm(new LoginPage()));
        }
        else {
            createUi(__prescribedLessons);
        }
    }

    
    private void createUi(CmList<Topic> lessons) {

        clear();
        
        String html = "<h2 style='color: #738AA6'>Assigned Lessons</h2>" +
                      "<p>Any lessons assigned through Catchup Math will be listed here.</p>";
        
        add(new HTML(html));
        
        GenericContainerTag ul = new GenericContainerTag("ul");
        ul.addStyleName("touch");
        for (int i = 0, t = lessons.size(); i < t; i++) {
            final Topic topic = lessons.get(i);

            GenericTextTag<String> li = new GenericTextTag<String>("li", topic.getName());

            li.addHandler(new TouchClickEvent.TouchClickHandler<String>() {
                @Override
                public void touchClick(TouchClickEvent<String> e) {
                    String tag = "topic:" + topic.getFile() + ":" + System.currentTimeMillis();
                    History.newItem(tag);
                }
            });
            li.setStyleName("group");
            ul.add(li);
        }
        add(ul);
        
        initialized=true;
    }
    
    static CmList<Topic> __prescribedLessons;
    static {
        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType().equals(EventTypes.EVENT_USER_LOGIN)) {
                    __prescribedLessons = CatchupMathMobileShared.getUser().getPrescribedLessons();
                    __instance.createUi(__prescribedLessons);
                }
            }
        });
    }
}
