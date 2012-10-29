package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;

import java.util.List;

public class GradeBookUtils {
    
    /** Calculate the grade from a list of student problems.
     * 
     * @param problems
     * @return
     */
    static public String getHomeworkGrade(List<StudentProblemDto> problems) {
        int percent = 0;
        int numCorrect = 0;

        for (StudentProblemDto dto : problems) {
            numCorrect += (dto.getStatus().equalsIgnoreCase(ProblemStatus.CORRECT.toString())) ? 1 : 0;
        }
        if (problems.size() > 0)
            percent = Math.round((float)numCorrect * 100.0f / (float)problems.size());

        return percent + "%";
    }

}
