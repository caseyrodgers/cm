package hotmath.gwt.cm_mobile_shared.server.rpc;

import hotmath.gwt.cm_mobile_shared.client.rpc.GetMobileTopicListAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetMobileTopicListCommand implements ActionHandler<GetMobileTopicListAction, CmList<Topic>>{

    @Override
    public CmList<Topic> execute(Connection conn, GetMobileTopicListAction action) throws Exception {
        CmList<Topic> topics = new CmArrayList<Topic>();
        PreparedStatement ps=null;
        try {
            String sql = "select distinct lesson, file " +
                         "from HA_PROGRAM_LESSONS_static " +
                         "order by lesson";
            ps = conn.prepareStatement(sql);
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
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetMobileTopicListAction.class;
    }
}
