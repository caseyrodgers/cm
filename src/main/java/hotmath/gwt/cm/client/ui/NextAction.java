package hotmath.gwt.cm.client.ui;

public class NextAction {
    
    static public enum NextActionName {QUIZ,PRESCRIPTION,AUTO_ASSSIGNED};
    
    NextActionName nextAction;
    
    String assignedTest;


    public String getAssignedTest() {
        return assignedTest;
    }


    public void setAssignedTest(String assignedTest) {
        this.assignedTest = assignedTest;
    }


    public NextAction() {
    }
    

    public NextAction(NextActionName action) {
        nextAction = action;
    }

    public NextActionName getNextAction() {
        return nextAction;
    }

    public void setNextAction(NextActionName nextAction) {
        this.nextAction = nextAction;
    }

}
