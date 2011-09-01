package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.AutoAdvanceUserAction;
import hotmath.gwt.cm_rpc.client.rpc.AutoUserAdvanced;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.testset.ha.EndOfProgramHandler;
import hotmath.testset.ha.StudentUserProgramModel;

import java.sql.Connection;

import org.apache.log4j.Logger;


/** Auto advance this user to the next logical program
 * 
 * @author casey
 *
 */
public class AutoAdvanceUserCommand implements ActionHandler<AutoAdvanceUserAction, AutoUserAdvanced> {

    Logger logger = Logger.getLogger(AutoAdvanceUserCommand.class);

    @Override
    public AutoUserAdvanced execute(final Connection conn, AutoAdvanceUserAction action) throws Exception {
        
        EndOfProgramHandler eofh = new EndOfProgramHandler();
        eofh.loadStudent(conn, action.getUserId());
        StudentUserProgramModel program = eofh.getNextProgram(conn);
        
        String chapter = program.getConfig().getChapters().size() > 0?program.getConfig().getChapters().get(0):null;
        AutoUserAdvanced advance = new AutoUserAdvanced(program.getTestName(), chapter);
        return advance;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return AutoAdvanceUserAction.class;
    }
}
