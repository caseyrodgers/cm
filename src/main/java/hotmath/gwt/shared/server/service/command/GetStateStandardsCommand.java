package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.StateStandard;
import hotmath.gwt.shared.client.rpc.action.GetStateStandardsAction;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;


public class GetStateStandardsCommand implements ActionHandler<GetStateStandardsAction, CmList<StateStandard>> {

    Logger logger = Logger.getLogger(GetStateStandardsCommand.class);

    @Override
    public CmList<StateStandard> execute(Connection conn, GetStateStandardsAction action) throws Exception {

        String topic = action.getTopic();
        if(topic.startsWith("topics/"))
            topic = topic.substring(7);
        
        String sql = "select * from inmh_standard where topic = ? and standard_state = ? order by standard_name";
        PreparedStatement ps = null;
        
        CmList<StateStandard> list = new CmArrayList<StateStandard>();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, topic);
            ps.setString(2, action.getState());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new StateStandard(topic,  rs.getString("standard_name"),  rs.getString("standard_name_new")));
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
