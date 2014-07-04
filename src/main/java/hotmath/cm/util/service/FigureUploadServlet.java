package hotmath.cm.util.service;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.testset.ha.WhiteboardDao;

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
 * FigureUploadServlet is exposed as a servlet
 * 
 * Allows uploading 'figures' into admin specific directory
 * (help/whiteboard_template/AID/UNIQ_FILENAME.[jpg|gif|png])
 * 
 * @author bob
 * 
 */
public class FigureUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 5021091911533642662L;

    private static final Logger logger = Logger.getLogger(FigureUploadServlet.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int aid = Integer.parseInt(req.getParameter("aid"));
        
        if (ServletFileUpload.isMultipartContent(req)) {
            try {
                FileItemFactory fileItemFactory = new DiskFileItemFactory();
                ServletFileUpload uploadHandlr = new ServletFileUpload(fileItemFactory);
                List<FileItem> uploadItems = uploadHandlr.parseRequest(req);
                
                FileItem firstItem = uploadItems.get(0);

                
                File tempFigureDir = new File(CmWebResourceManager.getInstance().getFileBase(), aid + "");
                for (FileItem fileItem : uploadItems) {
                    if (!fileItem.isFormField()) {
                        
                        File figureFile = new File(tempFigureDir, (System.currentTimeMillis()) + getFileExtension(firstItem)); 
                        if (ensureFilesDir(tempFigureDir)) {
                            File file = figureFile;
                            fileItem.write(file);

                            WhiteboardDao.getInstance().addWhiteboardFigure(aid, file);
                            
                            logger.info("Wrote: " + figureFile);
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new ServletException("Error uploading figure", ex);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    private String getFileExtension(FileItem firstItem) throws Exception {
        String fileType = firstItem.getContentType().toLowerCase();
        
        if(fileType.contains("png")) {
            return ".png";
        }
        else if(fileType.contains("jpg") || fileType.contains("jpeg")) {
            return ".jpg";
        }
        if(fileType.contains("gif")) {
            return ".gif";
        }
        else {
            throw new Exception("Invalid figure upload type: " + fileType);
        }
    }

    private boolean ensureFilesDir(File path) {
        boolean status = path.exists();
        if (!status) {
            status = path.mkdirs();
        }
        return status;
    }

}
