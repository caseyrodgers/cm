package hotmath.cm.util.service;

import hotmath.cm.util.report.StudentDetailReport;
import hotmath.cm.util.report.StudentReportCard;
import hotmath.cm.util.report.StudentSummaryReport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class GeneratePDF extends HttpServlet {

	private static final long serialVersionUID = 2788260006560387781L;
	
	private static final Logger logger = Logger.getLogger(GeneratePDF.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
        try {
            makePdf(request, response, "GET");
        } catch (Exception e) {
            logger.info("Error generating report card", e);
            request.getRequestDispatcher("/gwt-resources/report_card_fail.html").forward(request, response);
        }	    
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
        try {
            makePdf(request, response, "POST");
        } catch (Exception e) {
            logger.info("Error generating report card", e);
            request.getRequestDispatcher("/gwt-resources/report_card_fail.html").forward(request, response);
        }       	    
	}

	/**
	 * Performs the action: generate a PDF from a GET or POST.
	 * 
	 * @param request	the request object
	 * @param response	the response object
	 * @param methodGetPost	the method that was used in the form
	 */
	public void makePdf(HttpServletRequest request, HttpServletResponse response, String methodGetPost) throws Exception {
		
		String type = "unknown";
		String reportId = null;
		Integer adminId = -1;
		reportId = request.getParameter("id");
		adminId = Integer.parseInt(request.getParameter("aid"));
		type = request.getParameter("type");
		Boolean checkStatus = Boolean.valueOf(request.getParameter("checkStatus"));
		String reportName;
		
		if (checkStatus) {
			checkStatus(reportId, request, response);
			return;
		}
		
		ByteArrayOutputStream baos = null;
		if (type.equals("studentSummary")) {
			StudentSummaryReport ss = new StudentSummaryReport();
			baos = ss.makePdf(reportId, adminId);
			reportName = ss.getReportName();
		}
		else if (type.equals("studentDetail")) {
			StudentDetailReport sd = new StudentDetailReport();
			baos = sd.makePdf(reportId, adminId);
			reportName = sd.getReportName();
		}
		else if (type.equals("reportCard")) {
			StudentReportCard sr = new StudentReportCard();
			baos = sr.makePdf(reportId, adminId);
			reportName = sr.getReportName();
		}
	    else {
			throw new IllegalArgumentException("Unrecognized report type: " + type);
		}
		
		// write PDF ByteArrayOutputStream to a ServletOutputStream
		if (baos != null) {
			// setting some response headers
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Disposition", "attachment; filename=" + reportName + ".pdf");
			// setting the content type
			response.setContentType("application/pdf");
			// the content length is needed for MSIE @#%&!
			response.setContentLength(baos.size());
			// write ByteArrayOutputStream to the ServletOutputStream
			ServletOutputStream out = response.getOutputStream();
			baos.writeTo(out);
			out.flush();
		}
		else {
			throw new Exception("PDF generation failed");
		}

	}

	public void destroy() {
	}
	
	private void checkStatus(String reportId, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        StringBuilder sb = new StringBuilder();
        
        //TODO: check actual status for specified reportId
        
        sb.append("{status:'").append("done");
        sb.append("' }");
        resp.getWriter().write(sb.toString());
	}
}