package hotmath.gwt.cm_tools.client.model;

import java.io.Serializable;

/** Represents a CM Custom Program Object
 * 
 *  A composite of a Custom Program.
 *  
 *  A custom program can be either:
 *  
 *  1. a CustomProgram containing list of lessons
 *  2. a CustomQuiz which is a custom set of questions
 *  
 *  Cannot be both.
 *  
 * @author casey
 *
 */
public class CustomProgramComposite implements Serializable {
    int customProgramId;
    String customProgramName;
    int customQuizId;
    String customQuizName;
    Type type;
    
    public CustomProgramComposite(){}
    
    public CustomProgramComposite(int custProgId, String custProgName, int custQuizId, String custQuizName) {
        assert(customProgramId == 0 || customQuizId == 0);
        
        if(custProgId > 0) {
            customProgramId = custProgId;
            customProgramName = custProgName;
            type = Type.LESSONS;
        }
        else if(custQuizId > 0) {
            customQuizId = custQuizId;
            customQuizName = custQuizName;
            type = Type.QUIZ;
        }
    }
    
    /** Return the correct name of this custom object.
     * 
     * If a quiz, the quiz name, if a lessons then the custom program name
     * If is not custom then return empty string
     * 
     * @return
     */
    public String getCustomName() {
        if(isCustom()) {
            switch(type) {
                case LESSONS:
                    return getCustomProgramName();
                    
                case QUIZ:
                    return getCustomQuizName();
            }
        }
        return "";
    }
    /** Return true if an active custom program
     *  consisting of a set of lessons
     *  
     * @return
     */
    public boolean isCustomLessons() {
        return getCustomProgramId() > 0;
    }

    /** Set this object to LESSONS only if id set
     * 
     * @param progId
     * @param name
     */
    public void setCustomProgram(int progId, String name) {
        if(progId > 0) {
            type = Type.LESSONS;
            this.customProgramName = name;
            this.customProgramId = progId;
            
            this.customQuizId = 0;
            this.customQuizName = null;
        }
    }
    
    /** Set this object as a QUIZ only if quizId is > 0
     * 
     * @param progId
     * @param name
     */
    public void setCustomQuiz(int quizId, String name) {
        if(quizId > 0) {
            type = Type.QUIZ;
            this.customQuizId = quizId;
            this.customQuizName = name;
            
            this.customProgramId = 0;
            this.customProgramName = null;
        }
    }
    
    /** Is any type of custom, either
     *  a QUIZ or a LESSONS
     *  
     * @return
     */
    public boolean isCustom() {
        return type != null;
    }
    
    public int getCustomProgramId() {
        return customProgramId;
    }

    public void setCustomProgramId(int customProgramId) {
        this.customProgramId = customProgramId;
    }

    public String getCustomProgramName() {
        return customProgramName;
    }

    public void setCustomProgramName(String customProgramName) {
        this.customProgramName = customProgramName;
    }

    public int getCustomQuizId() {
        return customQuizId;
    }

    public void setCustomQuizId(int customQuizId) {
        this.customQuizId = customQuizId;
    }

    public String getCustomQuizName() {
        return customQuizName;
    }

    public void setCustomQuizName(String customQuizName) {
        this.customQuizName = customQuizName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {QUIZ, LESSONS}

    @Override
    public String toString() {
        return "CustomProgramComposite [customProgramId=" + customProgramId + ", customProgramName=" + customProgramName
                + ", customQuizId=" + customQuizId + ", customQuizName=" + customQuizName + ", type=" + type + "]";
    };
}
