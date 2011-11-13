package hotmath.cm.util;


public class LogMonitorOutputImplStdOut implements LogMonitorOutput {
    public void writeRecord(String type, String timeStamp, String actionName, String args, int elapseTime, int userId,
            String userType, String actionId) {
        System.out.println(type + ", " + timeStamp + ", " + actionName + ", " + args + ", " + elapseTime + ", " + userId);
    }
}
