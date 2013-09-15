package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;


public class UserInfoStats implements Response {
    
    UserTutorWidgetStats tutorStats;
    int userId;
    int activeMinutes;
    
    public UserInfoStats() {}
    
    public UserInfoStats(int userId, UserTutorWidgetStats tutorStats, int activeMinutes) {
        this.userId = userId;
        this.tutorStats = tutorStats;
        this.activeMinutes = activeMinutes;
    }
    public int getActiveMinutes() {
        return activeMinutes;
    }

    public void setActiveMinutes(int activeMinutes) {
        this.activeMinutes = activeMinutes;
    }

    public UserTutorWidgetStats getTutorStats() {
        return tutorStats;
    }

    public void setTutorStats(UserTutorWidgetStats tutorStats) {
        this.tutorStats = tutorStats;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserInfoStats [tutorStats=" + tutorStats + ", userId=" + userId + ", activeMinutes=" + activeMinutes + "]";
    }
}
