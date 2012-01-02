package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActionBase;
import hotmath.gwt.cm_rpc.client.rpc.ActionType;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;



public class SaveSolutionContextCommand extends ActionBase implements ActionHandler<SaveSolutionContextAction, RpcData>{
    
    public SaveSolutionContextCommand() {
        getActionInfo().setActionType(ActionType.STUDENT);
    }
    
    @Override
    public RpcData execute(Connection conn, SaveSolutionContextAction action) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = "insert into HA_SOLUTION_CONTEXT(uid, time_viewed, run_id, pid, variables)values(?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, action.getUid());
            ps.setDate(2, new Date(System.currentTimeMillis()));
            ps.setInt(3, action.getRunId());
            ps.setString(4, action.getPid());
            ps.setString(5,action.getContextVariables());
            
            int result = ps.executeUpdate();
            if(result != 1) {
                throw new CmException("Could not save solution context");
            }
            return new RpcData("status=OK");
        }
        finally {
            SqlUtilities.releaseResources(null,  ps, null);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveSolutionContextAction.class;
    }
}
