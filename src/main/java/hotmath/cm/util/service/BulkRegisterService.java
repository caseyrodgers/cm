package hotmath.cm.util.service;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;

import java.io.File;
import java.io.IOException;
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

	@SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        try {
            Integer aid = Integer.parseInt(req.getParameter("aid"));
        	boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        	
        	if (logger.isDebugEnabled()) {
        	    logger.debug("+++ isMultipart: " + isMultipart);
        	    logger.debug("+++ aid: " + aid);
            }

        	String returnJson=null;
            if (isMultipart) {

            	File tmpDir = new File("/tmp");
            	FileItemFactory fiFactory = new DiskFileItemFactory(100000, tmpDir);

            	ServletFileUpload sfu = new ServletFileUpload(fiFactory);
            	List<FileItem> fileItems = sfu.parseRequest(req);
            	if (fileItems != null  && fileItems.size() > 0) {

            	    BulkRegLoader brLoader = new BulkRegLoader();
            	    brLoader.processUpload(fileItems);
                    
                    boolean hasErrors = brLoader.hasErrors();
                    boolean hasDuplicates = brLoader.hasDuplicateNames() || brLoader.hasDuplicatePasswords();
                    boolean contentIsAcceptable = brLoader.contentIsAcceptable();

                    returnJson = 
                        "{" +
                         "key:'" + brLoader.getKey() + "', " +
                         "status:'" + ((hasErrors || !contentIsAcceptable) ? "Error" : "Successful") + "', " +
                         "msg:'";

                    if (hasErrors && !hasDuplicates && contentIsAcceptable) {
                        returnJson += "Uploaded file contains errors, please review and correct.";
                    }
                    else if (hasDuplicates) {
                        returnJson += "Uploaded file contains duplicates, please review and correct;";
                        if (brLoader.hasDuplicateNames()) {
                         	returnJson += " duplicate names: " + brLoader.getDuplicateNames();
                        }
                        if (brLoader.hasDuplicatePasswords()) {
                        	if (brLoader.hasDuplicateNames()) returnJson += ", ";
                        	returnJson += " duplicate passwords: " + brLoader.getDuplicatePasswords();
                        }
                    }
                    else if (! contentIsAcceptable) {
                    	returnJson += "Uploaded file must be a tab-delimited text file (.txt) or Excel (.xls) file.";
                    }
                    else if (brLoader.hasNumericContent()){
                        returnJson += "File uploaded successfully; numeric values may have been changed, please check.";
                    }
                    else {
                        returnJson += "File uploaded successfully.";
                    }
                    
                    returnJson += "' }";
            		
                    CmCacheManager.getInstance().addToCache(CacheName.BULK_UPLOAD_FILE, brLoader.getKey(), brLoader);
            	}
            	else {
                	if (logger.isDebugEnabled())
            		    logger.debug("+++ No Files found!");
                	returnJson = "{status: 'Error', msg:'No files to upload' }";
            	}

            }
            else {
            	returnJson = "{status: 'Error', msg:'Must be a multi-part form' }";
            }

            resp.getWriter().write(returnJson);
        }
        catch(Exception e) {
        	logger.error("Exception uploading bulk reg file", e);
        	String res = String.format("{status: 'Error', msg:'%s' }", e.getMessage());
            resp.getWriter().write(res);
        }
    }
}
