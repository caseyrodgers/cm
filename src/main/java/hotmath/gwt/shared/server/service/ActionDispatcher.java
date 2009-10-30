package hotmath.gwt.shared.server.service;


import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.gwt.cm_tools.server.service.SetInmhItemAsViewedCommand;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.server.service.command.AddGroupCommand;
import hotmath.gwt.shared.server.service.command.AddStudentCommand;
import hotmath.gwt.shared.server.service.command.AutoAdvanceUserCommand;
import hotmath.gwt.shared.server.service.command.CheckUserAccountStatusCommand;
import hotmath.gwt.shared.server.service.command.ClearWhiteboardDataCommand;
import hotmath.gwt.shared.server.service.command.CreateAutoRegistrationAccountCommand;
import hotmath.gwt.shared.server.service.command.CreateAutoRegistrationAccountsCommand;
import hotmath.gwt.shared.server.service.command.CreateAutoRegistrationPreviewCommand;
import hotmath.gwt.shared.server.service.command.CreateTestRunCommand;
import hotmath.gwt.shared.server.service.command.GeneratePdfCommand;
import hotmath.gwt.shared.server.service.command.GetGroupAggregateInfoCommand;
import hotmath.gwt.shared.server.service.command.GetLessonItemsForTestRunCommand;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.gwt.shared.server.service.command.GetProgramDefinitionsCommand;
import hotmath.gwt.shared.server.service.command.GetQuizHtmlCheckedCommand;
import hotmath.gwt.shared.server.service.command.GetQuizHtmlCommand;
import hotmath.gwt.shared.server.service.command.GetQuizResultsHtmlCommand;
import hotmath.gwt.shared.server.service.command.GetReportDefCommand;
import hotmath.gwt.shared.server.service.command.GetReviewHtmlCommand;
import hotmath.gwt.shared.server.service.command.GetSolutionCommand;
import hotmath.gwt.shared.server.service.command.GetStateStandardsCommand;
import hotmath.gwt.shared.server.service.command.GetSummariesForActiveStudentsCommand;
import hotmath.gwt.shared.server.service.command.GetUserInfoCommand;
import hotmath.gwt.shared.server.service.command.GetViewedInmhItemsCommand;
import hotmath.gwt.shared.server.service.command.GroupManagerCommand;
import hotmath.gwt.shared.server.service.command.LogUserInCommand;
import hotmath.gwt.shared.server.service.command.MarkPrescriptionLessonAsViewedCommand;
import hotmath.gwt.shared.server.service.command.ProcessLoginRequestCommand;
import hotmath.gwt.shared.server.service.command.SaveAutoRegistrationCommand;
import hotmath.gwt.shared.server.service.command.SaveFeedbackCommand;
import hotmath.gwt.shared.server.service.command.SaveQuizCurrentResultCommand;
import hotmath.gwt.shared.server.service.command.SaveWhiteboardDataCommand;
import hotmath.gwt.shared.server.service.command.UnregisterStudentsCommand;
import hotmath.gwt.shared.server.service.command.UpdateStudentCommand;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/** Implements a Command Pattern for controlling
 *  commands used by Catchup Math RPC calls.
 *  
 *  Each command invocation gets its own instance of 
 *  the command object.  This will make sure we do
 *  have any race conditions dealing with multiple 
 *  threads.
 *  
 *  @TODO: The next step, if needed, will be to add an
 *  object to provide pooling for each command.
 *  
 *  
 *  @TODO: configuration should be injected by DI
 *  
 *  
 * @author casey
 *
 */
public class ActionDispatcher {

    static private ActionDispatcher __instance;
    static public ActionDispatcher getInstance() {
        if(__instance == null)
            __instance = new ActionDispatcher();
        return __instance;
    }
    
    static {
        HotmathFlusher.getInstance().addFlushable(new Flushable() {    
            public void flush() {
                ActionDispatcher.flush();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    Map<Class<? extends Action<? extends Response>>, Class> commands = new HashMap<Class<? extends Action<? extends Response>>, Class>();
    
    static Logger logger = Logger.getLogger(ActionDispatcher.class.getName());
    
    
    /** remove commands
     * @TODO: should be closed.
     */
    static public void flush() {
        __instance = null;
    }
    
    private ActionDispatcher() {
        logger.info("Creating new ActionDispatcher");
        
        /** All commands should be injected, hard coded for now
         * 
         * Register each command available to RPC
         */
        addCommand(GetPrescriptionCommand.class);
        addCommand(GetViewedInmhItemsCommand.class);
        addCommand(GetSolutionCommand.class);
        addCommand(SetInmhItemAsViewedCommand.class);
        addCommand(GetUserInfoCommand.class);
        addCommand(CreateTestRunCommand.class);
        addCommand(GetQuizHtmlCommand.class);
        addCommand(GetQuizHtmlCheckedCommand.class);
        addCommand(SaveQuizCurrentResultCommand.class);
        addCommand(GetQuizResultsHtmlCommand.class);
        addCommand(SaveFeedbackCommand.class);
        addCommand(AutoAdvanceUserCommand.class);
        addCommand(GetProgramDefinitionsCommand.class);
        addCommand(AddStudentCommand.class);
        addCommand(UpdateStudentCommand.class);
        addCommand(SaveWhiteboardDataCommand.class);
        addCommand(ClearWhiteboardDataCommand.class);
        addCommand(CreateAutoRegistrationPreviewCommand.class);
        addCommand(CreateAutoRegistrationAccountsCommand.class);
        addCommand(AddGroupCommand.class);
        addCommand(SaveAutoRegistrationCommand.class);
        addCommand(GetReportDefCommand.class);
        addCommand(CreateAutoRegistrationAccountCommand.class);
        addCommand(GetStateStandardsCommand.class);
        addCommand(GetLessonItemsForTestRunCommand.class);
        addCommand(UnregisterStudentsCommand.class);
        addCommand(ProcessLoginRequestCommand.class);
        addCommand(GetSummariesForActiveStudentsCommand.class);
        addCommand(GetReviewHtmlCommand.class);
        addCommand(MarkPrescriptionLessonAsViewedCommand.class);
        addCommand(CheckUserAccountStatusCommand.class);
        addCommand(LogUserInCommand.class);
        addCommand(GetGroupAggregateInfoCommand.class);
        addCommand(GroupManagerCommand.class);
        addCommand(GeneratePdfCommand.class);
        
    }
    
    
    /** Register a new command with system.
     * 
     *  An instance if created and the getActionType is called
     *  to return the action name.  This is stored as the key for
     *  the command.  The value is a Class object that represents
     *  the ActionListener implementation that does all the server work.
     *  This object is created dynamically on each invocation.
     *  
     * @param command
     */
    @SuppressWarnings("unchecked")
    private void addCommand(Class command) {
        try {
            Class action = ((ActionHandler)command.newInstance()).getActionType();
            commands.put(action, command);
        }
        catch(Exception e) {
            logger.error("Could not add command: " + command.getName(), e); 
        }
    }
    
    
    /** Execute the given command and return the proper Result object.
     * 
     * Each command is given a preallocated Connection option that it
     * will utilize for any DB access.  
     * 
     * 
     * @param <T>
     * @param action
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T extends Response> T execute(Action<T> action) throws CmRpcException {
 
        long timeStart = System.currentTimeMillis();
        logger.debug("RPC Action executing: " + action.getClass().getName() + "  toString: " + action.toString());
        Connection conn = null;
        try {
            Class clazz = commands.get(action.getClass());
            if(clazz == null)
                throw new CmRpcException("Could not find Action: " + action.getClass().getName());
            
            ActionHandler actionHandler = (ActionHandler)clazz.newInstance();
            
            if(actionHandler == null)
                throw new CmRpcException("No such RPC Action defined: " + action.getClass().getName());

            
            /** Should connection management be automatic (default)? 
             * If Command object implements the ActionHandlerManualConnectionManagement
             * interface, then no connection is automatically created, it must
             * manually manage the creation/release of connections.
             */
            if(!(actionHandler instanceof ActionHandlerManualConnectionManagement)) {
                logger.debug("RPC Action: DB Connection requested");
                conn = HMConnectionPool.getConnection();
            }
            else {
                logger.debug("RPC Action: DB Connection NOT requested");
            }

            return (T)actionHandler.execute(conn, action);
        }
        catch(CmRpcException cre) {
            throw cre;
        }
        catch(Exception e) {
            throw new CmRpcException(e);
        }
        finally {
            if(conn != null)
                SqlUtilities.releaseResources(null,null,conn);
            logger.debug("RPC Action " + action.getClass().getName() + " complete: elapsed time: " + (System.currentTimeMillis() - timeStart)/ 1000);
        }
    }
}
