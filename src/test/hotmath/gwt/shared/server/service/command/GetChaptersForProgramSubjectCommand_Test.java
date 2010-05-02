package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.shared.client.rpc.action.GetChaptersForProgramSubjectAction;
import hotmath.testset.ha.CmProgram;

public class GetChaptersForProgramSubjectCommand_Test extends CmDbTestCase {
	
	public GetChaptersForProgramSubjectCommand_Test(String name) {
		super(name);
	}
	
	
	public void testGet() throws Exception {
		GetChaptersForProgramSubjectAction action = new GetChaptersForProgramSubjectAction(CmProgram.ALG1_CHAP.getProgramType(), CmProgram.ALG2_CHAP.getSubject());
		CmList<ChapterModel> spm = new GetChaptersForProgramSubjectCommand().execute(conn, action);
		assertTrue(spm.size() > 0);
	}

	public void testDispatch() throws Exception {
		GetChaptersForProgramSubjectAction action = new GetChaptersForProgramSubjectAction(CmProgram.ALG1_CHAP.getProgramType(), CmProgram.ALG2_CHAP.getSubject());
		CmList<ChapterModel> spm = ActionDispatcher.getInstance().execute(action);
		assertTrue(spm.size() > 0);
	}
}
