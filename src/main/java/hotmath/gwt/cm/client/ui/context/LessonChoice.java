package hotmath.gwt.cm.client.ui.context;

import com.extjs.gxt.ui.client.data.BaseModel;

public class LessonChoice extends BaseModel {
    public LessonChoice(String topic, boolean isComplete, String status) {
        set("topic", topic);
        set("isComplete", isComplete);
        set("status", status);
        set("style", "");
    }
    
    public void setStyle(String style) {
        set("style", style);
    }
    
    public String getStyle() {
        return get("style");
    }
    
    public String getTopic() {
        return get("topic");
    }
    
    public boolean isComplete() {
        return get("isComplete");
    }
    
    @Override
    public String toString() {
        return "topic = " + get("topic");
    }
}
