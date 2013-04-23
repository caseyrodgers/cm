package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class StudentShowWorkModelPojo implements Response {
    
    private String pid;
    private long insertTime;
    private int uid;
    private String viewTimeKey;

    public StudentShowWorkModelPojo() {
    }
    
    public StudentShowWorkModelPojo(String pid,long insertTime, int uid, String viewTimeKey) {
        this.pid = pid;
        this.insertTime = insertTime;
        this.uid = uid;
        this.viewTimeKey = viewTimeKey;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getLabel() {
        return viewTimeKey;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getViewTimeKey() {
        return viewTimeKey;
    }

    public void setViewTimeKey(String viewTimeKey) {
        this.viewTimeKey = viewTimeKey;
    }

    @Override
    public String toString() {
        return "StudentShowWorkModelPojo [pid=" + pid + ", labe = " + getLabel() +  ", insertTime=" + insertTime + ", uid="  + uid + ", viewTimeKey=" + viewTimeKey + "]";
    }
}
