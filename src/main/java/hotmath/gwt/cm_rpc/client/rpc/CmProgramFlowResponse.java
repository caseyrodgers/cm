package hotmath.gwt.cm_rpc.client.rpc;

/** 
 *  A composite object that represents the 'current' 
 *  CM program flow.
 *   
 * @author casey
 *
 */
public class CmProgramFlowResponse implements Response {
    
    QuizHtmlResult quizResult;
    PrescriptionSessionResponse prescriptionResponse;
    CmPlace place;
    
    public CmProgramFlowResponse(QuizHtmlResult quizResult) {
        this.quizResult = quizResult;
        this.place = CmPlace.QUIZ;
    }
    
    public CmProgramFlowResponse(PrescriptionSessionResponse prescriptionResponse) {
        this.prescriptionResponse = prescriptionResponse;
        this.place = CmPlace.PRESCRIPTION;
    }
    
    public CmProgramFlowResponse(CmPlace place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return "CmProgramFlowResponse [quizResult=" + quizResult + ", prescriptionResponse=" + prescriptionResponse
                + ", place=" + place + "]";
    }   
}
    