package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.ResetUserPrescripionLessonAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;

public class ResetUserPrescripionLessonCommand_Test extends TestCase {
    
    int UID=28221;
    int RID=1238086;
    String PID="test_dynamic_dist-prop_basic_1_1$1";
    
    public ResetUserPrescripionLessonCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        ResetUserPrescripionLessonAction action = new ResetUserPrescripionLessonAction(UID, PID);
        RpcData result = new ResetUserPrescripionLessonCommand().execute(HMConnectionPool.getConnection(), action);
        assertTrue(result.getDataAsString("status").equals("OK"));
    }
}
