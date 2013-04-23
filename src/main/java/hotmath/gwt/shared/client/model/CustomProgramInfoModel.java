package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;

import java.util.ArrayList;
import java.util.List;

public class CustomProgramInfoModel implements Response {
    CustomProgramModel program;
    List<StudentModelExt> assignedStudents = new ArrayList<StudentModelExt>();
    List<CustomLessonModel> lessons = new ArrayList<CustomLessonModel>();
    
    public CustomProgramModel getProgram() {
        return program;
    }
    public void setProgram(CustomProgramModel program) {
        this.program = program;
    }
    public List<StudentModelExt> getAssignedStudents() {
        return assignedStudents;
    }
    public void setAssignedStudents(List<StudentModelExt> assignedStudents) {
        this.assignedStudents = assignedStudents;
    }
    public List<CustomLessonModel> getLessons() {
        return lessons;
    }
    public void setLessons(List<CustomLessonModel> lessons) {
        this.lessons = lessons;
    }
}
