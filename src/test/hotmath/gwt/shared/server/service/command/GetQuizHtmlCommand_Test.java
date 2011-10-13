package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;

public class GetQuizHtmlCommand_Test extends CmDbTestCase {
	
	public GetQuizHtmlCommand_Test(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		if(_test== null)
			setupDemoAccountTest();
		
		CmWebResourceManager.setFileBase(CatchupMathProperties.getInstance().getProperty("web.base.path","/dev/work/cm/src/main/webapp/cm_temp"));
	}
	
    public void testAlwaysTheSame() throws Exception {
        GetQuizHtmlAction  action = new GetQuizHtmlAction(_test.getTestId());
        QuizHtmlResult response = new GetQuizHtmlCommand().execute(conn, action);
        String q1 = response.getQuizHtml();
        assertNotNull(q1);
    }
}
