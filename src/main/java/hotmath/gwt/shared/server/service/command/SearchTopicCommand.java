package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.search.HMIndexSearcher;
import hotmath.search.HMIndexWriter;
import hotmath.search.HMIndexWriterFactory;
import hotmath.search.Hit;

import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * 
 * Search for a lesson/topic using wildcards
 * 
 * 
 * @author casey
 * 
 */
public class SearchTopicCommand implements ActionHandler<SearchTopicAction, CmList<Topic>> {

    static Logger __logger = Logger.getLogger(SearchTopicCommand.class);

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SearchTopicAction.class;
    }

    @Override
    public CmList<Topic> execute(Connection conn, SearchTopicAction action) throws Exception {
        CmList<Topic> topics = new CmArrayList<Topic>();
        try {
            Hit[] results = HMIndexSearcher.getInstance().searchFor("inmh", action.getSearch());
            for (Hit hit : results) {
                
                topics.add(new Topic(hit.getTitle(), hit.getUrl()));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return topics;
    }
    
    static public void main(String as[]) {
        try {
            HMIndexWriter hmIw = HMIndexWriterFactory.getHMIndexWriter("inmh");
            hmIw.writeIndex();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
