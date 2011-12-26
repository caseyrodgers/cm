package hotmath.gwt.cm_rpc.server.rpc;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import sb.util.SbUtilities;

/**
 * Swing class used to monitor ActionDispatcher log messages
 * 
 * @author casey
 * 
 */
public class ActionDispatcherLoggerGui extends JFrame {
    
    private static ActionDispatcherLoggerGui __instance;
    public static ActionDispatcherLoggerGui getInstance() {
        if(__instance == null) {
            __instance = new ActionDispatcherLoggerGui();
        }
        return __instance;
    }

    JTextArea _logArea = new JTextArea(), _requestsArea = new JTextArea();
    JTextField _filter = new JTextField();
    List<String> _fullLog = new ArrayList<String>();
    JToggleButton _disabled = new JToggleButton("Enable");

    private ActionDispatcherLoggerGui() {

        setupLog4JAppender();

        setSize(640, 480);
        setTitle("ActionDispatcher Log Watcher");

        _logArea.setDocument(new JTextFieldFilter(_filter));

        _filter.setToolTipText("Filter log contents");
        _filter.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                _logArea.setText("");
                for(String lm: _fullLog) {
                    _logArea.append(lm);
                }
                _logArea.setCaretPosition(_logArea.getDocument().getLength());
            }
        });

        
        JPanel footer = new JPanel();
        footer.setLayout(new BorderLayout());
        footer.add("Center", _filter);
        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _fullLog.clear();
                _logArea.setText("");
                _requestsArea.setText("");
            }
        });
        footer.add("East", clear);
        footer.add("West", _disabled);
        
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout());
        logPanel.add("Center", new JScrollPane(_logArea));
        logPanel.add("South", _filter);
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.add("Log", logPanel);
        tabPane.add("Requests Only", new JScrollPane(_requestsArea));
        getContentPane().add("Center", tabPane);
        getContentPane().add("South", footer);
        setVisible(true);
    }

    private void setupLog4JAppender() {

        Logger.getRootLogger().addAppender(new AppenderSkeleton() {

            @Override
            public boolean requiresLayout() {
                return false;
            }

            @Override
            public void close() {
            }

            @Override
            protected void append(LoggingEvent logEvent) {
                if(!_disabled.isSelected())
                    return;
                
                setVisible(true);
                
                String logMsg = logEvent.getMessage() + " \n";
                processRequestActionLog(logEvent.timeStamp, logMsg);
                _logArea.append(logMsg);
                _logArea.setCaretPosition(_logArea.getDocument().getLength());

                _fullLog.add(logMsg);
            }
        });

    }
    
    private RequestInfo  getRequestInfo() {
        return null;
    }
    
    static class RequestInfo {
        String name;
        String params;
    }

    
    int linesProcessed;
    private void processRequestActionLog(long timeStampIn, String line) {
        linesProcessed++;

        DateFormat df = new SimpleDateFormat();
        String timeStamp = df.format(new Date(timeStampIn));
        
        String LOG_FORMAT_BEGIN="^(.*).*RPC Action\\ \\(userId:(.*),userType:(.*)\\)\\ \\(ID:(.*)\\)\\ .*executing\\:(.*)\\.*toString.*";
        String LOG_FORMAT_END="^(.*)RPC Action\\ \\(userId:(.*),userType:(.*)\\)\\ \\(ID:(.*)\\)\\ (.*)\\.*toString:(.*)elapsed time\\:\\ (.*) msec.*$";

        Pattern startPattern = Pattern.compile(LOG_FORMAT_BEGIN);
        Matcher matcher = startPattern.matcher(line);
        if (matcher.find()) {
            /**
             * is start of an Action
             */
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
            Pattern endPattern = Pattern.compile(LOG_FORMAT_END);
            matcher = endPattern.matcher(line);
            if (matcher.find()) {
                /**
                 * is end of action or failed action
                 */
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
    
    private void writeDatabaseRecord(String type, String timeStamp, String actionName, String args, int elapseTime,int userId, String userType,String actionId) {
        
        Toolkit.getDefaultToolkit().beep();
        
        _requestsArea.append("type: " + type + "\ntime: " + timeStamp + "\nname:  " + actionName + "\nelapsed:  " + elapseTime + "\nargs: " + args + "\nactionId: " + actionId + "\n\n");
        _requestsArea.setCaretPosition(_requestsArea.getDocument().getLength());
    }

    static public void main(String as[]) {
        new ActionDispatcherLoggerGui();
        Logger log = Logger.getLogger(ActionDispatcherLoggerGui.class);
        log.error("Test");
    }

}

class JTextFieldFilter extends PlainDocument {

    JTextField _filterText;

    public JTextFieldFilter(JTextField filterText) {
        this._filterText = filterText;
    }

    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null)
            return;

        if (str.toLowerCase().contains(_filterText.getText().toLowerCase())) {
            super.insertString(offset, str, attr);
        }
    }
}