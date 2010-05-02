package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;

import java.util.List;

public class CustomProgramAction implements Action<CmList<CustomLessonModel>>{

    private static final long serialVersionUID = -5883561195322210717L;
    
    ActionType action;
    Integer programId;
    Integer adminId;
    String programName;
    
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    List<CustomLessonModel> lessons;
    
    public CustomProgramAction() {
        /** empty */
    }
    
    public CustomProgramAction(ActionType action) {
        this.action = action;
    }
    
    public ActionType getAction() {
        return action;
    }


    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public List<CustomLessonModel> getLessons() {
        return lessons;
    }

    public void setLessons(List<CustomLessonModel> lessons) {
        this.lessons = lessons;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }
    
    @Override
    public String toString() {
        return "CustomProgramAction [action=" + action + ", adminId=" + adminId + ", lessons=" + lessons
                + ", programId=" + programId + ", programName=" + programName + "]";
    }    

    static public enum ActionType {
        /** return all possible lessons in 
         *  which to build a custom prescription
         */
        GET_ALL_LESSONS,
        
        /** load the named Custom program definition.
         * 
         */
        GET_CUSTOM_PROGRAM,
        
        /** save any changes
         *  (lessons should be set)
         */
        SAVE,
        
        /** create a new Custom Program 
         * 
         */
        CREATE
    }
}
