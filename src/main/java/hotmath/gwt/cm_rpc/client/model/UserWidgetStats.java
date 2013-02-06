package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

/** Contains information about a single user's
 *  widget stats
 *  
 * @author casey
 *
 */
public class UserWidgetStats implements Response {
    
    private int uid;
    private int correctPercent;

    public UserWidgetStats() {}
    
    public UserWidgetStats(int uid, int correctPercent) {
        this.uid = uid;
        this.correctPercent = correctPercent;
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
}
