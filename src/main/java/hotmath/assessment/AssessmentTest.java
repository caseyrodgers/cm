package hotmath.assessment;

import hotmath.testset.TestSet;
import hotmath.testset.TestSetQuestion;

import java.util.ArrayList;
import java.util.List;

/** Represents the current active Test
 *  being used for assessment.
 *  
 *  
 * @author Casey
 *
 */
public class AssessmentTest {

	String name;
	String textCode;
    int startProblemNumber;
    int endProblemNumber;
    
    List<TestSetQuestion> questions = new ArrayList<TestSetQuestion>();
	
	
	public AssessmentTest(String name, String textCode, String chapter, int startProblemNumber, int endProblemNumber) throws Exception {
	    this.name = name;
	    this.textCode = textCode;
	    this.startProblemNumber = startProblemNumber;
	    this.endProblemNumber = endProblemNumber;
	    
	    
	    TestSet testSet = new TestSet(textCode,chapter,startProblemNumber,endProblemNumber);
	    questions = testSet.getQuestions();
	}

	public String getTextCode() {
		return textCode;
	}

	public void setTextCode(String textCode) {
		this.textCode = textCode;
	}
	
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStartProblemNumber() {
		return startProblemNumber;
	}

	public void setStartProblemNumber(int startProblemNumber) {
		this.startProblemNumber = startProblemNumber;
	}

	public int getEndProblemNumber() {
		return endProblemNumber;
	}

	public void setEndProblemNumber(int endProblemNumber) {
		this.endProblemNumber = endProblemNumber;
	}

	public List<TestSetQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<TestSetQuestion> questions) {
		this.questions = questions;
	}
}
