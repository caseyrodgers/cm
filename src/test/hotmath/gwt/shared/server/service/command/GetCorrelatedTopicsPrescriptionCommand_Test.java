package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.GetCorrelatedTopicsPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

import junit.framework.TestCase;

public class GetCorrelatedTopicsPrescriptionCommand_Test extends TestCase {
	
	private static final String _PID = 	"cmextras_2_7-addlike_1_5_1";

	public GetCorrelatedTopicsPrescriptionCommand_Test(String name) {
		super(name);
	}
	
	public void testIt() throws Exception {
		Connection conn=null;
		try {
			conn = HMConnectionPool.getConnection();
			GetCorrelatedTopicsPrescriptionAction action = new GetCorrelatedTopicsPrescriptionAction(_PID);
			CmList<PrescriptionSessionResponse> values = new GetCorrelatedTopicsPrescriptionCommand().execute(conn, action);
			assertTrue(values != null);
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}

}
