package hotmath.gwt.shared.server.service.command;

import hotmath.HotMathProperties;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.util.HmContentExtractor;

import java.io.File;
import java.sql.Connection;

import org.apache.log4j.Logger;

import sb.util.SbFile;

public class GetReviewHtmlCommand implements ActionHandler<GetReviewHtmlAction, LessonResult>,
        ActionHandlerManualConnectionManagement {

    static Logger logger = Logger.getLogger(GetReviewHtmlCommand.class);

    @Override
    public LessonResult execute(Connection conn, GetReviewHtmlAction action) throws Exception {
        String filePath = HotMathProperties.getInstance().getHotMathWebBase();

        /** Always check to see if there is a Spanish version of this file.
         *  The GUI can use this to determine which controls to enable.
         * 
         */
        LessonResult result = new LessonResult();
        result.setHasSpanish(checkIfSpanishAvailable(filePath, action.getFile()));
        
        /** Read the appropriate file and return HTML.
         * 
         * If Spanish is requested but not available, return English and a warning message
         * 
         */
        if(action.isSpanish() && !result.isHasSpanish()) {
            result.setWarning("Spanish version of this lesson not available");
            action.setSpanish(false);
        }
        String html = new SbFile(filePath + "/" + getFile(action.getFile(), action.isSpanish())).getFileContents().toString("\n");
        
        HmContentExtractor ext = new HmContentExtractor();
        String htmlCooked = ext.extractContent(html, getBaseDirectory(action.isSpanish()));

        htmlCooked = fixupForUniqueResourceLoad(htmlCooked, action.getUniqueInstanceKey());
        
        result.setLesson(htmlCooked);
        return result;
    }
    

    /** Replace call to doLoadResource, with a specific identifier.  This is to allow having multiple
     * review viewers up ... only changing the one viewable.  Otherwise, when a link is clicked all viewers would be
     * affected.  This is a problem in the lesson explorer.
     * 
     */    
    private String fixupForUniqueResourceLoad(String htmlCooked, int uniqInstance) {
        return htmlCooked.replace("doLoadResource(", "doLoadResource_Gwt(" + uniqInstance + ",");
    }

    private String getFile(String file, boolean isSpanish) {
        return getLangPath(isSpanish) + file;
    }
    
    private String getBaseDirectory(boolean isSpanish) {
        return getLangPath(isSpanish) + "topics";
    }

    private String getLangPath(boolean isSpanish) {
    	return "/hotmath_help" + (isSpanish ? "/spanish/" : "/");
    }

    /** return true if a spanish version of this file exists
     * 
     * @param file
     * @return
     * @throws Exception
     */
    private boolean checkIfSpanishAvailable(String filePath, String file) throws Exception {
        File f = new File(filePath,getFile(file,true));
        return f.exists();
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetReviewHtmlAction.class;
    }
}
