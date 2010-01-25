package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlAction;
import hotmath.gwt.shared.client.rpc.result.QuizHtmlResult;
import hotmath.gwt.shared.server.service.ActionDispatcher;
import hotmath.testset.ha.CmProgram;

public class GetQuizHtmlCommand_Test extends CmDbTestCase {
	
	public GetQuizHtmlCommand_Test(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		if(_user == null)
			setupDemoAccount();
	}
	
	
    public void testGetQuizHtmChapterTitleNumber() throws Exception {
        CmStudentDao dao = new CmStudentDao();
        dao.assignProgramToStudent(conn,_user.getUid(),CmProgram.PREALG_CHAP,"Integers");
        GetQuizHtmlAction  action = new GetQuizHtmlAction(_user.getUid(), 1);
        QuizHtmlResult result = ActionDispatcher.getInstance().execute(action);
        
        String chapter = result.getSubTitle();
        assertTrue(chapter.indexOf("Integers") > -1);
    }

}
