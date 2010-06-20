package hotmath.gwt.shared.server.service.command;

import hotmath.HotMathProperties;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.server.service.ActionHandlerManualConnectionManagement;
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

        /**
         * If Spanish version requested, make sure it exists .. If not give an
         * appropriate message.
         * 
         * Also, check to see if spanish version is available for this lesson
         * 
         */
        LessonResult result = new LessonResult();
        if (action.isSpanish()) {
            if (!new File(filePath, action.getFile()).exists()) {
                result.setWarning("Spanish version of this lesson does not exist.");
                action.setSpanish(false);
            }
        }
        else {
            /** check to see if a Spanish version is available for this text
             * 
             */
            action.setSpanish(true);
            result.setHasSpanish(new File(filePath, action.getFile()).exists());
            action.setSpanish(false);
        }
        String html = new SbFile(filePath + "/" + action.getFile()).getFileContents().toString("\n");
        HmContentExtractor ext = new HmContentExtractor();
        String htmlCooked = ext.extractContent(html, action.getBaseDirectory());

        result.setLesson(htmlCooked);
        return result;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetReviewHtmlAction.class;
    }
}
