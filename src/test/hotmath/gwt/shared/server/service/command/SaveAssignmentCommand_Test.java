package hotmath.gwt.shared.server.service.command;


import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentAction;
import hotmath.util.HMConnectionPool;

import java.util.Date;

import junit.framework.TestCase;

public class SaveAssignmentCommand_Test extends TestCase {
    
    public SaveAssignmentCommand_Test(String name) {
        super(name);
    }
    
    public void testCreateNew() throws Exception {
        
        Assignment ass = new Assignment();
        ass.setAssignmentName("Test 1: " + System.currentTimeMillis());
        ass.setAssignmentName("New Test Assignment");
        ass.setGroupId(0);
        ass.setDueDate(new Date());
        CmList<ProblemDto> pids = new CmArrayList<ProblemDto>();
        pids.add(new ProblemDto(0,"Test Lesson", "Label", "pid", null, 0));
        ass.setPids(pids);
        
        SaveAssignmentAction action = new SaveAssignmentAction(0, ass);
        RpcData data = new SaveAssignmentCommand().execute(HMConnectionPool.getConnection(), action);
        assertNotNull(data);
    }

}
