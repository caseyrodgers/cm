package hotmath.cm.lwl;

/** Represents the tutoring information about this student
 * 
 *  Contains information used to hook up the student with the 
 *  tutoring infrastructure.
 *  
 * @author casey
 *
 */
public class StudentTutoringInfo {
    String subscriberId;
    int lwlId;
    
    public int getLwlId() {
        return lwlId;
    }

    public void setLwlId(int lwlId) {
        this.lwlId = lwlId;
    }

    public StudentTutoringInfo(String subId) {
        this.subscriberId = subId;
    }
    
    public String getSubscriberId() {
        return subscriberId;
    }
    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }
}
