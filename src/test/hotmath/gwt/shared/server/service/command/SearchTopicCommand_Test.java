package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_core.client.model.TopicSearchResults;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchApp;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchType;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;

public class SearchTopicCommand_Test extends TestCase {

    public SearchTopicCommand_Test(String name) {
        super(name);
    }


    public void testItSugTest() throws Exception {
        TopicSearchResults results = new SearchTopicCommand().execute(HMConnectionPool.getConnection(),
                new SearchTopicAction("sas-postulate",SearchApp.TEST, 0));
        assertTrue(results.getTopics().size() > 0);
        assertTrue(results.getSuggestions() != null);
    }
    

    
    public void testItSug() throws Exception {
        TopicSearchResults results = new SearchTopicCommand().execute(HMConnectionPool.getConnection(),
                new SearchTopicAction(SearchType.LESSON_LIKE, SearchApp.TEST, "Length", 0));
        assertTrue(results.getTopics().size() > 0);
        assertTrue(results.getSuggestions() != null);
    }
    
    public void testIt1() throws Exception {
        TopicSearchResults results = new SearchTopicCommand().execute(HMConnectionPool.getConnection(),
                new SearchTopicAction(SearchType.LESSON_LIKE, SearchApp.TEST, "di", 0));
        assertTrue(results.getTopics().size() > 0);
    }

    public void testIt() throws Exception {
        TopicSearchResults results = new SearchTopicCommand().execute(HMConnectionPool.getConnection(),
                new SearchTopicAction(SearchType.LESSON_LIKE, SearchApp.TEST, "Integer", 0));
        assertTrue(results.getTopics().size() > 0);
    }
}
