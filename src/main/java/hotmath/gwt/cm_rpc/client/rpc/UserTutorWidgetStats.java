package hotmath.gwt.cm_rpc.client.rpc;


public class UserTutorWidgetStats implements Response {
    
    int userId;
    int correctPercent;
    int countWidgets;
    
    public UserTutorWidgetStats() {}
    
    public UserTutorWidgetStats(int userId, int correctPercent, int countWidgets) {
        this.userId = userId;
        this.correctPercent = correctPercent;
        this.countWidgets = countWidgets;
    }

    public int getCountWidgets() {
        return countWidgets;
    }

    public void setCountWidgets(int countWidgets) {
        this.countWidgets = countWidgets;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /** If less than 2 widgets have been completed
     *  then return indicator that percent is not valid
     *  and should not be shown.
     *  
     * @return
     */
    public int getCorrectPercent() {
        return correctPercent;
    }

    public void setCorrectPercent(int correctPercent) {
        this.correctPercent = correctPercent;
    }
}