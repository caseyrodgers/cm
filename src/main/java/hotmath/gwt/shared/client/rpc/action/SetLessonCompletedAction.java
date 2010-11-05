package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

public class SetLessonCompletedAction implements Action<RpcData>{
    
    String lesson;
    Integer runId;
    Integer session;
    

    public SetLessonCompletedAction() {}
    
    public SetLessonCompletedAction(String lesson, Integer runId, Integer session) {
        this.lesson = lesson;
        this.runId = runId;
        this.session = session;
        
    }
    
    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public Integer getRunId() {
        return runId;
    }

    public void setRunId(Integer runId) {
        this.runId = runId;
    }

    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "SetLessonCompletedAction [Session=" + session + ", runId=" + runId + ", lesson=" + lesson + "]";
    }
}
