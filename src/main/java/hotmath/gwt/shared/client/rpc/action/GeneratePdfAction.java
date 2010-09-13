package hotmath.gwt.shared.client.rpc.action;

import java.util.List;
import java.util.Map;

import hotmath.gwt.cm_rpc.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActionBase;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;

public class GeneratePdfAction extends ActionBase implements Action<CmWebResource>{

	@Override
    public String toString() {
        return "GeneratePdfAction [adminId=" + adminId + ", pdfType=" + pdfType + ", studentUids=" + studentUids + "]";
    }

    private static final long serialVersionUID = 6104125833256395744L;
	
	PdfType pdfType;
    Integer adminId;
    List<Integer> studentUids;
    Map<FilterType,String> filterMap;
    String title;
    
    
    GetStudentGridPageAction pageAction;


    public GeneratePdfAction() {
    	getClientInfo().setUserType(UserType.ADMIN);
    }
    
    public GeneratePdfAction(PdfType pdfType, Integer adminId, List<Integer> studentUids) {
        this.pdfType = pdfType;
        this.adminId = adminId;
        this.studentUids = studentUids;

        getClientInfo().setUserId((adminId != null)?adminId:0);
    	getClientInfo().setUserType(UserType.ADMIN);
    }
    
    public GeneratePdfAction(PdfType pdfType, Integer adminId, GetStudentGridPageAction pageAction) {
        this.pdfType = pdfType;
        this.adminId = adminId;
        this.pageAction = pageAction;

        getClientInfo().setUserId((adminId != null)?adminId:0);
    	getClientInfo().setUserType(UserType.ADMIN);
    }


    public PdfType getPdfType() {
        return pdfType;
    }

    public GetStudentGridPageAction getPageAction() {
        return pageAction;
    }

    public void setPageAction(GetStudentGridPageAction pageAction) {
        this.pageAction = pageAction;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStudentUids(List<Integer> studentUids) {
        this.studentUids = studentUids;
    }

    public Map<FilterType,String> getFilterMap() {
    	if(this.filterMap != null)
    	    return this.filterMap;
    	else if(this.pageAction != null)
    	    return this.pageAction.getFilterMap();
    	else 
    	    return null;
    }

    public void setFilterMap(Map<FilterType,String> filterMap) {
    	this.filterMap = filterMap;
    }

    public enum PdfType{
        /** student report card report
         * 
         */
        REPORT_CARD, 
        
        /** student detail report
         * 
         */
        STUDENT_DETAIL, 
        
        /** student summary report for admins
         * 
         */
        STUDENT_SUMMARY, 
        
        
        /** Assessment report from the Assessment window
         * 
         */
        GROUP_ASSESSMENT,
        
        
        /** Student list report
         * 
         */
        STUDENT_LIST};
}
