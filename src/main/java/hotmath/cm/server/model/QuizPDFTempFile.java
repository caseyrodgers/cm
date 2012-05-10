package hotmath.cm.server.model;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.shared.client.rpc.CmWebResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

/**
 * 
 * Provide standard way to create temporary PDF file names
 * to serve back PDF results.
 * 
 * This way we do not have to create a servlet and it
 * can be cached on client.
 * 
 * 
 * @author casey
 *
 */

public class QuizPDFTempFile {
    
    Logger __logger = Logger.getLogger(QuizPDFTempFile.class);
    
    QuizResultsModel resultsModel;
    File tempFileName;
    CmWebResource webResource;
    
    public QuizPDFTempFile(QuizResultsModel resultsModel) throws Exception {
        this.resultsModel = resultsModel;
        
        tempFileName = new File(CmWebResourceManager.getInstance().getFileBase(), resultsModel.getRunId() + "-quiz_results.pdf");
        if(!tempFileName.exists()) {
            makeWebResource();
        }
        webResource = new CmWebResource(tempFileName.getPath(), CmWebResourceManager.getInstance().getFileBase(), CmWebResourceManager.getInstance().getWebBase());
    }
    
    public String getPdfUrl() {
        return webResource.getUrl("");
    }
    
    private void makeWebResource() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(resultsModel.getQuizPDFbytes(), 0, resultsModel.getQuizPDFbytesLength());
        
        __logger.info("Writing PDF output: " + tempFileName);
        FileOutputStream fw = null;
        try {
            fw = new FileOutputStream(tempFileName);
            os.writeTo(fw);
        }
        finally {
            if (fw != null) fw.close();
        }       
    }

}