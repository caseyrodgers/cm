package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class LessonModel implements Response {

    private String lessonName;
    private String lessonFile;
    private String subject;

    public LessonModel() {
    }

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
        if (obj instanceof LessonModel) {
            LessonModel l = (LessonModel) obj;
            
            if(compareMaybeNull(getSubject(), l.getSubject())) {
                if(compareMaybeNull(getLessonName(), l.getLessonName())) {
                    if(compareMaybeNull(getLessonFile(), l.getLessonFile())) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return super.equals(obj);
        }
    }

    private boolean compareMaybeNull(String s1, String s2) {
        if(s1 == s2) {
            return true;
        }
        else if(s1 == null && s2 != null) {
            return false;
        }
        else if(s2 == null && s1 != null) {
            return false;
        }
        else {
            return s1.equals(s2);
        }

    }

    @Override
    public String toString() {
        return "LessonModel [lessonName=" + lessonName + ", lessonFile=" + lessonFile + "]";
    }

}
