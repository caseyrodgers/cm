package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.rpc.action.GetSubjectDefinitionsAction;
import hotmath.testset.ha.CmProgram;

public class GetSubjectDefinitionsCommand_Test extends CmDbTestCase {
	
	public GetSubjectDefinitionsCommand_Test(String name) {
		super(name);
	}
	
	
	public void testGet() throws Exception {
		GetSubjectDefinitionsAction action = new GetSubjectDefinitionsAction(CmProgram.ALG1_CHAP.getProgramType());
		CmList<SubjectModel> spm = new GetSubjectDefinitionsCommand().execute(conn, action);
		assertTrue(spm.size() > 0);
	}

	public void testDispatch() throws Exception {
		GetSubjectDefinitionsAction action = new GetSubjectDefinitionsAction(CmProgram.ALG1_CHAP.getProgramType());
		CmList<SubjectModel> spm = ActionDispatcher.getInstance().execute(action);
		assertTrue(spm.size() > 0);
	}
}
