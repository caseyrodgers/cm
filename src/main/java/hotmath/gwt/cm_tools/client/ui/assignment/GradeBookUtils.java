package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;

import java.util.List;

public class GradeBookUtils {
    
    /** Calculate the grade from a list of student problems.
     * 
     * @param problems
     * @return
     */
    static public String getHomeworkGrade(List<StudentProblemDto> problems) {
        int totCount=0, totCorrect=0, totIncorrect=0, totHalfCredit=0;
        for (StudentProblemDto dto : problems) {
            totCount++;
            String status = dto.getStatus();
            if(status.equals(ProblemStatus.CORRECT.toString())) {
                totCorrect++;
            }
            else if(status.equals(ProblemStatus.HALF_CREDIT.toString())) {
                totHalfCredit++;
            }
            else if(status.equals(ProblemStatus.INCORRECT.toString())) {
                totIncorrect++;
            }
        }
        return getHomeworkGrade(totCount, totCorrect, totIncorrect, totHalfCredit);
    }
    
    

    static public String getHomeworkGrade(int totCount, int totCorrect, int totIncorrect, int totHalfCredit) {
        // add .5 for each half correct answer
        float totCorrectF = (float) totCorrect + (totHalfCredit > 0 ? (float)totHalfCredit / 2 : 0);
        String grade = "-";
        if ((totCorrect + totIncorrect + totHalfCredit) > 0) {
            int percent = Math.round(((float) totCorrectF / (float) totCount) * 100.0f);
            grade = percent+"%";
        }
        return grade;
    }

}
