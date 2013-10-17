package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class LessonModel implements Response {
    
    private String lessonName;
    private String lessonFile;
    private String subject;
    
    public LessonModel() {}

    public LessonModel(String lessonName, String lessonFile) {
        this(lessonName, lessonFile, null);
    }
    
    public LessonModel(String lessonName, String lessonFile, String subject) {
        this.lessonName = lessonName;
        this.lessonFile = lessonFile;
        this.subject = subject;
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
    

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof LessonModel) {
            LessonModel l = (LessonModel)obj;
            if(l.getSubject().equals(getSubject()) && (l.getLessonFile() == getLessonFile() || l.getLessonFile().equals(getLessonFile())) && (l.getLessonName() == getLessonName() || l.getLessonName().equals(getLessonName()))) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return super.equals(obj);
        }
    }
    @Override
    public String toString() {
        return "LessonModel [lessonName=" + lessonName + ", lessonFile=" + lessonFile + "]";
    }
    
}
