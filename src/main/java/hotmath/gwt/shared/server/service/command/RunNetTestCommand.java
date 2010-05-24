package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestApplication;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.NetTestModel;
import hotmath.util.sql.SqlUtilities;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;



public class RunNetTestCommand implements ActionHandler<RunNetTestAction, NetTestModel>{

	private static Logger logger = Logger.getLogger(RunNetTestCommand.class);

    @Override
    public NetTestModel execute(Connection conn, RunNetTestAction action) throws Exception {
        
        String BLOCK_STRING="1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if(action.getAction() == TestAction.RUN_TEST) {
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<action.getDataSize();i++) {
                sb.append(BLOCK_STRING);
            }
            return new NetTestModel("test " + action.getTestNum(), sb.toString());
        }
        else if(action.getAction() == TestAction.SAVE_RESULTS) {
            
            PreparedStatement pstat=null;
            try {
                String sql = null;
                if(action.getTestApp() == TestApplication.CM_ADMIN) {
                    sql = "insert into HA_USER_NET_TEST(aid, test_num, test_size, test_time,run_date)values(?,?,?,?,now())";
                }
                else {
                    sql = "insert into HA_USER_NET_TEST(uid, test_num, test_size, test_time,run_date)values(?,?,?,?,now())";
                }
                
                try {
                pstat = conn.prepareStatement(sql);
                
                for(NetTestModel test: action.getTestResults()) {
                    pstat.setInt(1, action.getUid());
                    pstat.setInt(2,test.getNumber());
                    pstat.setLong(3,test.getSize());
                    pstat.setLong(4,test.getTime());
                    
                    pstat.executeUpdate();
                }
                }
                catch(Exception e) {
                	logger.error(String.format("*** Error executing Action: %s", action.toString()), e);
                }
            }
            finally {
                SqlUtilities.releaseResources(null, pstat,null);
            }
            
            
            return new NetTestModel();
            
        }
        else {
            throw new CmException("Unknown TestRequest: " + action.getAction());
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return RunNetTestAction.class;
    }

}
