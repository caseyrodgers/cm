package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchType;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;

public class SearchTopicCommand_Test extends TestCase {
    
    public SearchTopicCommand_Test(String name) {
        super(name);
    }
  
    public void testIt1() throws Exception {
        new SearchTopicCommand().execute(HMConnectionPool.getConnection(), new SearchTopicAction(SearchType.LESSON_LIKE, "di"));
    }
    
    public void testIt2() throws Exception {
        new SearchTopicCommand().execute(HMConnectionPool.getConnection(), new SearchTopicAction(SearchType.LESSON_LIKE, "%Integer%"));
    }

}
