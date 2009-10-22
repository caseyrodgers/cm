package hotmath.testset.ha.info;

import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.StudentUserProgramModel;

import java.util.List;

/** A single Program Information object that 
 * Identifies a user program assignment.
 * 
 * Head of nested set of objects that identifies all activity
 * for a given user.
 * 
 * @author casey
 *
 */
public class UserProgramInfo {

    StudentUserProgramModel program;
    List<HaTest> tests;

    public UserProgramInfo() {}
   
    public StudentUserProgramModel getProgram() {
        return program;
    }

    public void setProgram(StudentUserProgramModel program) {
        this.program = program;
    }

    public List<HaTest> getTests() {
        return tests;
    }

    public void setTests(List<HaTest> tests) {
        this.tests = tests;
    }
}
