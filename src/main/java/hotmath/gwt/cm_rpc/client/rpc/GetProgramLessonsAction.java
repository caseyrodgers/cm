package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

/** Provide ability to get list of lessons for a given test/quiz
 * 
 * @author casey
 *
 */
public class GetProgramLessonsAction implements Action<CmList<ProgramLesson>>{
    
    int testDefId;
    int segment;
    String chapter;
    int sectionCount;
    
    public GetProgramLessonsAction() {}
    public GetProgramLessonsAction(int testDefId, int segment, String chapter, int sectionCount) {
        this.testDefId = testDefId;
        this.segment = segment;
        this.chapter = chapter;
        this.sectionCount = sectionCount;
    }
    public int getTestDefId() {
        return testDefId;
    }
    
    public String getChapter() {
    	return chapter;
    }
    
    public int getSectionCount() {
    	return sectionCount;
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
