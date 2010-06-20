package hotmath.gwt.cm_rpc.client.rpc;

public class LessonResult implements Response {
    
    String lesson;
    String warning;
    boolean hasSpanish;
    
    public LessonResult() {}
    public LessonResult(String lesson) {
        this.lesson = lesson;
    }
    public String getLesson() {
        return lesson;
    }
    public void setLesson(String lesson) {
        this.lesson = lesson;
    }
    public String getWarning() {
        return warning;
    }
    public void setWarning(String warning) {
        this.warning = warning;
    }
    public boolean isHasSpanish() {
        return hasSpanish;
    }
    public void setHasSpanish(boolean hasSpanish) {
        this.hasSpanish = hasSpanish;
    }
}
