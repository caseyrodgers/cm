package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.TopicMatch;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchApp;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchType;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;

public class SearchTopicCommand_Test extends TestCase {
    
    public SearchTopicCommand_Test(String name) {
        super(name);
    }
  
    public void testIt1() throws Exception {
        CmList<TopicMatch> results = new SearchTopicCommand().execute(HMConnectionPool.getConnection(), new SearchTopicAction(SearchType.LESSON_LIKE, SearchApp.TEST,"di",0));
        assertTrue(results.size() > 0);
    }
    
    public void testIt() throws Exception {
        CmList<TopicMatch> results = new SearchTopicCommand().execute(HMConnectionPool.getConnection(), new SearchTopicAction(SearchType.LESSON_LIKE, SearchApp.TEST, "Integer",0));
        assertTrue(results.size() > 0);
    }
}
