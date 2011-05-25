package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlCheckedAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizResultsHtmlAction;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaTestRunResult;
import hotmath.util.Jsonizer;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.visitors.NodeVisitor;


/**
 * Return the QuizHtml with the results stored as a list of pids that are
 * correct.  
 * 
 * Also, the HTML radiobuttons are pre-selected and readonly.
 * 
 * 
 * rpcData: (all from QuizHtml) and
 * quiz_result_json,quiz_correct_count,quiz_question_count
 * 
 * @TODO: replace RpcData with QuizHtmlResults
 */
public class GetQuizResultsHtmlCommand implements ActionHandler<GetQuizResultsHtmlAction, RpcData> {

    @Override
    public RpcData execute(final Connection conn, GetQuizResultsHtmlAction action) throws CmRpcException {
        try {
            HaTestRun testRun = HaTestRunDao.getInstance().lookupTestRun(action.getRunId());
            
            List<HaTestRunResult> results = testRun.getTestRunResults();
            String resultJson = "";
            for (HaTestRunResult r : results) {
                if (resultJson.length() > 0)
                    resultJson += ",";
                resultJson += Jsonizer.toJson(r);
            }
            resultJson = "[" + resultJson + "]";

            GetQuizHtmlCheckedAction quizResultsAction = new GetQuizHtmlCheckedAction(testRun.getHaTest().getTestId());
            RpcData quizRpc = ActionDispatcher.getInstance().execute(quizResultsAction);

            
            /** Preselect HTML results with user's selection and make readonly
             * 
             */
            String key="quiz_html";
            quizRpc.putData(key, markUserSelections(quizRpc.getDataAsString(key), results));
            
            
            
            quizRpc.putData("quiz_result_json", resultJson);
            quizRpc.putData("quiz_question_count", testRun.getHaTest().getTestQuestionCount());
            quizRpc.putData("quiz_correct_count", testRun.getAnsweredCorrect());

            return quizRpc;
        } catch (Exception e) {
            throw new CmRpcException(e);
        } finally {
            SqlUtilities.releaseResources(null, null, null);
        }

    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetQuizResultsHtmlAction.class;
    }

    
    Parser parser = new Parser();
    Node root=null;
    int selectedAnswer=-1;
    int currentAnswer=0;    
    
    /** Parse quiz HTML and mark the users current selections as 
     *  readonly values.  This is so we do not have to dynamically
     *  select the answers on the client which is buggy due to 
     *  IE not being able add/remove radiobuttons to the DOM without reset 
     *  to original value.
     *  
     *  Returns a readonly representation of the users quiz html 
     *  with users selections made.
     *  
     */
    private String markUserSelections(String html,final List<HaTestRunResult> results) throws Exception {
        NodeVisitor visitor = new  NodeVisitor() {
            public void visitTag(org.htmlparser.Tag tag) {
                if(root == null)
                    root = tag;
                
                String guid=null;

                String tn = tag.getTagName().toLowerCase();
                if(tn.equals("div") && tag.getAttribute("guid") != null ) {
                    guid=tag.getAttribute("guid");
                    
                    selectedAnswer = getUserSelection(guid,results);
                    currentAnswer = 0;
                }
                else if(tag.getTagName().equalsIgnoreCase("input")) {
                    tag.setAttribute("disabled","true");
                    tag.setAttribute("onclick","");
                    if(currentAnswer++ == selectedAnswer) {
                        tag.setAttribute("checked", "true");
                    }
                }
            }
        };
        parser.setInputHTML(html);
        parser.visitAllNodesWith(visitor);
        html = getDocumentNode(root).toHtml();
        return html;
    }

    /** Return the users selection for the question for guid.  Return 
     * -1 if user has not made a selection.
     */
    private int getUserSelection(String guid, List<HaTestRunResult> results) {
        for(HaTestRunResult r: results) {
            if(r.getPid().equals(guid)) {
                if(r.isAnswered()) {
                    return r.getResponseIndex();
                }
            }
        }
        return -1;
    }
    
    private Node getDocumentNode(Node nl) {
        Node parent = nl;
        while(parent.getParent() != null)
            parent = parent.getParent();
        
        return parent;
    }    
}
