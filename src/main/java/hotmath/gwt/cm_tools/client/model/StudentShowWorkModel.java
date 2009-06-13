package hotmath.gwt.cm_admin.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class StudentShowWorkModel extends BaseModelData {
    private static final long serialVersionUID = 184993523959891704L;

    public static final String PID_KEY = "pid";
    public static final String LABEL_KEY = "label";
    public static final String INSERT_TIME_KEY = "insert_time_mills";
    public static final String UID_KEY = "uid";
    public static final String VIEW_TIME_KEY = "uid";

    public String getPid() {
        return get(PID_KEY);
    }

    public void setPid(String pid) {
        set(PID_KEY, pid);
    }

    public void setViewTime(String str) {
        set(VIEW_TIME_KEY, str);
    }

    public String getViewTime() {
        return get(VIEW_TIME_KEY);
    }

    public String getLabel() {
        return get(LABEL_KEY);
    }

    public void setLabel(String label) {
        set(LABEL_KEY, label);
    }

    public Long getInsertTimeMills() {
        return get(INSERT_TIME_KEY);
    }

    public void setInsertTimeMills(long insertTimeMills) {
        set(INSERT_TIME_KEY, insertTimeMills);
    }

    public StudentShowWorkModel() {
    }
}
