package hotmath.gwt.cm_rpc_core.server.rpc;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.gwt.cm_rpc_core.client.ClientInfo;
import hotmath.gwt.cm_rpc_core.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc_core.client.CmExceptionDoNotNotify;
import hotmath.gwt.cm_rpc_core.client.CmUserException;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.ActionType;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.client.rpc.HasActionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcherListener.ActionExecutionType;
import hotmath.gwt.cm_rpc_core.server.rpc.MonitorType.MonitorTypeItem;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.gwt.cm_rpc_core.server.service.ClientInfoHolder;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sb.mail.SbMailManager;

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

	public static final int CONNECTION_WARNING_THRESHOLD = 10;

	static private ActionDispatcher __instance;

	static String startDate;

	static synchronized public ActionDispatcher getInstance() {
		if (__instance == null)
			__instance = new ActionDispatcher();
		return __instance;
	}

	private int counter;

	static {
		HotmathFlusher.getInstance().addFlushable(new Flushable() {
			public void flush() {
				ActionDispatcher.flush();
			}
		});
	}

	@SuppressWarnings("rawtypes")
	Map<Class<? extends Action<? extends Response>>, Class> commands = new HashMap<Class<? extends Action<? extends Response>>, Class>();
	List<ActionDispatcherListener> listeners = new ArrayList<ActionDispatcherListener>();

    private Map<String, Response> oldResponses = new HashMap<String,Response>();


    private boolean _debuggingClientMode;
    


	static Logger logger = Logger.getLogger(ActionDispatcher.class.getName());

	/**
	 * remove commands
	 * 
	 * @TODO: should be closed.
	 */
	static public void flush() {
		__instance = null;
	}

	boolean isGuiDisplayed;
	private ActionDispatcher() {
		logger.info("Creating new ActionDispatcher");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		/**
		 * ContextListener will only be initialized in a Servlet Container. So,
		 * running in UnitTest or standalone ContextListener will not have been
		 * initialized ... so all tests fail.
		 * 
		 * @TODO: ContextListener should initialize getStartDate and have
		 *        servlet context override it when running in a servlet context.
		 * 
		 */
		if (ContextListener.getStartDate() != null)
			startDate = sdf.format(ContextListener.getStartDate());
		else
			startDate = sdf.format(System.currentTimeMillis());
		
		
		
		/** startup GUI log watcher if possible
		 * 
		 */
		
		try {
		    ActionDispatcherLoggerGui.getInstance();
		    isGuiDisplayed=true;
		}
		catch(Throwable th) {
		    logger.debug("Cannot start logger GUI", th);
		}
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
			Class action = ((ActionHandler) command.newInstance())
					.getActionType();
			commands.put(action, command);
		} catch (Exception e) {
			logger.error("Could not add command: " + command.getName(), e);
		}
	}
	
	public void setDebuggingClientMode(boolean yesNo) {
	    _debuggingClientMode=yesNo;
	    
	    // always reset
	    oldResponses.clear();
	}

	@SuppressWarnings("rawtypes")
	public Map<Class<? extends Action<? extends Response>>, Class> getCommands() {
		return commands;
	}

	@SuppressWarnings("rawtypes")
	public void setCommands(
			Map<Class<? extends Action<? extends Response>>, Class> commands) {
		this.commands = commands;
	}

	/**
	 * Add listener ability to ActionDispatcher. Each event executed will fire
	 * an event to any interested parties.
	 * 
	 * @param listener
	 */
	public void addActionDispatchListener(ActionDispatcherListener listener) {
		listeners.add(listener);
	}

	public void removeActionDispatcherListener(ActionDispatcherListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	public void removeActionDispatcherListenerAll() {
		listeners.clear();
	}

	private void fireActionDispatcherEvent(ActionExecutionType type,
			Action<? extends Response> action) {
		for (ActionDispatcherListener listener : listeners) {
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
	public <T extends Response> T execute(Action<T> action, Connection connectionToUse)
			throws CmRpcException {

	    if(_debuggingClientMode) {
	        logger.info("DEBUGGING_CLIENT enabled!");
	        
	        String actionName = action.getClass().getName();
	        if(ActionDispatcherLoggerGui.getInstance().shouldActionBeCached(actionName)) {
    	        // reuse action/response to avoid round-trip
        	    Response oldResponse = oldResponses.get(actionName);
        	    if(oldResponse != null) {
        	        logger.info("DEBUGGING_CLIENT: returning cached action (" + actionName + ")");
        	        return (T)oldResponse;
        	    }
	        }
	        else {
	            logger.info("DEBUGGING_CLIENT: not caching action: " + actionName);
	        }
	    }
	    
		String actionId = new StringBuilder().append(startDate).append(".").append(++counter).toString();

		boolean failed = false;
		String errMsg = null;
		String exceptionClass = null;

		ClientInfo clientInfo = ClientInfoHolder.get();
		if (clientInfo == null) {
			clientInfo = new ClientInfo();
			clientInfo.setUserType(UserType.UNKNOWN);
			logger.debug("+++ execute(): ClientInfo from ThreadLocal is NULL");
		}
		clientInfo.setActionId(actionId);

		long timeStart = System.currentTimeMillis();
		String c[] = action.getClass().getName().split("\\.");
		String clazzName = c[c.length - 1];

		logger.info(String
				.format("RPC Action (userId:%d,userType:%s) (ID:%s) executing: %s toString: %s",
						clientInfo.getUserId(), clientInfo.getUserType(),
						actionId, clazzName, action.toString()));

		Connection conn = null;
		ActionType actionType = ActionType.UNKNOWN;
		ActionHandler actionHandler = null;
		try {
			Class clazz = getActionCommand(action);
			if (clazz == null) {
				throw new CmRpcException(
						"Could not find Action Handler for Action: "
								+ action.getClass().getName());
			}
			actionHandler = (ActionHandler) clazz.newInstance();

			if (actionHandler == null)
				throw new CmRpcException("No such RPC Action Handler defined: "
						+ action.getClass().getName());

			/**
			 * Should connection management be automatic (default)? If Command
			 * object implements the ActionHandlerManualConnectionManagement
			 * interface, then no connection is automatically created, it must
			 * manually manage the creation/release of connections.
			 */
			if (!(actionHandler instanceof ActionHandlerManualConnectionManagement)) {
				if (logger.isInfoEnabled()) {
					logger.debug(String
							.format("RPC Action: (ID:%s) about to request DB Connection, openConnectionCount: (%d)",
									actionId, HMConnectionPool.getInstance()
											.getConnectionCount()));
				}
				
				
				
				if(connectionToUse == null) {
	                logger.debug("Creating new connection");

				    conn = HMConnectionPool.getConnection();
				}
				else { 
				    logger.debug("Setting connection to passed value");
				    conn = connectionToUse;
				}
			} else {
				if (logger.isInfoEnabled()) {
					logger.debug(String
							.format("RPC Action: (ID:%s) DB Connection not requested, openConnectionCount: (%d)",
									actionId, HMConnectionPool.getInstance()
											.getConnectionCount()));
				}
			}

			
			if(action instanceof HasActionInfo) {
				/** new method, embedded meta */
				actionType = ((HasActionInfo)action).getActionInfo().getActionType();
			}
			else {
				/** old method */
				actionType = ActionTypeMap.getActionType(clazzName);
			}
			
			if (actionType == ActionType.OTHER) {
				logger.debug(String.format(
						"=== RPC Action [%s] not in ActionTypeMap!", clazzName));
			}

			/**
			 * todo: move to listener
			 * 
			 */
			incrementActionsExecuted(actionType);

			fireActionDispatcherEvent(ActionExecutionType.BEFORE, action);

			T response = (T) actionHandler.execute(conn, action);

			fireActionDispatcherEvent(ActionExecutionType.AFTER, action);

			incrementActionsCompleted(actionType);

			if(_debuggingClientMode) {
			    oldResponses.put(action.getClass().getName(),  response);
			}
			
			return response;
		} catch (Exception e) {
			if ((e instanceof CmExceptionDoNotNotify) == false) {
				incrementActionsException(actionType);
				totalMonitorData.monitorCountActionsException++;

				//sendEmailNotifications(e, clientInfo);

				failed = true;
				errMsg = e.getMessage();
				exceptionClass = e.getClass().getName();
				if (e instanceof CmRpcException)
					throw (CmRpcException) e;
			} else if ((e instanceof CmUserException) == true) {
				throw new CmRpcException(e, true);
			}
			throw new CmRpcException(e);

		} finally {
			if (conn != null) {
			    
			    
			    if(connectionToUse != null) {
			        conn = null; // do not close
			    }
			    
				SqlUtilities.releaseResources(null, null, conn);
				if (logger.isInfoEnabled()) {
					logger.debug(String
							.format("RPC Action: (ID:%s) DB Connection closed, openConnectionCount: (%d)",
									actionId, HMConnectionPool.getInstance()
											.getConnectionCount()));
				}
				if (HMConnectionPool.getInstance().getConnectionCount() > CONNECTION_WARNING_THRESHOLD) {
					logger.debug(String
							.format("RPC Action: DB openConnectionCount: %d over threshold: %d",
									HMConnectionPool.getInstance()
											.getConnectionCount(),
									CONNECTION_WARNING_THRESHOLD));
				}
			} else if (actionHandler != null
					&& (actionHandler instanceof ActionHandlerManualConnectionManagement)) {
				if (logger.isInfoEnabled()) {
					logger.debug(String
							.format("RPC Action: (ID:%s) DB Connection not closed, openConnectionCount: (%d)",
									actionId, HMConnectionPool.getInstance()
											.getConnectionCount()));
				}
			}

			long now = System.currentTimeMillis();
			long executeTimeMills = (now - timeStart);

			if (!failed)
				logger.info(String
						.format("RPC Action (userId:%d,userType:%s) (ID:%s) %s toString: %s complete; elapsed time: %d msec",
								clientInfo.getUserId(),
								clientInfo.getUserType(), actionId, clazzName,
								action.toString(), executeTimeMills));
			else
				logger.info(String
						.format("RPC Action (userId:%d,userType:%s) (ID:%s) %s toString: %s - %s FAILED; elapsed time: %d msec",
								clientInfo.getUserId(),
								clientInfo.getUserType(), actionId, clazzName,
								exceptionClass, errMsg, executeTimeMills));

			incrementProcessingTime(actionType, executeTimeMills);
		}
	}
	public <T extends Response> T execute(Action<T> action) throws CmRpcException {
	    return execute(action, null);
	}
	

	/**
	 * Send error notification to errors@catchupmath.com to allow real time tracking
	 * of errors.
	 * 
	 * @param ex
	 */
	private void sendEmailNotifications(final Exception ex,
			final ClientInfo clientInfo) {

		/**
		 * send mail in separate thread to make sure not to block server thread.
		 */
		new Thread() {
			public void run() {

				String serverName = ContextListener.getServerName();

				String cmInstallationId="unknown";
				try {
				    cmInstallationId = CatchupMathProperties.getInstance().getCmInstallationId();
				}
				catch(Exception e) {
				    logger.error("Error getting cm installation id", e);
				}
				String subject = "CM (installation=" + cmInstallationId + ") ActionDispatcher (" + serverName + ") ["
						+ clientInfo.getUserId() + "] error: "
						+ ex.getLocalizedMessage();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw, true);
				ex.printStackTrace(pw);
				pw.flush();
				sw.flush();
				String message = subject + "\n\n" + clientInfo.toString()
						+ "\n\n" + sw.toString();
				try {
					SbMailManager.getInstance().sendMessage(subject, message,
							"errors@catchupmath.com", "errors@catchupmath.com",
							"text/plain");
				} catch (Exception e) {
					logger.error("Could not send error notification error", e);
				}
			}
		}.run();

	}

	private void incrementActionsExecuted(ActionType actionType) {
		switch (actionType) {
		case ADMIN:
			adminMonitorData.monitorCountActionsExecuted++;
			break;
		case STUDENT:
			studentMonitorData.monitorCountActionsExecuted++;
			break;
		case ANY:
			anyMonitorData.monitorCountActionsExecuted++;
			break;
		case OTHER:
			otherMonitorData.monitorCountActionsExecuted++;
			break;
		case HM_MOBILE:
			hmMobileMonitorData.monitorCountActionsExecuted++;
			break;			
		}

		totalMonitorData.monitorCountActionsExecuted++;

		logger.debug(String
				.format("+++ incrementActionsExecuted(): admin: %d, student: %d, any: %d, other: %d, all: %d",
						adminMonitorData.monitorCountActionsExecuted,
						studentMonitorData.monitorCountActionsExecuted,
						anyMonitorData.monitorCountActionsExecuted,
						otherMonitorData.monitorCountActionsExecuted,
						totalMonitorData.monitorCountActionsExecuted));
	}

	private void incrementProcessingTime(ActionType actionType,
			long executeTimeMillis) {
		switch (actionType) {
		case ADMIN:
			adminMonitorData.monitorProcessingTime += executeTimeMillis;
			break;
		case STUDENT:
			studentMonitorData.monitorProcessingTime += executeTimeMillis;
			break;
		case ANY:
			anyMonitorData.monitorProcessingTime += executeTimeMillis;
			break;
		case OTHER:
			otherMonitorData.monitorProcessingTime += executeTimeMillis;
			break;
		case HM_MOBILE:
			hmMobileMonitorData.monitorProcessingTime += executeTimeMillis;
			break;
		}

		totalMonitorData.monitorProcessingTime += executeTimeMillis;
	}

	private void incrementActionsCompleted(ActionType actionType) {
		switch (actionType) {
		case ADMIN:
			adminMonitorData.monitorCountActionsCompleted++;
			break;
		case STUDENT:
			studentMonitorData.monitorCountActionsCompleted++;
			break;
		case ANY:
			anyMonitorData.monitorCountActionsCompleted++;
			break;
		case OTHER:
			otherMonitorData.monitorCountActionsCompleted++;
			break;
		case HM_MOBILE:
			hmMobileMonitorData.monitorCountActionsCompleted++;
			break;
		}

		totalMonitorData.monitorCountActionsCompleted++;

		logger.debug(String
				.format("+++ incrementActionsCompleted(): admin: %d, student: %d, all: %d",
						adminMonitorData.monitorCountActionsCompleted,
						studentMonitorData.monitorCountActionsCompleted,
						totalMonitorData.monitorCountActionsCompleted));
	}

	private void incrementActionsException(ActionType actionType) {
		switch (actionType) {
		case ADMIN:
			adminMonitorData.monitorCountActionsException++;
			break;
		case STUDENT:
			studentMonitorData.monitorCountActionsException++;
			break;
		case ANY:
			anyMonitorData.monitorCountActionsException++;
			break;
		case OTHER:
			otherMonitorData.monitorCountActionsException++;
			break;
			
		case HM_MOBILE:
			hmMobileMonitorData.monitorCountActionsException++;
			break;
		}
		totalMonitorData.monitorCountActionsException++;

		logger.debug(String
				.format("+++ incrementActionsException(): admin: %d, student: %d, all: %d",
						adminMonitorData.monitorCountActionsException,
						studentMonitorData.monitorCountActionsException,
						totalMonitorData.monitorCountActionsException));
	}

	/**
	 * Check for command, if not registered then look for Command following
	 * conventions:
	 * 
	 * 1. actions end with Action 2. all commands live in package
	 * hotmath.gwt.shared.server.service.command; 3. cmd names match action
	 * names, but end with Command instead of Action (MyAction == MyCommand)
	 * 
	 * @param action
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Class getActionCommand(Action<? extends Response> action)
			throws Exception {
		if (!commands.containsKey(action.getClass())) {
			logger.debug("Auto registering action: " + action.getClass());
			String actionName = action.getClass().getName();
			if (actionName.endsWith("Action")) {

				Class cmdClass = loadCommandClass(action);
				Class actionType = ((ActionHandler) cmdClass.newInstance())
						.getActionType();

				commands.put(actionType, cmdClass);
			}
		}
		Class x = commands.get(action.getClass());
		return x;
	}

	/*
	 * extract name and construct command class name using standard package.
	 */
	@SuppressWarnings("rawtypes")
	static public Class loadCommandClass(Action<? extends Response> action)
			throws Exception {

		Class cmdClass = null;

		String actionName = action.getClass().getName();
		String p[] = actionName.split("\\.");
		String cmdName = p[p.length - 1];
		cmdName = cmdName.substring(0, cmdName.length() - 6) + "Command";

		String assumedCmdPackage = "";
		for (int i = 0; i < p.length - 1; i++) {
			assumedCmdPackage += p[i] + ".";
		}
		assumedCmdPackage = assumedCmdPackage.replace("client", "server");
		
		logger.debug("AssumedCommandPackage: " + assumedCmdPackage);

		/**
		 * Create a list of possible places to search.
		 * 
		 * commands are named:
		 * 
		 * MyThingAction == MyThingCommand
		 * 
		 */
		List<String> places = Arrays.asList(assumedCmdPackage,
				"hotmath.gwt.shared.server.service.command.",
				"hotmath.gwt.cm_admin.server.command.",
				"hotmath.gwt.cm_mobile.server.rpc.",
				"hotmath.gwt.cm_mobile_shared.server.rpc.",
				"hotmath.gwt.solution_editor.server.rpc.",
				"hotmath.gwt.cm_activity.server.rpc.",
                "hotmath.gwt.shared.server.service.command.cm2."
		);
		for (String place : places) {
			String commandClass = place + cmdName;
			try {
				/** create instance and get object */
				cmdClass = Class.forName(commandClass);
				logger.debug("Auto registering action command: " + cmdName);
				break;
			} catch (ClassNotFoundException ie) {
				/** silent */
			}
		}

		if (cmdClass == null)
			throw new CmRpcException("No command found for Action: " + action);

		return cmdClass;
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
		logger.debug("Adding " + commands.length + " commands");
		for (Class command : commands) {
			addCommand(command);
		}
	}

	/**
	 * Used by CmMonitor get individual stats from running ActionDispatcher.
	 * 
	 * @param type
	 * @return
	 */
	public long getMonitoredData(MonitorDataPoints type) {
		switch (type) {
		case ActionsExecuted:
			return totalMonitorData.monitorCountActionsExecuted;

		case ActionsCompleted:
			return totalMonitorData.monitorCountActionsCompleted;

		case StudentActionsExecuted:
			return studentMonitorData.monitorCountActionsExecuted;

		case StudentActionsCompleted:
			return studentMonitorData.monitorCountActionsCompleted;

		case StudentActionsException:
			return studentMonitorData.monitorCountActionsException;

		case AdminActionsExcecuted:
			return adminMonitorData.monitorCountActionsExecuted;

		case AdminActionsCompleted:
			return adminMonitorData.monitorCountActionsCompleted;

		case AdminActionsException:
			return adminMonitorData.monitorCountActionsException;

		case ProcessingTime:
			return totalMonitorData.monitorProcessingTime;

		case AdminProcessingTime:
			return adminMonitorData.monitorProcessingTime;

		case StudentProcessingTime:
			return studentMonitorData.monitorProcessingTime;

		case AnyProcessingTime:
			return anyMonitorData.monitorProcessingTime;

		case OtherProcessingTime:
			return otherMonitorData.monitorProcessingTime;

		case ExceptionCount:
			return totalMonitorData.monitorCountActionsException;
			
		case HmMobileActionsExecuted:
			return hmMobileMonitorData.monitorCountActionsExecuted;
			
		case HmMobileActionsCompleted:
			return hmMobileMonitorData.monitorCountActionsCompleted;
			
		case HmMobileActionsException:
			return hmMobileMonitorData.monitorCountActionsException;
			
		case HmMobileProcessingTime:
			return hmMobileMonitorData.monitorProcessingTime;
			
		default:
			return -1;
		}
	}

	public static enum MonitorDataPoints {
		ActionsExecuted, ActionsCompleted, ProcessingTime, ExceptionCount, StudentActionsExecuted, 
		StudentActionsCompleted, StudentActionsException, AdminActionsExcecuted, AdminActionsCompleted, 
		AdminActionsException, AnyActionsExecuted, AnyActionsCompleted, AnyActionsException, 
		OtherActionsExecuted, OtherActionsCompleted, OtherActionsException, AdminProcessingTime, 
		StudentProcessingTime, AnyProcessingTime, OtherProcessingTime,
		HmMobileActionsExecuted, HmMobileActionsCompleted,HmMobileActionsException,HmMobileProcessingTime
	}

	/** for zabbix logging see CmMonitor 
	 * 
	 * */
	MonitorType totalMonitorData = new MonitorType(MonitorTypeItem.TOTAL);
    MonitorType studentMonitorData = new MonitorType(MonitorTypeItem.STUDENT);
    MonitorType adminMonitorData = new MonitorType(MonitorTypeItem.ADMIN);
    MonitorType anyMonitorData = new MonitorType(MonitorTypeItem.ANY);
    MonitorType otherMonitorData = new MonitorType(MonitorTypeItem.OTHER);
    MonitorType hmMobileMonitorData = new MonitorType(MonitorTypeItem.HM_MOBILE);
    
    
    /** define format here to allow sharing
     * 
     */
    static public String LOG_FORMAT_BEGIN="^(.*),.*RPC Action\\ \\(userId:(.*),userType:(.*)\\)\\ \\(ID:(.*)\\)\\ .*executing\\:(.*)\\.*toString.*";
    static public String LOG_FORMAT_END="^(.*),.*RPC Action\\ \\(userId:(.*),userType:(.*)\\)\\ \\(ID:(.*)\\)\\ (.*)\\.*toString:(.*)elapsed time\\:\\ (.*) msec$";
}
