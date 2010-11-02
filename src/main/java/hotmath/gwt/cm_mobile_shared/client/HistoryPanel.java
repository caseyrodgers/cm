package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.CmEventListener;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;

import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class HistoryPanel extends FlowPanel {
    static private CmList<Topic> __historyLessons = null;

    static public CmList<Topic> getHistoryLessons() {
        if (__historyLessons == null) {
            __historyLessons = readHistoryLessons();
        }
        return __historyLessons;
    }

    static private CmList<Topic> readHistoryLessons() {
        __historyLessons = new CmArrayList<Topic>();

        if (Storage.isSupported()) {
            String history = Storage.getLocalStorage().getItem("history");
            if (history != null && history.length() > 0) {
                for (String t : history.split("\\n")) {
                    String p[] = t.split("\\|");
                    
                    boolean found=false;
                    for(int a=0,at=__historyLessons.size();a<at;a++) {
                        if(__historyLessons.get(a).getFile().equals(p[1])) {
                            found=true;
                            break;
                        }
                    }
                    if(!found)
                        __historyLessons.add(new Topic(p[0], p[1]));
                }
            }
        }
        
        return __historyLessons; 
    }

    static void addToHistoryLessons(String title, String file) {
        CmList<Topic> lessons = getHistoryLessons();
        
        /** make sure does not already exist 
         * 
         */
        for(int a=0,at=lessons.size();a<at;a++) {
            if(lessons.get(a).getFile().equals(file))
                return;
        }
        
        
        lessons.add(new Topic(title, file));

        if (Storage.isSupported()) {
            Storage storeage = Storage.getLocalStorage();
            String persist = "";
            for (int i = 0, t = lessons.size(); i < t; i++) {
                if (persist.length() > 0)
                    persist += "\n";

                Topic les = lessons.get(i);
                persist += les.getName() + "|" + les.getFile();
            }
            storeage.setItem("history", persist);
        }
    }

    boolean isInitialized = false;

    public HistoryPanel() {
        add(new Label("Loading history information ..."));
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    /**
     * Prepare panel for use
     * 
     */
    public void initialize() {
        clear();

        CmList<Topic> history = getHistoryLessons();
        createUi(history);
    }

    private void createUi(CmList<Topic> lessons) {
        
        clear();
        String html = "<h2 style='color: #738AA6'>Previously Visited Lessons</h2>";
        add(new HTML(html));
        
        GenericContainerTag ul = new GenericContainerTag("ul");
        ul.addStyleName("touch");
        for (int i = 0, t = lessons.size(); i < t; i++) {
            final Topic topic = lessons.get(i);

            GenericTextTag<String> li = new GenericTextTag<String>("li", topic.getName());

            li.addHandler(new TouchClickEvent.TouchClickHandler<String>() {
                @Override
                public void touchClick(TouchClickEvent<String> e) {
                    History.newItem(new TokenParser("lesson", topic.getFile(),0).getHistoryTag());
                }
            });
            li.setStyleName("group");
            ul.add(li);
        }
        
        add(ul);
        
        isInitialized=true;
    }
    
    static {
        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType().equals(EventTypes.EVENT_LESSON_LOADED)) {
                    PrescriptionData pd = (PrescriptionData)event.getEventData();
                    addToHistoryLessons(pd.getCurrSession().getTopic(), pd.getCurrSession().getInmhResources().get(0).getItems().get(0).getFile());
                }
            }
        });
    }
}
