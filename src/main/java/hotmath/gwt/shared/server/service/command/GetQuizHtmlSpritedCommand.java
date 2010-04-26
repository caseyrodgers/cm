package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlSpritedAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.io.File;
import java.sql.Connection;

import sb.util.MD5;
import sb.util.SbFile;

public class GetQuizHtmlSpritedCommand implements ActionHandler<GetQuizHtmlSpritedAction,QuizHtmlResult> {
    
    public GetQuizHtmlSpritedCommand() {}
    
    @Override
    public QuizHtmlResult execute(Connection conn, GetQuizHtmlSpritedAction action) throws Exception {
        GetQuizHtmlAction htmlAction = new GetQuizHtmlAction(action.getUid(), action.getTestId(), 0);
        QuizHtmlResult result = new GetQuizHtmlCommand().execute(conn, htmlAction);
        
        String html = result.getQuizHtml();
        result.setQuizHtml(processHtmlForSprites(html));
        return result;
    }
    
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetQuizHtmlSpritedAction.class; 
    }

    private String processHtmlForSprites(String quizHtml) throws Exception {
        String md5OfThis = MD5.getMD5(quizHtml);
        String fileBase = CmWebResourceManager.getInstance().getFileBase();
        File quizSprited = new File(fileBase,md5OfThis);
        if(!new File(quizSprited,"tutor_steps-sprited.html").exists()) {
            quizSprited.mkdirs();
            /** write to sprited file */
            new CreateQuizSprited(quizSprited, quizHtml).createQuizSpritedHtml();
        }
        
        /** return from sprited file */
        quizHtml = new SbFile(new File(quizSprited, "tutor_steps-sprited.html")).getFileContents().toString("\n");
        return quizHtml;
    }    

}
