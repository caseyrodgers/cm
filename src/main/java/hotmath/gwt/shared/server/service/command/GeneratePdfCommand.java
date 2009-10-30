package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.cm.util.report.StudentDetailReport;
import hotmath.cm.util.report.StudentReportCard;
import hotmath.cm.util.report.StudentSummaryReport;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;

public class GeneratePdfCommand implements ActionHandler<GeneratePdfAction, CmWebResource>{

    
    
    /** 
     * Performs the action: generate a PDF and return absolute file name to created PDF
     */
    @Override
    public CmWebResource execute(Connection conn, GeneratePdfAction action) throws Exception {
        String reportName=null;
    
        Integer adminId = action.getAdminId();
        List<Integer> studentUids = action.getStudentUids();
        PdfType pdfType = action.getPdfType();
        
        String reportId = new CmAdminDao().getPrintableStudentReportId(studentUids);
        
        ByteArrayOutputStream baos = null;
        if (pdfType == PdfType.STUDENT_SUMMARY) {
            StudentSummaryReport ss = new StudentSummaryReport();
            baos = ss.makePdf(reportId, adminId);
            reportName = ss.getReportName();
        }
        else if (pdfType == PdfType.STUDENT_DETAIL) {
            StudentDetailReport sd = new StudentDetailReport();
            baos = sd.makePdf(reportId, adminId);
            reportName = sd.getReportName();
        }
        else if (pdfType == PdfType.REPORT_CARD) {
            StudentReportCard sr = new StudentReportCard();
            baos = sr.makePdf(reportId, adminId);
            reportName = sr.getReportName();
        }
        else {
            throw new IllegalArgumentException("Unrecognized report type: " + pdfType);
        }
        
        // write PDF ByteArrayOutputStream to a ServletOutputStream
        if (baos != null) {
            
            // write out to temporary file to be clean up later
            String outputBase = CmWebResourceManager.getInstance().getFileBase();
            String fileName = outputBase + "/rc_" + System.currentTimeMillis() + ".pdf";
            FileOutputStream fw = null;
            try {
                fw = new FileOutputStream(fileName);
                baos.writeTo(fw);
                
                return new CmWebResource(fileName,CmWebResourceManager.getInstance().getWebBase());
            }
            finally {
                fw.close();
            }
        }
        else {
            throw new Exception("PDF generation failed");
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GeneratePdfAction.class;
    }
  
} 