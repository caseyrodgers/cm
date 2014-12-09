package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class LessonResult implements Response {
    String lesson;
    String warning;
    boolean hasSpanish;
    InmhItemData item;
    
    public LessonResult() {}
    public LessonResult(String lesson, InmhItemData item) {
        this.lesson = lesson;
        this.item = item;
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
    public InmhItemData getItem() {
        return item;
    }
    public void setItem(InmhItemData item) {
        this.item = item;
    }
}
