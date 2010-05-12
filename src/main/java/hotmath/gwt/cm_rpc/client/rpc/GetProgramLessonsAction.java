package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;

/** Provide ability to get list of lessons for a given test/quiz
 * 
 * @author casey
 *
 */
public class GetProgramLessonsAction implements Action<CmList<ProgramLesson>>{
    
    int testDefId;
    int segment;
    
    public GetProgramLessonsAction() {}
    public GetProgramLessonsAction(int testDefId, int segment) {
        this.testDefId = testDefId;
        this.segment = segment;
    }
    public int getTestDefId() {
        return testDefId;
    }
    public void setTestDefId(int testDefId) {
        this.testDefId = testDefId;
    }
    public int getSegment() {
        return segment;
    }
    public void setSegment(int segment) {
        this.segment = segment;
    }
}
