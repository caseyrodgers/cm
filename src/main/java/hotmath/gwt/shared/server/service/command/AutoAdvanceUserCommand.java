package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.AutoUserAdvanced;
import hotmath.gwt.shared.client.rpc.action.AutoAdvanceUserAction;
import hotmath.gwt.shared.server.service.ActionHandlerManualConnectionManagement;
import hotmath.testset.ha.EndOfProgramHandler;
import hotmath.testset.ha.StudentUserProgramModel;

import java.sql.Connection;

import org.apache.log4j.Logger;


/** Auto advance this user to the next logical program
 * 
 * @author casey
 *
 */
public class AutoAdvanceUserCommand implements ActionHandlerManualConnectionManagement, ActionHandler<AutoAdvanceUserAction, AutoUserAdvanced> {

    Logger logger = Logger.getLogger(AutoAdvanceUserCommand.class);

    @Override
    public AutoUserAdvanced execute(final Connection conn, AutoAdvanceUserAction action) throws Exception {
        
        int userId = action.getUserId();
        EndOfProgramHandler eofh = new EndOfProgramHandler(userId);
        StudentUserProgramModel program = eofh.getNextProgram();
        
        String chapter = program.getConfig().getChapters().size() > 0?program.getConfig().getChapters().get(0):null;
        AutoUserAdvanced advance = new AutoUserAdvanced(program.getTestName(), chapter);
        return advance;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return AutoAdvanceUserAction.class;
    }
}
