package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;


/** An EXT model for representing a single item
 * in a custom program.
 *
 * Represents a single lesson in a custom program.
 *
 *
 * @author casey
 *
 */
public class CustomLessonModel extends BaseModel implements Response {

    static public enum Type{LESSON,QUIZ};

    Type customProgramType;


    public CustomLessonModel(){
        /** empty */
    }

    public CustomLessonModel(String lesson, String file, String subject) {
        this.customProgramType = Type.LESSON;

        setLesson(lesson);

        if(subject == null || subject.length() == 0)
            subject = "Alg 1";

        setFile(file);
        setSubject(subject);
    }

    public CustomLessonModel(int quizId, String quizLabel) {
        this.customProgramType = Type.QUIZ;
        setQuiz(quizLabel);
        setQuizId(quizId);

        set("subjectStyleClass", "is_quiz_subject");
        set("customProgramItemClass", "is_quiz");
    }

    public void setQuizId(Integer quizId) {
        set("quizId", quizId);
    }

    public Integer getQuizId() {
        return get("quizId");
    }

    public void setQuiz(String name) {
        setCustomProgramItem(name);
    }

    public String getCustomProgramItem() {
        return get("customProgramItem");
    }

    public void setCustomProgramItem(String item) {
        set("customProgramItem", item);
    }

    public String getQuiz() {
        return getCustomProgramItem();
    }

    public void setLesson(String lesson) {
        setCustomProgramItem(lesson);
    }

    public String getLesson() {
        return getCustomProgramItem();
    }

    public void setSubject(String subject) {
        set("subject", subject);

        /** used to set a style for this lesson when
         *  displayed via the CustomPrescriptionDialog list
         */
        set("subjectStyleClass", subject.toLowerCase().replace(" " ,""));
    }

    public void setFile(String file) {
        set("file", file);
    }

    public String getFile() {
        return get("file");
    }

    public Type getCustomProgramType() {
        return customProgramType;
    }

    public void setCustomProgramType(Type customProgramType) {
        this.customProgramType = customProgramType;
    }


    public String getSubject() {
        return get("subject");
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CustomLessonModel) {
            CustomLessonModel clm = (CustomLessonModel)obj;

            if(clm.getCustomProgramType() == Type.LESSON && clm.getFile().equals(getFile())) {
                return true;
            }
            else if(clm.getCustomProgramType() == Type.QUIZ && (clm.getQuiz() != null && clm.getQuiz().equals(getQuiz()))) {
                return true;
            }
            else
                return false;
        }
        else {
            return super.equals(obj);
        }
    }
}
