package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetSummariesForActiveStudentsAction;

public class GetSummariesForActiveStudentsCommand_Test extends CmDbTestCase {
    
    public GetSummariesForActiveStudentsCommand_Test(String name) {
        super(name);
    }
    
    public void testGetSummaries() throws Exception {
        CmList<StudentModelI> students = new GetSummariesForActiveStudentsCommand().execute(conn, new GetSummariesForActiveStudentsAction(126));
        assertTrue(students.size() > 0);
    }
}
