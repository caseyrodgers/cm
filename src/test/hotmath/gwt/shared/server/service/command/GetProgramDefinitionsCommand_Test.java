package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.shared.client.rpc.action.GetProgramDefinitionsAction;

public class GetProgramDefinitionsCommand_Test extends CmDbTestCase {
	
	public GetProgramDefinitionsCommand_Test(String name) {
		super(name);
	}
	
	
	public void testGet() throws Exception {
		GetProgramDefinitionsAction action = new GetProgramDefinitionsAction(2);
		CmList<StudyProgramModel> spm = new GetProgramDefinitionsCommand().execute(conn, action);
		assertTrue(spm.size() > 0);
	}
}
