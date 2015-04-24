package hotmath.gwt.shared.server.service.command.cm2;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.test.HaTestSet;
import hotmath.cm.test.HaTestSetQuestion;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_core.client.model.QuizCm2Question;
import hotmath.gwt.cm_rpc.client.rpc.cm2.GetCm2QuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.QuizAnswer;
import hotmath.gwt.cm_rpc.client.rpc.cm2.QuizCm2HtmlResult;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetQuizCurrentResultsAction;
import hotmath.gwt.shared.server.service.command.GetQuizCurrentResultsCommand;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.StudentUserProgramModel;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import sb.util.SbFile;


/** 
 * 
 * creates cm2 quiz question abstractions.
 *
 *
 */
public class GetCm2QuizHtmlCommand implements ActionHandler<GetCm2QuizHtmlAction, QuizCm2HtmlResult> {

    @Override
    public QuizCm2HtmlResult execute(Connection conn, GetCm2QuizHtmlAction action) throws Exception {
        if (action.getTestId() == 0) {
            throw new CmRpcException("Invalid QUIZ request: " + action);
        }

        try {
            StudentUserProgramModel programInfo = CmUserProgramDao.getInstance().loadProgramInfoForTest(action.getTestId());

            
            int testId = action.getTestId();

            HaTest haTest = HaTestDao.getInstance().loadTest(testId);
            
            String testTitle = null;
            if(programInfo.getCustomProgramId() > 0) {
                testTitle = CmCustomProgramDao.getInstance().getCustomProgram(conn,programInfo.getCustomProgramId()).getProgramName();
            }
            else if(programInfo.getCustomQuizId() > 0) {
                testTitle =  programInfo.getCustomQuizName();
            }
            else {
                testTitle = haTest.getTitle();
            }
                        
            HaTestSet testSet = new HaTestSet(conn,haTest.getPids());
            
            QuizCm2HtmlResult result = new QuizCm2HtmlResult();
            for(HaTestSetQuestion q: testSet.getQuestions()) {
                
                // String htmlWithAbsolute = HotMathUtilities.makeAbsolutePaths(absolutePath, q.getQuestionHtml());
                
                String htmlWithAbsolute = q.getQuestionHtml().replace("/help/solutions/", "http://test.catchupmath.com/help/solutions/");
                htmlWithAbsolute = htmlWithAbsolute.replace("/images/specialchars/", "http://test.catchupmath.com/images/specialchars/");
                
                QuizCm2Question question = new QuizCm2Question(action.getTestId(), q.getProblemIndex(), htmlWithAbsolute);
                result.getQuizQuestions().add(question);
            }
            
            GetQuizCurrentResultsAction resultsAction = new GetQuizCurrentResultsAction(action.getTestId());
            CmList<RpcData> currentResponses = new GetQuizCurrentResultsCommand().execute(conn, resultsAction);
            
            // convert to more specific type
            List<QuizAnswer> answers = new ArrayList<QuizAnswer>();
            for(RpcData r: currentResponses) {
                answers.add(new QuizAnswer(r.getDataAsString("pid"), r.getDataAsInt("answer")));
            }
            result.setCurrentSelections(answers);
            result.setTestId(haTest.getTestId());
            result.setTitle(testTitle);
            return result;
            
        } catch (Exception e) {
            /** for catching error during debugging */
            throw e;
        }
        
        
    }


    static public String readQuizHtmlTemplate() throws Exception {
        String file = CatchupMathProperties.getInstance().getCatchupRuntime() + "/quiz_template_cm2.vm";
        return new SbFile(file).getFileContents().toString("\n");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCm2QuizHtmlAction.class;
    }
}
