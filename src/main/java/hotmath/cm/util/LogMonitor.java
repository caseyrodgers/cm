package hotmath.cm.util;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogMonitor {
    
    static {
        HotmathFlusher.getInstance().addFlushable(new Flushable() {
            @Override
            public void flush() {
                if(__instance != null) {
                    __instance.stopTailing();
                    __instance = null;
                }
            }
        });
    }
    
    
    static private LogMonitor __instance;
    static public LogMonitor getInstance() {
        if(__instance == null) {
            __instance = new LogMonitor();
        }
        return __instance;
    }
    
    
    LogTailer _tailer;
    
    private LogMonitor() {
        this("/home/hotmath/tomcat2/logs/catalina.out");
    }
    
    private void stopTailing() {
        _tailer.stopTailing();
    }
    
    private LogMonitor(String file) {
        
        _tailer = new LogTailer(new File(file),2000,true);
        _tailer.addLogFileTailerListener(new LogFileTailerListener() {
            @Override
            public void newLogFileLine(String line) {
                if(line.indexOf("service.ActionDispatcher") > -1)
                    processActionLog(line);
            }
        });
        
        _tailer.start();
    }
    
    public Map<String,ActionInfo> getActionInfo() {
        return actions;
    }
    
    Map<String,ActionInfo> actions = new HashMap<String,ActionInfo>();
    
    /** Works off of these two lines:
     * start
     * ... - RPC Action executing: GetAdminTrendingDataDetailAction  toString: hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataDetailAction@ff0d4b
     * 
     * end
     * ... - RPC Action GetAdminTrendingDataDetailAction toString: hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataDetailAction@ff0d4b complete: elapsed time: 0
     * @param line
     */
    private void processActionLog(String line) {
        
        String start = ".*\\:\\ (.*)\\ .*toString";
        Pattern startPattern = Pattern.compile(start);
        Matcher matcher = startPattern.matcher(line);
        if (matcher.find()) {
            /** is start of action
             * 
             */
            ActionInfo ai = getActionInfo(matcher.group(1).trim());
         }
        else {
            /** is end of action
             * 
             */
            String end = ".*RPC Action\\ (.*)\\.*toString.*elapsed time\\:\\ (.*)$";
            Pattern endPattern = Pattern.compile(end);
            matcher = endPattern.matcher(line);
            if (matcher.find()) {
                /** is start of action
                 * 
                 */
                ActionInfo ai = getActionInfo(matcher.group(1).trim());
                int elapseTime = Integer.parseInt(matcher.group(2));
                ai.setInfo(elapseTime);
             }
        }
    }

    private ActionInfo getActionInfo(String name) {
        ActionInfo ai = actions.get(name);
        if(ai == null) {
            ai = new ActionInfo(name);
            actions.put(name, ai);
        }
        return ai;
    }
    
    static public void main(String as[]) {
        new LogMonitor("/temp/test.log");
    }
}
