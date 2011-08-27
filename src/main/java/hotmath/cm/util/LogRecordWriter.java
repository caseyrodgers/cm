package hotmath.cm.util;

public interface LogRecordWriter {
    void writeDatabaseRecord(String type, String timeStamp, String actionName, String args, int elapseTime,int userId, String userType,String actionId);
}