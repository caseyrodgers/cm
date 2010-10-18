package hotmath.gwt.cm_activity.server.rpc;
import hotmath.gwt.cm_activity.client.WordProblemSet;
import hotmath.gwt.cm_activity.client.rpc.GetWordProblemSetAction;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetWordProblemSetCommand implements ActionHandler<GetWordProblemSetAction, WordProblemSet>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetWordProblemSetAction.class;
    }

    
    @Override
    public WordProblemSet execute(Connection conn, GetWordProblemSetAction action) throws Exception {
        WordProblemsDao dao = new WordProblemsDao(conn);
        
        WordProblemSet problemSet = new WordProblemSet();
        
        problemSet.setProblems(dao.getWordProblems());
        
        return problemSet;
    }
}
