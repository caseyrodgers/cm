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
    
    int maxRetries = 5;
    int retryDelayMsec = 1000;
    
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
    public <T extends Response> T execute(Action<T> action) throws CmRpcException {

        T response = null;
        for (int retryCount=0; retryCount<maxRetries; retryCount++) {
            try {
                response = executeAction(action);
                break;
            }
            catch (Exception e) {
                
                monitorCountActionsRetried++;

                String c[] = action.getClass().getName().split("\\.");
                String clazzName = c[c.length - 1];
                logger.info(String.format("RPC Action: %s failed, count: %d ", clazzName, retryCount+1));

                if ((retryCount+1) < maxRetries) {
                    try {
                        Thread.sleep(retryDelayMsec);
                    }
                    catch (InterruptedException ie) {logger.error(ie);}
                }
                else {
                    monitorCountActionsFailed++;
                    throw new CmRpcException(String.format("ACTION FAILURE, Could not execute after %d attempts, action: %s", maxRetries, action), e);
                }
            }
        }
        /*
        if(response == null) {
        	// TODO: will this code actually be visited?
            monitorCountActionsFailed++;
            throw new CmRpcException(String.format("ACTION FAILURE, NULL response after %d attempts, action: %s", maxRetries, action));
        }
        */
        return response;
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

    @SuppressWarnings("unchecked")
	public Map<Class<? extends Action<? extends Response>>, Class> getCommands() {
        return commands;
    }

    @SuppressWarnings("unchecked")
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
    private <T extends Response> T executeAction(Action<T> action) throws Exception {

        long timeStart = System.currentTimeMillis();
        String c[] = action.getClass().getName().split("\\.");
        String clazzName = c[c.length - 1];
        logger.info("RPC Action executing: " + clazzName + "  toString: " + action.toString());
        Connection conn = null;
        try {
            Class clazz = getActionCommand(action);
            if (clazz == null) {
                throw new CmRpcException("Could not find Command for Action: " + action.getClass().getName());
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

            monitorCountActionsExecuted++;

            T response = (T) actionHandler.execute(conn, action);

            monitorCountActionsCompleted++;

            return response;
        }
        finally {
            if (conn != null)
                SqlUtilities.releaseResources(null, null, conn);
            long now = System.currentTimeMillis();
            long executeTimeMills = (now - timeStart);
            long executeTime = executeTimeMills / 1000;
            logger.info("RPC Action " + clazzName + " toString: " + action.toString() + " complete: elapsed time: " + executeTime);

            monitorTotalProcessingTime += executeTimeMills;
        }
    }

    /** Check for command, if not registered then look
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
                
                /** TODO: how to add these automatically ?
                 * 
                 * NOTE: each ends with period
                 */
                String standardPlaces[] = {"hotmath.gwt.shared.server.service.command.",
                                           "hotmath.gwt.cm_mobile.server.rpc.",
                                           "hotmath.gwt.solution_manager.server.rpc."};
                                     
                Class actionHandler=null,cmdClass=null;
                for(int i=0;i<standardPlaces.length;i++) {
                    String commandClass = standardPlaces[i] + cmdName + "Command";
                    try {
                        /** create instance and get object */
                        logger.info("Auto registering action command: " + cmdName);
                        cmdClass = Class.forName(commandClass);
                        actionHandler = ((ActionHandler) cmdClass.newInstance()).getActionType();
                        break;
                    }
                    catch(ClassNotFoundException ie) {
                        /** silent */
                    }
                }
                if(actionHandler == null)
                    throw new CmRpcException("No command found action: " + action);
                
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

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public int getRetryDelayMsec() {
		return retryDelayMsec;
	}

	public void setRetryDelayMsec(int retryDelayMsec) {
		this.retryDelayMsec = retryDelayMsec;
	}
	
	
	/** Used by CmMonitor get get inidividual stats
	 *  from running ActionDispatcher.
	 *  
	 * @param type
	 * @return
	 */
	public long getMonitoredData(MonitorData type) {
	    switch(type) {
	    case ActionsExecuted:
	        return monitorCountActionsExecuted;
	        
	    
	    case ActionsCompleted:
	        return monitorCountActionsCompleted;
	        
	        
	    case ActionsRetry:
	        return monitorCountActionsRetried;
	        
	    case ActionsFailed:
	        return monitorCountActionsFailed;
	        
	    case ProcessingTime:
	        return monitorTotalProcessingTime;
	        
	        default:
	            return -1;
	       
	    }
	}
	public static enum MonitorData {
	    ActionsExecuted,
	    ActionsCompleted,
	    ActionsRetry,
	    ActionsFailed,
	    ProcessingTime
	}
	
    
    /** for zabbix logging see CmMonitor */
    int monitorCountActionsExecuted; 
    int monitorCountActionsRetried;
    int monitorCountActionsFailed;
    int monitorCountActionsCompleted;
    long monitorTotalProcessingTime;
}


