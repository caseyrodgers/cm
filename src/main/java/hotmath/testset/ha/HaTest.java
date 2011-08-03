package hotmath.testset.ha;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/** HaTest Class 
 *  
 *  Describes a single CM Test (quiz/segment)
 *  
 *  
 * @author Casey
 *
 */
public class HaTest {

    static Logger __logger = Logger.getLogger(HaTest.class);
    
	final static int QUESTIONS_PER_TEST  = 10;
	final static int SEGMENTS_PER_PROGRAM = 4;
	
	HaUser user;
	Integer testId;
	List<String> pids = new ArrayList<String>();
	HaTestDef testDef;
	int segment;
	int segmentSlot;
	int totalSegments;
	int numTestQuestions;
	Timestamp createTime;
	int gradeLevel;

	List<HaTestRun> testRuns;
	

    public List<HaTestRun> getTestRuns() {
        return testRuns;
    }

    public void setTestRuns(List<HaTestRun> testRuns) {
        this.testRuns = testRuns;
    }

    StudentUserProgramModel programInfo;

	int TEST_SIZE=10;

	public int getTotalSegments() {
		return totalSegments;
	}

	public void setTotalSegments(int totalSegments) {
		this.totalSegments = totalSegments;
	}

	public int getNumTestQuestions() {
		return numTestQuestions;  
	}

	public void setNumTestQuestions(int numTestQuestions) {
		this.numTestQuestions = numTestQuestions;
	}

	public Integer getTestId() {
		return testId;
	}

	public void setTestId(Integer testId) {
		this.testId = testId;
	}
	
	public HaUser getUser() {
		return user;
	}
	public void setUser(HaUser user) {
		this.user = user;
	}
	
	public void addPid(String pid) {
		this.pids.add(pid);
	}
	
	public List<String> getPids() {
		return this.pids;
	}

	public void setTestDef(HaTestDef testDef) {
	    this.testDef = testDef;
	}
	
	public HaTestDef getTestDef() {
		return this.testDef;
	}
	
	public String getTitle() {
		return testDef.getTitle();
	}
	
	public String getSubTitle(int segment) {
		return testDef.getSubTitle(segment);		
	}
	
	/** Return the current segment the user is working in
	 * 
	 * @param segment
	 */
	public void setSegment(int segment) {
		this.segment = segment;
	}
	
	public int getSegment() {
		return this.segment;
	}
	
	public void setSegmentSlot(int slot) {
	    this.segmentSlot = slot;
	}
	
	public int getSegmentSlot() {
	    return this.segmentSlot;
	}

    /** 
     * return the number of questions in this test
     * @return
     */
	public int getTestQuestionCount() {
		return QUESTIONS_PER_TEST;
	}
	
	public int getSegmentCount() {
		return getTestDef().getTotalSegmentCount();
	}
	
	
	public StudentUserProgramModel getProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(StudentUserProgramModel programInfo) {
        this.programInfo = programInfo;
    }
    
    public Timestamp getCreateTime() {
    	return createTime;
    }
    
    public void setCreateTime(Timestamp timestamp){
    	this.createTime = timestamp;
    }

	/** Return the test run (prescription) that is currently active
	 *  for this user.
	 *  
	 * @return
	 */
	public int getCurrentTestRunId() {
		return getUser().getActiveTestRunId();
	}	
	
	@Override
	public String toString() {
		return "HaTest [user=" + user + ", testId=" + testId + ", pids=" + pids
				+ ", testDef=" + testDef + ", segment=" + segment
				+ ", segmentSlot=" + segmentSlot + ", totalSegments="
				+ totalSegments + ", numTestQuestions=" + numTestQuestions
				+ ", createTime=" + createTime + ", gradeLevel=" + gradeLevel
				+ ", testRuns=" + testRuns + ", programInfo=" + programInfo
				+ ", TEST_SIZE=" + TEST_SIZE + "]";
	}
	
    public int getGradeLevel() {
		return gradeLevel;
	}

	public void setGradeLevel(int gradeLevel) {
		this.gradeLevel = gradeLevel;
	}
	
}
