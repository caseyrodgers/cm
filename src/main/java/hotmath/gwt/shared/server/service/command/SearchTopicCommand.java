package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

/**
 * Read an existing prescription based on a test run
 * 
 * and return data that represents a single program prescription lesson.
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
        PreparedStatement ps=null;
        try {
            String sql = "select distinct lesson, file " +
                         "from HA_PROGRAM_LESSONS_static " +
                         "where lesson like ? " +  
                         "order by lesson";
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + action.getSearch() + "%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                topics.add(new Topic(rs.getString("lesson"), rs.getString("file")));        
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        
        return topics;        
    }
}

