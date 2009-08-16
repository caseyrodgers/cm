package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetStateStandardsAction;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;



public class GetStateStandardsCommand implements ActionHandler<GetStateStandardsAction, CmList<String>> {

    Logger logger = Logger.getLogger(GetStateStandardsCommand.class);

    @Override
    public CmList<String> execute(Connection conn, GetStateStandardsAction action) throws Exception {

        String topic = action.getTopic();
        if(topic.startsWith("topics/"))
            topic = topic.substring(7);
        
        String sql = "select * from inmh_standard where topic = ? order by standard_name";
        PreparedStatement ps = null;
        
        CmList<String> list = new CmArrayList<String>();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, topic);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(String.format("%s %s", rs.getString("standard_name"), rs.getString("standard_num")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null, ps,null);
        }
        
        return list;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetStateStandardsAction.class;
    }
}
