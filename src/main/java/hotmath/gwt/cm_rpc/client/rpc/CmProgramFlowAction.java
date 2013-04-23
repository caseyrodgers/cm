package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** Provides an abstraction over a single CM Program Item.
 * 
 * Represents the current location within a CM program
 * 
 * contains a composite object containing the data for
 * both a QUIZ OR a PRESCRIPTION item.
 * 
 * @author casey
 *
 */
public class CmProgramFlowAction implements Response {
    
    QuizHtmlResult quizResult;
    PrescriptionSessionResponse prescriptionResponse;
    
    CmPlace place;
    
    String assignedTest;
    Integer assignedTestId;

    public CmProgramFlowAction() {
    }

    public CmProgramFlowAction(Integer testId) {
        this.assignedTestId = testId;
    }

    public CmProgramFlowAction(CmPlace action) {
        place = action;
    }

    public CmProgramFlowAction(QuizHtmlResult quizResult) {
        this.quizResult = quizResult;
        this.place = CmPlace.QUIZ;
    }
    
    public CmProgramFlowAction(PrescriptionSessionResponse prescriptionResponse) {
        this.prescriptionResponse = prescriptionResponse;
        this.place = CmPlace.PRESCRIPTION;
    }
    
    
    public String getAssignedTest() {
        return assignedTest;
    }

    public void setAssignedTest(String assignedTest) {
        this.assignedTest = assignedTest;
    }
    
    public CmPlace getPlace() {
        return place;
    }

    public Integer getAssignedTestId() {
        return assignedTestId;
    }


    public void setAssignedTestId(Integer assignedTestId) {
        this.assignedTestId = assignedTestId;
    }

    public void setNextAction(CmPlace nextAction) {
        this.place = nextAction;
    }

    public QuizHtmlResult getQuizResult() {
        return quizResult;
    }

    public PrescriptionSessionResponse getPrescriptionResponse() {
        if(prescriptionResponse == null) {
            prescriptionResponse = new PrescriptionSessionResponse();
        }
            
        return prescriptionResponse;
    }
    
    public void setPrescriptionResponse(PrescriptionSessionResponse prescriptionResponse) {
        this.prescriptionResponse = prescriptionResponse;
    }

    @Override
    public String toString() {
        return "CmProgramFlowAction [quizResult=" + quizResult + ", prescriptionResponse=" + prescriptionResponse + ", place="
                + place + ", assignedTest=" + assignedTest + ", assignedTestId=" + assignedTestId + "]";
    }
}
