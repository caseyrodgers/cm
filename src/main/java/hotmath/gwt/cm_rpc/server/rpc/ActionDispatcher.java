package hotmath.gwt.cm_rpc.server.rpc;

import hotmath.cm.util.ActionTypeMap;
import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcherListener.ActionExecutionType;
import hotmath.gwt.shared.server.service.ActionHandlerManualConnectionManagement;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    List<ActionDispatcherListener> listeners = new ArrayList<ActionDispatcherListener>();

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
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addCommand(Class command) {
        try {
            Class action = ((ActionHandler) command.newInstance()).getActionType();
            commands.put(action, command);
        } catch (Exception e) {
            logger.error("Could not add command: " + command.getName(), e);
        }
    }

    @SuppressWarnings("rawtypes")
    public Map<Class<? extends Action<? extends Response>>, Class> getCommands() {
        return commands;
    }

    @SuppressWarnings("rawtypes")
	public void setCommands(Map<Class<? extends Action<? extends Response>>, Class> commands) {
        this.commands = commands;
    }


    /** Add listener ability to ActionDispatcher.  Each event executed
     *  will fire an event to any interested parties.
     *  
     * @param listener
     */
    public void addActionDispatchListener(ActionDispatcherListener listener) {
    	listeners.add(listener);
    }
    public void removeActionDispatcherListener(ActionDispatcherListener listener) {
    	if(listeners.contains(listener)) {
    		listeners.remove(listener);
    	}
    }
    public void removeActionDispatcherListenerAll() {
    	listeners.clear();
    }
    private void fireActionDispatcherEvent(ActionExecutionType type, Action<? extends Response> action) {
    	for(ActionDispatcherListener listener: listeners) {
    		listener.actionExecuted(type, action);
    	}
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T extends Response> T execute(Action<T> action) throws CmRpcException {

        long timeStart = System.currentTimeMillis();
        String c[] = action.getClass().getName().split("\\.");
        String clazzName = c[c.length - 1];
        logger.info("RPC Action executing: " + clazzName + "  toString: " + action.toString());
        Connection conn = null;
        ActionTypeMap.ActionType actionType = null;
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

            actionType = ActionTypeMap.getActionType(clazzName);
            
            /** todo: move to listener
             * 
             */
            incrementActionsExecuted(actionType);

            fireActionDispatcherEvent(ActionExecutionType.BEFORE, action);
            
            T response = (T) actionHandler.execute(conn, action);
            
            fireActionDispatcherEvent(ActionExecutionType.AFTER, action);

            incrementActionsCompleted(actionType);

            return response;
        } catch (CmRpcException cre) {
            monitorCountOfExceptions++;
            throw cre;
        } catch (Exception e) {
            monitorCountOfExceptions++;
            throw new CmRpcException(e);
        } finally {
            if (conn != null)
                SqlUtilities.releaseResources(null, null, conn);

            long now = System.currentTimeMillis();
            long executeTimeMills = (now - timeStart);
            logger.info("RPC Action " + clazzName + " toString: " + action.toString() + " complete: elapsed time: "
                    + executeTimeMills / 1000);
            
            incrementProcessingTime(actionType, executeTimeMills);
            
        }
    }

	private void incrementActionsExecuted(ActionTypeMap.ActionType actionType) {
		if (actionType == ActionTypeMap.ActionType.ADMIN) {
			monitorCountAdminActionsExecuted++;
		}
		if (actionType == ActionTypeMap.ActionType.STUDENT) {
			monitorCountStudentActionsExecuted++;
		}
		if (actionType == ActionTypeMap.ActionType.ANY) {
			monitorCountAnyActionsExecuted++;
		}

		monitorCountActionsExecuted++;
		
		logger.debug(String.format("+++ incrementActionsExecuted(): admin: %d, student: %d, all: %d",
			monitorCountAdminActionsExecuted, monitorCountStudentActionsExecuted, monitorCountActionsExecuted));
	}

	private void incrementProcessingTime(ActionTypeMap.ActionType actionType, long executeTimeMillis) {
		if (actionType == ActionTypeMap.ActionType.ADMIN) {
			monitorAdminProcessingTime += executeTimeMillis;
		}
		if (actionType == ActionTypeMap.ActionType.STUDENT) {
			monitorStudentProcessingTime += executeTimeMillis;
		}
		if (actionType == ActionTypeMap.ActionType.ANY) {
			monitorAnyProcessingTime += executeTimeMillis;
		}

        monitorTotalProcessingTime += executeTimeMillis; 
	}


	private void incrementActionsCompleted(ActionTypeMap.ActionType actionType) {
		if (actionType == ActionTypeMap.ActionType.ADMIN) {
			monitorCountAdminActionsCompleted++;
		}
		if (actionType == ActionTypeMap.ActionType.STUDENT) {
			monitorCountStudentActionsCompleted++;
		}
		if (actionType == ActionTypeMap.ActionType.ANY) {
			monitorCountAnyActionsCompleted++;
		}

		monitorCountActionsCompleted++;

		logger.debug(String.format("+++ incrementActionsCompleted(): admin: %d, student: %d, all: %d",
				monitorCountAdminActionsCompleted, monitorCountStudentActionsCompleted, monitorCountActionsCompleted));
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
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
    @SuppressWarnings({ "rawtypes" })
    public void registerCommands(Class[] commands) {
        logger.info("Adding " + commands.length + " commands");
        for (Class command : commands) {
            addCommand(command);
        }
    }
    
    /** Used by CmMonitor get get individual stats
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
            
        case StudentActionsExecuted:
        	return monitorCountStudentActionsExecuted;
            
        case StudentActionsCompleted:
        	return monitorCountStudentActionsCompleted;
            
        case AdminActionsExcecuted:
        	return monitorCountAdminActionsExecuted;
            
        case AdminActionsCompleted:
        	return monitorCountAdminActionsCompleted;
            
        case ProcessingTime:
            return monitorTotalProcessingTime;
            
        case AdminProcessingTime:
            return monitorAdminProcessingTime;
            
        case StudentProcessingTime:
            return monitorStudentProcessingTime;
            
        case AnyProcessingTime:
            return monitorAnyProcessingTime;
            
        case ExceptionCount:
            return monitorCountOfExceptions;
            
            default:
                return -1;
           
        }
    }
    public static enum MonitorData {
        ActionsExecuted,
        ActionsCompleted,
        ProcessingTime,
        ExceptionCount,
        StudentActionsExecuted,
        StudentActionsCompleted,
        AdminActionsExcecuted,
        AdminActionsCompleted,
        AnyActionsExecuted,
        AnyActionsCompleted,
        AdminProcessingTime,
        StudentProcessingTime,
        AnyProcessingTime
    }
    
    
    /** for zabbix logging see CmMonitor */
    int monitorCountActionsExecuted; 
    int monitorCountActionsCompleted;
    int monitorCountOfExceptions;
    long monitorTotalProcessingTime;
    
    int monitorCountStudentActionsExecuted;
    int monitorCountStudentActionsCompleted;
    int monitorCountAdminActionsExecuted;
    int monitorCountAdminActionsCompleted;
    int monitorCountAnyActionsExecuted;
    int monitorCountAnyActionsCompleted;

    long monitorAdminProcessingTime;
    long monitorStudentProcessingTime;
    long monitorAnyProcessingTime;
}


