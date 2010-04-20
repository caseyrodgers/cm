package hotmath.gwt.cm_rpc.server.rpc;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.shared.server.service.ActionHandlerManualConnectionManagement;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Implements a Command Pattern for controlling commands used by Catchup Math
 * RPC calls.
 * 
 * Each command invocation gets its own instance of the command object. This
 * will make sure we do have any race conditions dealing with multiple threads.
 * 
 * @TODO: The next step, if needed, will be to add an object to provide pooling
 *        for each command.
 * 
 * 
 * @TODO: configuration should be injected by DI
 * 
 * 
 * @author casey
 * 
 */
public class ActionDispatcher {

    static private ActionDispatcher __instance;

    static public ActionDispatcher getInstance() {
        if (__instance == null)
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

    /**
     * remove commands
     * 
     * @TODO: should be closed.
     */
    static public void flush() {
        __instance = null;
    }

    private ActionDispatcher() {
        logger.info("Creating new ActionDispatcher");
    }

    /**
     * Register a new command with system.
     * 
     * An instance is created and the getActionType is called to return the
     * action name. This is stored as the key for the command. The value is a
     * Class object that represents the ActionListener implementation that does
     * all the server work.
     * 
     * This object is created dynamically on each invocation.
     * 
     * @param command
     */
    @SuppressWarnings("unchecked")
    public void addCommand(Class command) {
        try {
            Class action = ((ActionHandler) command.newInstance()).getActionType();
            commands.put(action, command);
        } catch (Exception e) {
            logger.error("Could not add command: " + command.getName(), e);
        }
    }

    public Map<Class<? extends Action<? extends Response>>, Class> getCommands() {
        return commands;
    }

    public void setCommands(Map<Class<? extends Action<? extends Response>>, Class> commands) {
        this.commands = commands;
    }

    /**
     * Execute the given command and return the proper Result object.
     * 
     * Each command is given a preallocated Connection option that it will
     * utilize for any DB access.
     * 
     * You can disable the automatic creation of the Connection by having your
     * Command object implement the ActionHandlerManualConnectionManagement
     * interface.
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
        String c[] = action.getClass().getName().split("\\.");
        String clazzName = c[c.length - 1];
        logger.info("RPC Action executing: " + clazzName + "  toString: " + action.toString());
        Connection conn = null;
        try {
            Class clazz = getActionCommand(action);
            if (clazz == null) {
                throw new CmRpcException("Could not find Action: " + action.getClass().getName());
            }
            ActionHandler actionHandler = (ActionHandler) clazz.newInstance();

            if (actionHandler == null)
                throw new CmRpcException("No such RPC Action defined: " + action.getClass().getName());

            /**
             * Should connection management be automatic (default)? If Command
             * object implements the ActionHandlerManualConnectionManagement
             * interface, then no connection is automatically created, it must
             * manually manage the creation/release of connections.
             */
            if (!(actionHandler instanceof ActionHandlerManualConnectionManagement)) {
                logger.debug("RPC Action: DB Connection requested");
                conn = HMConnectionPool.getConnection();
            } else {
                logger.debug("RPC Action: DB Connection NOT requested");
            }

            return (T) actionHandler.execute(conn, action);
        } catch (CmRpcException cre) {
            throw cre;
        } catch (Exception e) {
            throw new CmRpcException(e);
        } finally {
            if (conn != null)
                SqlUtilities.releaseResources(null, null, conn);
            logger.info("RPC Action " + clazzName + " toString: " + action.toString() + " complete: elapsed time: "
                    + (System.currentTimeMillis() - timeStart) / 1000);
        }
    }
    
    /** Check for comamnd, if not regiestered then look
     *  for Command following conventions:
     *  
     *  1. actions end with Action
     *  2. all commands live in package hotmath.gwt.shared.server.service.command;
     *  3. cmd names match action names, but end with Command instead of Action 
     *    (MyAction  == MyCommand)
     *  
     * @param action
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Class getActionCommand(Action<? extends Response> action) throws Exception {
        if(!commands.containsKey(action.getClass())) {
            logger.info("Auto registering action: " + action.getClass());
            String actionName = action.getClass().getName();
            if(actionName.endsWith("Action")) {
                /* extract name and construct command class name
                 * using standard package.
                 */
                String p[] = actionName.split("\\.");
                String cmdName = p[p.length-1];
                cmdName = cmdName.substring(0, cmdName.length() - 6);
                cmdName = "hotmath.gwt.shared.server.service.command." + cmdName + "Command";
                
                /** create instance and get object */
                logger.info("Auto registering action command: " + cmdName);
                Class cmdClass = Class.forName(cmdName);
                Class actionHandler = ((ActionHandler) cmdClass.newInstance()).getActionType();
                commands.put(actionHandler, cmdClass);
            }
        }
        return commands.get(action.getClass());
    }

    /**
     * Register commands
     * 
     * Each should be a class of ActionHandler
     * 
     * 
     * 
     */
    @SuppressWarnings("unchecked")
    public void registerCommands(Class[] commands) {
        logger.info("Adding " + commands.length + " commands");
        for (Class command : commands) {
            addCommand(command);
        }
    }
}
