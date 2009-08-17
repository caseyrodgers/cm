package hotmath.cm.util.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

/**
 * BulkRegisterService is exposed as a servlet
 * 
 * @author bob
 * 
 */
public class BulkRegisterService extends HttpServlet {

	private static final long serialVersionUID = 5021091911533642662L;

	private static final Logger logger = Logger.getLogger(BulkRegisterService.class);

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        try {
            Integer aid = Integer.parseInt(req.getParameter("aid"));
        	boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        	
        	if (logger.isDebugEnabled()) {
        	    logger.debug("+++ isMultipart: " + isMultipart);
        	    logger.debug("+++ aid: " + aid);
            }

            StringBuilder sb = new StringBuilder();
            if (isMultipart) {

            	File tmpDir = new File("/tmp");
            	FileItemFactory fiFactory = new DiskFileItemFactory(100000, tmpDir);

            	ServletFileUpload sfu = new ServletFileUpload(fiFactory);
            	List<FileItem> fileItems = sfu.parseRequest(req);
            	if (fileItems != null  && fileItems.size() > 0) {
            		for (FileItem fi: fileItems) {
            			InputStream is = fi.getInputStream();
            			BulkRegLoader brLoader = new BulkRegLoader();
            			brLoader.setInputStream(is);
            			brLoader.load();
                        is.close();

                        boolean hasErrors = brLoader.hasErrors();

                        sb.append("{status:'");
                        sb.append((hasErrors) ? "Error" : "Successful");
                        sb.append("',msg:'");
                        if (hasErrors) {
                        	sb.append("Uploaded file contains errors, please review and correct.");
                        }
                        else {
                        	sb.append("File uploaded successfully.");
                        }
                        sb.append("' }");
                        
            			//TODO: put student/password List in Cache
            		}
            	}
            	else {
                	if (logger.isDebugEnabled())
            		    logger.debug("+++ No Files found!");
                	sb.append("{status: 'Error', msg:'No files to upload' }");
            	}

            }
            else {
            	sb.append("{status: 'Error', msg:'Must be a multi-part form' }");
            }
            resp.getWriter().write(sb.toString());
        }
        catch(Exception e) {
        	logger.error("Exception uploading bulk reg file", e);
        	String res = String.format("{status: 'Error', msg:'%s' }", e.getMessage());
            resp.getWriter().write(res);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ;
    }
}
