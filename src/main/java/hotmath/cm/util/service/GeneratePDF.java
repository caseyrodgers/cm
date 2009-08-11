package hotmath.cm.util.service;

import hotmath.cm.util.report.StudentDetailReport;
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
		makePdf(request, response, "GET");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		makePdf(request, response, "POST");
	}

	/**
	 * Performs the action: generate a PDF from a GET or POST.
	 * 
	 * @param request	the request object
	 * @param response	the response object
	 * @param methodGetPost	the method that was used in the form
	 */
	public void makePdf(HttpServletRequest request, HttpServletResponse response, String methodGetPost) {
		
		String type = "unknown";
		String reportId = null;
		Integer adminId = -1;
		try {

			reportId = request.getParameter("id");
			adminId = Integer.parseInt(request.getParameter("aid"));
			type = request.getParameter("type");
			
			ByteArrayOutputStream baos = null;
			if (type.equals("studentSummary")) {
				StudentSummaryReport ssr = new StudentSummaryReport();
				baos = ssr.makePdf(reportId, adminId);
			}
			else if (type.equals("studentDetail")) {
				StudentDetailReport sdr = new StudentDetailReport();
				baos = sdr.makePdf(reportId, adminId);
			}
			
			// write PDF ByteArrayOutputStream to a ServletOutputStream
			if (baos != null) {
				// setting some response headers
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				response.setHeader("Pragma", "public");
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
				// send to error page?
			}

		} catch (Exception e) {
			logger.error(String.format("*** Error generating %s PDF for adminUID: %d", type, adminId), e);
		}
	}

	public void destroy() {
	}
}