package hotmath.gwt.cm_rpc.client.rpc;



public class PrescriptionSessionResponse implements Response {
    
    PrescriptionData prescriptionData;
    int correctPercent;
    String programTitle;
    
    public PrescriptionSessionResponse() {}
    
    public PrescriptionSessionResponse(PrescriptionData presData, int correctPercent, String programTitle) {
        this.prescriptionData = presData;
        this.correctPercent = correctPercent;
        this.programTitle = programTitle;
    }

    public PrescriptionData getPrescriptionData() {
        return prescriptionData;
    }

    public void setPrescriptionData(PrescriptionData prescriptionData) {
        this.prescriptionData = prescriptionData;
    }

    public int getCorrectPercent() {
        return correctPercent;
    }

    public void setCorrectPercent(int correctPercent) {
        this.correctPercent = correctPercent;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
    }
    
}
