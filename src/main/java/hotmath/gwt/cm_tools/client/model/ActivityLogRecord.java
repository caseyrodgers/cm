package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_core.client.util.DateUtils4Gwt;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.Date;

public class ActivityLogRecord implements Response {
    
    private int uid;
    private int key;
    private Date activityDate;
    private int activeMinutes;
    public ActivityLogRecord() {}
    public ActivityLogRecord(int key, int uid, Date activityDate, int activeMinutes) {
        this.key = key;
        this.uid = uid;
        this.activityDate = activityDate;
        this.activeMinutes = activeMinutes;
    }
    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public Date getActivityDate() {
        return activityDate;
    }
    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }
    public int getKey() {
        return key;
    }
    public void setKey(int key) {
        this.key = key;
    }

    
    public int getActiveMinutes() {
        return activeMinutes;
    }
    public void setActiveMinutes(int activeMinutes) {
        this.activeMinutes = activeMinutes;
    }
    public String getActivityDay() {
        return DateUtils4Gwt.getPrettyDateString(activityDate,  true);
    }
    @Override
    public String toString() {
        return "ActivityLogRecord [uid=" + uid + ", key=" + key + ", activityDate=" + activityDate + ", activeMinutes=" + activeMinutes + "]";
    }

}
