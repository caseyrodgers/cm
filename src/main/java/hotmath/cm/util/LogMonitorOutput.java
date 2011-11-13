package hotmath.cm.util;

public interface LogMonitorOutput {
    void writeRecord(String type, String timeStamp, String actionName, String args, int elapseTime,int userId, String userType, String actionId);
}
