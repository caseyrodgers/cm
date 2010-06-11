package hotmath.cm.util;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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
                if (line.indexOf("rpc.ActionDispatcher") > -1)
                    processActionLog(line);
            }

            @Override
            public void logReady() {
                processCommands();
            }
        });

        _tailer.start();
    }
    
    private void processCommands() {
        System.out.println("Ready ...");
        while(true) {
            try {
                System.out.print("> ");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String command = br.readLine();
                // test the String
                if (command.trim().equals("")) {
                    System.out.println("No command found");
                } else if(command.equals("action_count")) {
                    System.out.println("Count=" + getActionInfo().size());
                }
                else if(command.equals("print")) {
                    System.out.println(getActionInfo().toString());
                }
                else if(command.equals("lines")) {
                    System.out.println("lines processed: " + linesProcessed);
                }
                else {
                    System.out.println("No such command");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        
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
     * @param line
     */
    private void processActionLog(String line) {
        linesProcessed++;
        String start = "^(.*)\\ \\[.*\\:\\ (.*)\\ toString.*";
        Pattern startPattern = Pattern.compile(start);
        Matcher matcher = startPattern.matcher(line);
        if (matcher.find()) {
            /**
             * is start of action, make sure it exists
             * 
             */
            String timeStamp = matcher.group(1).trim();
            ActionInfo ai = getActionInfo(matcher.group(2).trim());
            ai.setTimeStamp(timeStamp);

            // see if there are args
            String argString = "toString.*\\[(.*)\\]";
            Pattern argPattern = Pattern.compile(argString);
            Matcher argMatcher = argPattern.matcher(line);
            if (argMatcher.find()) {
                String args = argMatcher.group(0);
                ai.getArgs().add(timeStamp += " " + args);
            }
        } else {
            /**
             * is end of action
             * 
             */
            String end = ".*RPC Action\\ (.*)\\.*toString.*elapsed time\\:\\ (.*)$";
            Pattern endPattern = Pattern.compile(end);
            matcher = endPattern.matcher(line);
            if (matcher.find()) {
                /**
                 * is start of action
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
