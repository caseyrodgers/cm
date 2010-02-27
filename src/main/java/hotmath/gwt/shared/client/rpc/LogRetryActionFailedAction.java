package hotmath.gwt.shared.client.rpc;

import hotmath.gwt.shared.client.util.RpcData;

public class LogRetryActionFailedAction implements Action<RpcData> {
    
    Action<? extends Response> action;
    Integer uid;
    String className;
    String stackTrace;
    String logType;
    
    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public LogRetryActionFailedAction() {}
    
    public LogRetryActionFailedAction(String logType, Integer uid, String className, Action<? extends Response> action, String stackTrace) {
        this.logType = logType;
        this.uid = uid;
        this.className = className;
        this.action = action;
        this.stackTrace = stackTrace;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Action<? extends Response> getAction() {
        return action;
    }

    public void setAction(Action<? extends Response> action) {
        this.action = action;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "LogRetryActionFailedAction [action=" + action + ", className=" + className + ", logType=" + logType
                + ", stackTrace=" + stackTrace + ", uid=" + uid + "]";
    }
}
