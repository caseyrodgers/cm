package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** Contains information about a single user's
 *  widget stats
 *  
 * @author casey
 *
 */
public class UserWidgetStats implements Response {
    
    private int uid;
    private int correctPercent;
    private int widgetCompletedCount;

    private UserWidgetStats() {}
    
    private UserWidgetStats(int uid, int correctPercent, int widgetCompletedCount) {
        this.uid = uid;
        this.correctPercent = correctPercent;
        this.widgetCompletedCount = widgetCompletedCount;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getCorrectPercent() {
        return correctPercent;
    }

    public void setCorrectPercent(int correctPercent) {
        this.correctPercent = correctPercent;
    }
    
    public int getWidgetCompletedCount() {
        return this.widgetCompletedCount;
    }
}
