package hotmath.gwt.cm_activity.client;

/** track the user's progression 
 * 
 * @author casey
 *
 */
public class UserScore {
    int tried;
    int total;
    int correct;
    
    public int getTried() {
        return tried;
    }
    public void setTried(int tried) {
        this.tried = tried;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public int getCorrect() {
        return correct;
    }
    public void setCorrect(int correct) {
        this.correct = correct;
    }
}
