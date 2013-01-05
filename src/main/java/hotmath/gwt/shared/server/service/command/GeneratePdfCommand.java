package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.cm.util.FileUtil;
import hotmath.cm.util.report.GroupAssessmentReport;
import hotmath.cm.util.report.StudentAssignmentReport;
import hotmath.cm.util.report.StudentDetailReport;
import hotmath.cm.util.report.StudentListReport;
import hotmath.cm.util.report.StudentReportCard;
import hotmath.cm.util.report.StudentSummaryReport;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class GeneratePdfCommand implements ActionHandler<GeneratePdfAction, CmWebResource>{

    Logger logger = Logger.getLogger(GeneratePdfCommand.class);

    /** 
     * Performs the action: generate a PDF and return absolute file name to created PDF
     */
    @Override
    public CmWebResource execute(Connection conn, GeneratePdfAction action) throws Exception {
    	try {

    		String reportName=null;

    		/** Either the pageAction is set, in which we retrieve
    		 * from cache.  Or a List of UIDS is sent.
    		 * 
    		 * @TODO: Combine into one?
    		 */
    		Integer adminId = action.getAdminId();
    		List<Integer> studentUids = null;
    		if(action.getPageAction() != null) {
    			List<StudentModelI> studentPool = new GetStudentGridPageCommand().getStudentPool(action.getPageAction());
    			studentUids = new ArrayList<Integer>();
    			for(StudentModelI sm: studentPool) {
    				studentUids.add(sm.getUid());
    			}
    			logger.info("page action NOT NULL");
    		}
    		else {
    			logger.info("page action NULL");
    			studentUids = action.getStudentUids();
    		}
    		PdfType pdfType = action.getPdfType();
    		Date fromDate = action.getFromDate();
    		Date toDate = action.getToDate();

			logger.info("student UIDS.size(): " + ((studentUids != null)? studentUids.size():0));
    		String reportId = CmAdminDao.getInstance().getPrintableStudentReportId(studentUids);

    		ByteArrayOutputStream baos = null;
    		if (pdfType == PdfType.STUDENT_SUMMARY) {
    			StudentSummaryReport ss = new StudentSummaryReport();
    			ss.setFilterMap(action.getFilterMap());
    			baos = ss.makePdf(reportId, adminId);
    			reportName = ss.getReportName();
    		}
    		else if (pdfType == PdfType.STUDENT_DETAIL) {
    			StudentDetailReport sd = new StudentDetailReport();
    			baos = sd.makePdf(conn, reportId, adminId, fromDate, toDate);
    			reportName = sd.getReportName();
    		}
    		else if (pdfType == PdfType.REPORT_CARD) {
    			logger.info("creating Report Card");
    			StudentReportCard sr = new StudentReportCard();
    			baos = sr.makePdf(conn, reportId, adminId, fromDate, toDate);
    			reportName = sr.getReportName();
    		}
    		else if (pdfType == PdfType.GROUP_ASSESSMENT) {
    			GroupAssessmentReport gr = new GroupAssessmentReport();
    			gr.setFilterMap(action.getFilterMap());
    			baos = gr.makePdf(conn, reportId, adminId);
    			reportName = gr.getReportName();
    		}
    		else if(pdfType == PdfType.STUDENT_LIST) {
    			StudentListReport slr = new StudentListReport(action.getTitle());
    			slr.setFilterMap(action.getFilterMap());
    			baos = slr.makePdf(conn, reportId, adminId, action.getStudentUids());
    			reportName = slr.getReportName();
    		}
    		else if(pdfType == PdfType.ASSIGNMENT_REPORT) {
    			StudentAssignmentReport sar = new StudentAssignmentReport(action.getTitle());
    			sar.setFilterMap(action.getFilterMap());
    			baos = sar.makePdf(conn, reportId, adminId, action.getStudentUids());
    			reportName = sar.getReportName();
    		}
    		else {
    			throw new IllegalArgumentException("Unrecognized report type: " + pdfType);
    		}

    		/** 
    		 * write PDF ByteArrayOutputStream to a ServletOutputStream
    		 */
    		 assert(baos != null);

			 // write to temporary file to be cleaned up later
			 String outputBase = CmWebResourceManager.getInstance().getFileBase();

			 // if outputBase/adminId directory doesn't exist, create it

			 String unique = Long.toString(System.currentTimeMillis());

			 outputBase = outputBase + "/" + adminId;
			 String outputDir = FileUtil.ensureOutputDir(outputBase, unique);

			 File filePath = new File(outputDir, reportName + ".pdf");
			 logger.info("Writing PDF output: " + filePath);
			 FileOutputStream fw = null;
			 try {
				 fw = new FileOutputStream(filePath);
				 baos.writeTo(fw);

				 return new CmWebResource(filePath.getPath(), CmWebResourceManager.getInstance().getFileBase(), CmWebResourceManager.getInstance().getWebBase());
			 }
			 finally {
				 if (fw != null) fw.close();
			 }
    	}
    	catch(Throwable th) {
    	    /** we want to throw an exception to be able track the exception and
    	     * return an appropriate error message.  Not just null.
    	     */
    	    throw new CmRpcException("*** Error generating pdfType: " + action.getPdfType(), th);
    	}
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GeneratePdfAction.class;
    }
} 
