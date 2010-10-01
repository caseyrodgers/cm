package hotmath.gwt.cm_mobile.server.rpc;

import hotmath.gwt.cm_mobile.client.rpc.CreateTestRunMobileAction;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.CreateTestRunAction;
import hotmath.gwt.shared.server.service.command.CreateTestRunCommand;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.gwt.shared.server.service.command.MultiActionRequestCommand;

import java.sql.Connection;

public class CreateTestRunMobileCommand implements ActionHandler<CreateTestRunMobileAction, PrescriptionSessionResponse>{
    
    public CreateTestRunMobileCommand() {}

    @Override
    public PrescriptionSessionResponse execute(Connection conn, CreateTestRunMobileAction action) throws Exception {
        
        /** set the user's selection
         * 
         */
        new MultiActionRequestCommand().execute(conn, action.getSelections());
        
        
        /** check the test/create test run
         * 
         */
        CreateTestRunAction actionTestRun = new CreateTestRunAction(action.getUser().getTestId(), action.getUser().getUserId());
        CreateTestRunResponse testRunResult = new CreateTestRunCommand().execute(conn, actionTestRun);

        
        /** get the first lesson from the prescription
         * 
         */
        GetPrescriptionAction actionGetPres = new GetPrescriptionAction(testRunResult.getRunId(), 0, true);
        PrescriptionSessionResponse response = new GetPrescriptionCommand().execute(conn,actionGetPres);
        
        return response;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateTestRunMobileAction.class;
    }

}
