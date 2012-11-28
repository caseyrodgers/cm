package hotmath.gwt.cm.client.ui.context;


public class LessonChoice {
    private int id;
    private String topic;
    private String status;
    private boolean isComplete;
    private String style;

    public LessonChoice(int id, String topic, boolean isComplete, String status) {
        this.id = id;
        this.topic = topic;
        this.isComplete = isComplete;
        this.status = status;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
