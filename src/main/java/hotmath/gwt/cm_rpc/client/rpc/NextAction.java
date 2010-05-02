package hotmath.gwt.cm_rpc.client.rpc;

/** Provides abstraction over what to do 'next' when
 * the user is moving to the next 'part' of their program.
 * 
 * 
 * @author casey
 *
 */
public class NextAction {
    
    static public enum NextActionName {QUIZ,PRESCRIPTION,AUTO_ASSSIGNED};
    
    NextActionName nextAction;
    
    String assignedTest;
    Integer assignedTestId;

    public NextAction() {
    }

    public NextAction(Integer testId) {
        this.assignedTestId = testId;
    }

    public NextAction(NextActionName action) {
        nextAction = action;
    }

    public String getAssignedTest() {
        return assignedTest;
    }

    public void setAssignedTest(String assignedTest) {
        this.assignedTest = assignedTest;
    }
    
    public NextActionName getNextAction() {
        return nextAction;
    }

    public Integer getAssignedTestId() {
        return assignedTestId;
    }


    public void setAssignedTestId(Integer assignedTestId) {
        this.assignedTestId = assignedTestId;
    }


    public void setNextAction(NextActionName nextAction) {
        this.nextAction = nextAction;
    }
}
