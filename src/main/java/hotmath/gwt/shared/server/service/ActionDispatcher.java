package hotmath.gwt.shared.server.service;


import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.gwt.cm_tools.server.service.SetInmhItemAsViewedCommand;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.server.service.command.CreateTestRunCommand;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.gwt.shared.server.service.command.GetQuizHtmlCommand;
import hotmath.gwt.shared.server.service.command.GetSolutionCommand;
import hotmath.gwt.shared.server.service.command.GetUserInfoCommand;
import hotmath.gwt.shared.server.service.command.GetViewedInmhItemsCommand;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/** Implements a Command Pattern for controlling
 *  commands used by Catchup Math RPC calls.
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
    Map<Class<? extends Action<? extends Response>>, ActionHandler> commands = new HashMap<Class<? extends Action<? extends Response>>, ActionHandler>();
    
    static Logger logger = Logger.getLogger(ActionDispatcher.class.getName());
    
    
    /** remove comamnds
     * @TODO: should be closed.
     */
    static public void flush() {
        __instance = null;
    }
    
    private ActionDispatcher() {
        
        
        logger.info("Creating new ActionDispatcher");
        
        /** All commands should be injected, hard coded for now
         * 
         * Register each command availiable to RPC
         */
        addCommand(new GetPrescriptionCommand());
        addCommand(new GetViewedInmhItemsCommand());
        addCommand(new GetSolutionCommand());
        addCommand(new SetInmhItemAsViewedCommand());
        addCommand(new GetUserInfoCommand());
        addCommand(new CreateTestRunCommand());
        addCommand(new GetQuizHtmlCommand());
    }
    
    
    @SuppressWarnings("unchecked")
    private void addCommand(ActionHandler action) {
        commands.put(action.getActionType(), action);
    }
    
    
    /** Execute the given command and return the proper Result object
     * 
     * @param <T>
     * @param action
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T extends Response> T execute(Action<T> action) throws CmRpcException {
        
        logger.debug("RPC Action executing: " + action.getClass().getName());
        
        try {
            ActionHandler actionHandler = commands.get(action.getClass());
            if(actionHandler == null)
                throw new CmRpcException("No such RPC Action defined: " + action.getClass().getName());
            
            return (T)actionHandler.execute(action);
        }
        catch(CmRpcException cre) {
            throw cre;
        }
        catch(Exception e) {
            throw new CmRpcException(e);
        }
    }
}
