package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class LessonModel implements Response {
    
    private String lessonName;
    private String lessonFile;
    
    public LessonModel() {}

    public LessonModel(String lessonName, String lessonFile) {
        this.lessonName = lessonName;
        this.lessonFile = lessonFile;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getLessonFile() {
        return lessonFile;
    }

    public void setLessonFile(String lessonFile) {
        this.lessonFile = lessonFile;
    }
    

    @Override
    public String toString() {
        return "LessonModel [lessonName=" + lessonName + ", lessonFile=" + lessonFile + "]";
    }
    
}
