package hotmath.cm.util;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import sb.util.*;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class LogMonitor {

    static {
        HotmathFlusher.getInstance().addFlushable(new Flushable() {
            @Override
            public void flush() {
                if (__instances != null) {
                    for (LogMonitor lm : __instances.values()) {
                        lm.stopTailing();
                    }
                    __instances.clear();
                }
            }
        });
    }

    static private Map<String, LogMonitor> __instances = new HashMap<String, LogMonitor>();

    static public LogMonitor getInstance(String log) {
        if (__instances.get(log) == null) {
            __instances.put(log, new LogMonitor(log));
        }
        return __instances.get(log);
    }

    LogTailer _tailer;
    boolean _logIsReady;

    private void stopTailing() {
        _tailer.stopTailing();
    }

    private LogMonitor(String file) {
        
        System.out.println("Reading log ..");

        _tailer = new LogTailer(new File(file), 2000, true);
        _tailer.addLogFileTailerListener(new LogFileTailerListener() {
            @Override
            public void newLogFileLine(String line) {
                if (line.indexOf("rpc.ActionDispatcher") > -1 || line.indexOf("service.CmServiceImpl") > -1)
                    processActionLog(line);
            }
        });
        _tailer.start();
    }

    public Map<String, ActionInfo> getActionInfo() {
        return actions;
    }

    int linesProcessed;
    
    Map<String, ActionInfo> actions = new HashMap<String, ActionInfo>();

    /**
     * Works off of these two lines: start ... - RPC Action executing:
     * GetAdminTrendingDataDetailAction toString:
     * hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataDetailAction@ff0d4b
     * 
     * end ... - RPC Action GetAdminTrendingDataDetailAction toString:
     * hotmath.gwt
     * .shared.client.rpc.action.GetAdminTrendingDataDetailAction@ff0d4b
     * complete: elapsed time: 0
     * 
     * end .. with thread local info:
     * 2010-09-20 00:48:18,124 INFO  http-8081-3 [hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher] - 
     *              RPC Action (userId:6,userType:ADMIN) GetAccountInfoForAdminUidAction toString: 
     *              hotmath.gwt.shared.client.rpc.action.GetAccountInfoForAdminUidAction@12022b7 complete; elapsed time: 1018 msec
     * 
     * @param line
     */
    private void processActionLog(String line) {
        linesProcessed++;

        String start = "^(.*),.*RPC Action\\ \\(userId:(.*),userType:(.*)\\)\\ \\(ID:(.*)\\)\\ .*executing\\:(.*)\\.*toString.*";

        Pattern startPattern = Pattern.compile(start);
        Matcher matcher = startPattern.matcher(line);
        if (matcher.find()) {
        	/**
        	 * is start of an Action
        	 */
        	String timeStamp = matcher.group(1).trim();
        	int userId = SbUtilities.getInt(matcher.group(2).trim());
        	String userType = matcher.group(3).trim();
        	String id = matcher.group(4).trim();
        	String actionName = matcher.group(5).trim();

        	// see if there are args
        	String argString = "toString.*\\[(.*)\\]";
        	Pattern argPattern = Pattern.compile(argString);
        	Matcher argMatcher = argPattern.matcher(line);
        	String args="";
        	if (argMatcher.find()) {
        		args = argMatcher.group(1);
        	}

        	writeDatabaseRecord("start", timeStamp, actionName, args, -1, userId, userType, id);
        } else {
            /**
             * may be end of action
             * 
             */
            String end = "^(.*),.*RPC Action\\ \\(userId:(.*),userType:(.*)\\)\\ \\(ID:(.*)\\)\\ (.*)\\.*toString:(.*)elapsed time\\:\\ (.*) msec$";
            Pattern endPattern = Pattern.compile(end);
            matcher = endPattern.matcher(line);
            if (matcher.find()) {
                /**
                 * is end of action or failed action
                 */
                String timeStamp = matcher.group(1).trim();
            	int userId = SbUtilities.getInt(matcher.group(2).trim());
            	String userType = matcher.group(3).trim();
                String id = matcher.group(4).trim();
                String actionName = matcher.group(5).trim();
                String mills = matcher.group(7).trim();
                int elapsedTime = Integer.parseInt(mills);
                
                if (line.indexOf("FAILED") < 0) {
                    writeDatabaseRecord("end", timeStamp, actionName, null, elapsedTime, userId, userType, id);                	
                }
                else {
                	String msg = matcher.group(6).trim();
                	// trim "FAILED" if present
                	int idx;
                	if ((idx = msg.indexOf("FAILED")) > 0) {
                		msg = msg.substring(0, idx);
                	}
            		writeDatabaseRecord("fail", timeStamp, actionName, msg, elapsedTime, userId, userType, id);
                }
            }
        }
    }
    int recordsWritten=0;
    private void writeDatabaseRecord(String type, String timeStamp, String actionName, String args, int elapseTime,int userId, String userType,
    		String actionId)  {
        Connection conn=null;
        PreparedStatement ps=null;
        System.out.println(String.format("cm_log record=%d %s,%d,%s,%s", (++recordsWritten), userType, userId, actionName, actionId));
        
        try {
            String sql = "insert into HA_ACTION_LOG(type, time_stamp, action_name, action_args, elapse_time, user_id, user_type, action_id)values(?,?,?,?,?,?,?,?)";
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, type);
            ps.setString(2, timeStamp);
            ps.setString(3, actionName);
            ps.setString(4, args);
            ps.setInt(5, elapseTime);
            ps.setInt(6, userId);
            ps.setString(7,userType);
            ps.setString(8, actionId);
            
            ps.executeUpdate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null, ps,conn);
        }
    }


    private ActionInfo getActionInfo(String name) {
        ActionInfo ai = actions.get(name);
        if (ai == null) {
            ai = new ActionInfo(name);
            actions.put(name, ai);
        }
        return ai;
    }

    static public void main(String as[]) {
        try {
            if (as.length != 1)
                throw new Exception("usage: ... logfile");

            new LogMonitor(as[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
