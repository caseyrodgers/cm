package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

/** An EXT model for representing a single Lesson
 * 
 * @author casey
 *
 */
public class CustomLessonModel extends BaseModel implements Response {
    public CustomLessonModel(){
        /** empty */
    }
    
    public CustomLessonModel(String lesson, String file, String subject) {
        
        if(subject == null || subject.length() == 0)
            subject = "Alg 1";
        
        setLesson(lesson);
        setFile(file);
        setSubject(subject);
    }

    public String getLesson() {
        return get("lesson");
    }

    public void setLesson(String lesson) {
        set("lesson", lesson);
    }

    public String getSubject() {
        return get("subject");
    }

    public void setSubject(String subject) {
        set("subject", subject);

        /** used to set a style for this lesson when 
         *  displayed vias the CustomPrescriptionDialog list
         */
        set("subjectStyleClass", subject.toLowerCase().replace(" " ,""));
    }
    
    public void setFile(String file) {
        set("file", file);
    }
    
    public String getFile() {
        return get("file");
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CustomLessonModel) {
            CustomLessonModel clm = (CustomLessonModel)obj;
            if(clm.getFile().equals(getFile()))
                return true;
            else
                return false;
        }
        else
            return super.equals(obj);
    }
    
    @Override
    public String toString() {
        return "CustomLessonModel [lesson=" + getLesson() + ", subject=" + getSubject() + "]";
    }
}
