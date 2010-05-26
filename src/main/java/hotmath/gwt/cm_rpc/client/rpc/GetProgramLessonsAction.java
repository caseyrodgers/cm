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
    String chapter;
    
    public GetProgramLessonsAction() {}
    public GetProgramLessonsAction(int testDefId, int segment, String chapter) {
        this.testDefId = testDefId;
        this.segment = segment;
        this.chapter = chapter;
    }
    public int getTestDefId() {
        return testDefId;
    }
    
    public String getChapter() {
    	return chapter;
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
