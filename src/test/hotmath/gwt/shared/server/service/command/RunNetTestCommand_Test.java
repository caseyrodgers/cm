package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestApplication;
import hotmath.gwt.shared.client.util.NetTestModel;
import hotmath.gwt.shared.server.service.ActionDispatcher;

import java.util.ArrayList;
import java.util.List;

public class RunNetTestCommand_Test extends CmDbTestCase {
    
    public RunNetTestCommand_Test(String name) {
        super(name);
    }

    public void testRunSaveResultsAdmin() throws Exception {
        List<NetTestModel> models = new ArrayList<NetTestModel>();
        models.add(new NetTestModel("test1", "result 1"));
        models.add(new NetTestModel("test2", "result 2"));
        RunNetTestAction action = new RunNetTestAction(TestApplication.CM_ADMIN,TestAction.SAVE_RESULTS,1,models);
        NetTestModel testResult = ActionDispatcher.getInstance().execute(action);
    } 
    
    public void testRunSaveResultsStudent() throws Exception {
        List<NetTestModel> models = new ArrayList<NetTestModel>();
        models.add(new NetTestModel("test1", "result 1"));
        models.add(new NetTestModel("test2", "result 2"));
        RunNetTestAction action = new RunNetTestAction(TestApplication.CM_STUDENT,TestAction.SAVE_RESULTS,1,models);
        NetTestModel testResult = ActionDispatcher.getInstance().execute(action);
    } 
    
    public void testRunTests() throws Exception {
        RunNetTestAction action = new RunNetTestAction(TestApplication.CM_STUDENT,TestAction.RUN_TEST,1,1,100);
        NetTestModel testResult = new RunNetTestCommand().execute(conn, action);
        assertNotNull(testResult);
    }
    
    public void testRunTestsDispatch() throws Exception {
        RunNetTestAction action = new RunNetTestAction(TestApplication.CM_STUDENT,TestAction.RUN_TEST,1,1,100);
        NetTestModel testResult = ActionDispatcher.getInstance().execute(action);
        assertNotNull(testResult);
    }

}
