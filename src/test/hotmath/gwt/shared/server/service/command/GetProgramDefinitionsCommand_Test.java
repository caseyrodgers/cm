package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetProgramDefinitionsAction;
import hotmath.gwt.shared.server.service.ActionDispatcher;
import hotmath.testset.ha.CmProgram;

public class GetProgramDefinitionsCommand_Test extends CmDbTestCase {
	
	public GetProgramDefinitionsCommand_Test(String name) {
		super(name);
	}
	
	
	public void testGet() throws Exception {
		GetProgramDefinitionsAction action = new GetProgramDefinitionsAction(CmProgram.ALG1_CHAP.getProgramId());
		CmList<StudyProgramModel> spm = new GetProgramDefinitionsCommand().execute(conn, action);
		assertTrue(spm.size() > 0);
	}

	public void testDispatch() throws Exception {
		GetProgramDefinitionsAction action = new GetProgramDefinitionsAction(CmProgram.ALG1_CHAP.getProgramId());
		CmList<StudyProgramModel> spm = ActionDispatcher.getInstance().execute(action);
		assertTrue(spm.size() > 0);
	}
}
