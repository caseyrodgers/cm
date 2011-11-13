package hotmath.cm.util;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sb.util.SbUtilities;



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
    boolean _writeToDb=true;
    LogMonitorOutput output;

    private void stopTailing() {
        _tailer.stopTailing();
    }

    private LogMonitor(String file) {
        this(file, new LogMonitorOutputImplStdOut());
    }
    
    private LogMonitor(String file,LogMonitorOutput output) {
        this.output = output;
        
        System.out.println("Processing log: " + file + " (output=" + output + ")");

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

        String start = ActionDispatcher.LOG_FORMAT_BEGIN;

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
        	
            // System.out.println(String.format("cm_log record=%d %s, %s,%d,%s,%s", (++recordsWritten),timeStamp, userType, userId, actionName, id));
        	output.writeRecord("start", timeStamp, actionName, args, -1, userId, userType, id);
        } else {
            /**
             * may be end of action
             * 
             */
            String end = ActionDispatcher.LOG_FORMAT_END;
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
                    output.writeRecord("end", timeStamp, actionName, null, elapsedTime, userId, userType, id);                	
                }
                else {
                	String msg = matcher.group(6).trim();
                	// trim "FAILED" if present
                	int idx;
                	if ((idx = msg.indexOf("FAILED")) > 0) {
                		msg = msg.substring(0, idx);
                	}
                	output.writeRecord("fail", timeStamp, actionName, msg, elapsedTime, userId, userType, id);
                }
            }
        }
    }
    
    public void setWriteToDb(boolean writeToDb) {
        _writeToDb = writeToDb;
    }
    
    int recordsWritten=0;
   

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
            if (as.length < 1)
                throw new Exception("usage: ... logfile [-write_to_db=true]");
            
            SbUtilities.addOptions(as);
            String x = SbUtilities.getOption("true", "write_to_db");
            boolean writeToDb = SbUtilities.getBoolean(x);
            
            LogMonitorOutput output = writeToDb?new LogMonitorOutputImplDb():new LogMonitorOutputImplStdOut();
            new LogMonitor(as[0], output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
