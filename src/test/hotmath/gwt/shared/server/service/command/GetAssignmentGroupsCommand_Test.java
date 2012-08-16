package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGroupsAction;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;

public class GetAssignmentGroupsCommand_Test extends TestCase {
    
    public GetAssignmentGroupsCommand_Test(String name) {
        super(name);
    }
    
    public void testId() throws Exception {
        
        GetAssignmentGroupsAction action = new GetAssignmentGroupsAction(2);
        CmList<GroupDto> groups = new GetAssignmentGroupsCommand().execute(HMConnectionPool.getConnection(), action);
        assertTrue(groups != null);
    }

}
