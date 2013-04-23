package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;


public class UserTutorWidgetStats implements Response {
    
    int userId;
    int correctPercent;
    int countWidgets;
    int countCorrect;
    
    public UserTutorWidgetStats() {}
    
    public UserTutorWidgetStats(int userId, int correctPercent, int countWidgets, int countCorrect) {
        this.userId = userId;
        this.correctPercent = correctPercent;
        this.countWidgets = countWidgets;
        this.countCorrect = countCorrect;
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

    public int getCountCorrect() {
        return countCorrect;
    }

    public void setCountCorrect(int countCorrect) {
        this.countCorrect = countCorrect;
    }
}
