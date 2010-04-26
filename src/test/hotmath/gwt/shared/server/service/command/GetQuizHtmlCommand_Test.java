package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
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
		
		CmWebResourceManager.setFileBase(CatchupMathProperties.getInstance().getProperty("web.base.path","/dev/local/gwt2/cm/src/main/webapp/cm_temp"));
	}
	
    public void testAlwaysTheSame() throws Exception {
        GetQuizHtmlAction  action = new GetQuizHtmlAction(_user.getUid(),0, 1);
        QuizHtmlResult result = new GetQuizHtmlCommand().execute(conn, action);
        String q1 = result.getQuizHtml();
        assertNotNull(q1);
    }
    
	public void testCreate() throws Exception {
        GetQuizHtmlAction  action = new GetQuizHtmlAction(_user.getUid(),0, 1);
        QuizHtmlResult result = new GetQuizHtmlCommand().execute(conn, action);
        assertTrue(result.getAnswers().get(1) > -1);
	}
	
    public void testGetQuizHtmChapterTitleNumber() throws Exception {
        CmStudentDao dao = new CmStudentDao();
        dao.assignProgramToStudent(conn,_user.getUid(),CmProgram.PREALG_CHAP,"Integers");
        GetQuizHtmlAction  action = new GetQuizHtmlAction(_user.getUid(),0, 1);
        QuizHtmlResult result = ActionDispatcher.getInstance().execute(action);
        String chapter = result.getSubTitle();
        assertTrue(chapter.indexOf("Integers") > -1);
    }
    
    

}
