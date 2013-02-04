package hotmath.gwt.cm_rpc.client.rpc;


public class UserTutorWidgetStats implements Response {
    
    int userId;
    int percent;
    
    public UserTutorWidgetStats() {}
    
    public UserTutorWidgetStats(int userId, int percent) {
        this.userId = userId;
        this.percent = percent;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
