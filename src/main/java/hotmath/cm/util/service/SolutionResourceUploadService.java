package hotmath.cm.util.service;

import hotmath.cm.util.CatchupMathProperties;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class SolutionResourceUploadService extends HttpServlet {
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setContentType("text/html");
        
        FileItem uploadItem = getFileItem(req);
        if(uploadItem == null) {
            resp.getWriter().write("No uploaded resource");
            return;
        }
        FileOutputStream fos = null;
        try {
            
            /** file will go into
             *  PID_HOME/resources/name_of_file
             */
            File fileName = new File(uploadItem.getName());
            
            File resourcesDir=null;
            /* path to solution's resource dir
             * 
             */
            if(pid != null && pid.length()>0) { 
                SolutionDef solDef = new SolutionDef(pid);
                resourcesDir = new File(solDef.getResourcesPath());
            }
            else {
                resourcesDir = new File(CatchupMathProperties.getInstance().getSolutionBase() + "/help/solutions/resources");
            }
            if(!resourcesDir.exists())
                resourcesDir.mkdirs();
            
            File resourceFile = new File(resourcesDir,fileName.getName());
            fos = new FileOutputStream(resourceFile);
            byte buffer[] = new byte[1024];
            BufferedInputStream bis = new BufferedInputStream(uploadItem.getInputStream());
            int cnt=0;
            while((cnt=bis.read(buffer,0, 1024)) > -1) {
                fos.write(buffer,0, cnt);    
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            fos.close();
            uploadItem.delete();
        }
        resp.getWriter().write("OK");
    }


    String pid;
    /** Get the uploaded file and extract the pid and
     *  store in module param.
     *  
     * @param req
     * @return
     */
    private FileItem getFileItem(HttpServletRequest req) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        
        FileItem fileRet=null;
        try {
            List items = upload.parseRequest(req);
            Iterator it = items.iterator();
            
            while(it.hasNext()) {
                FileItem item = (FileItem) it.next();
                if(!item.isFormField() && "resourceUpload.field".equals(item.getFieldName())) {
                    fileRet = item;
                }
                else if("resourceUpload.pid".equals(item.getFieldName())) {
                    pid = item.getString();
                }
            }
            return fileRet;
        }
        catch(FileUploadException e){
            return null;
        }
    }
}