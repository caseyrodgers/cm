package hotmath.gwt.shared.client.rpc.action;

import java.util.List;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.CmWebResource;

public class GeneratePdfAction implements Action<CmWebResource>{

	@Override
    public String toString() {
        return "GeneratePdfAction [adminId=" + adminId + ", pdfType=" + pdfType + ", studentUids=" + studentUids + "]";
    }

    private static final long serialVersionUID = 6104125833256395744L;
	
	PdfType pdfType;
    Integer adminId;
    List<Integer> studentUids;
    public GeneratePdfAction() {}
    
    public GeneratePdfAction(PdfType pdfType, Integer adminId, List<Integer> studentUids) {
        this.pdfType = pdfType;
        this.adminId = adminId;
        this.studentUids = studentUids;
    }

    public PdfType getPdfType() {
        return pdfType;
    }

    public void setPdfType(PdfType pdfType) {
        this.pdfType = pdfType;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public List<Integer> getStudentUids() {
        return studentUids;
    }

    public void setStudentUids(List<Integer> studentUids) {
        this.studentUids = studentUids;
    }

    public enum PdfType{REPORT_CARD, STUDENT_DETAIL, STUDENT_SUMMARY, GROUP_ASSESSMENT};
}
