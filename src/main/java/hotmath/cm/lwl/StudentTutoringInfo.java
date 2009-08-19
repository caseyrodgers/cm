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
    Integer schoolNumber;
    Integer studentNumber;
    Integer accountType;

    public StudentTutoringInfo(String subId, Integer studentNumber, Integer schoolNumber, Integer accountType) {
        this.subscriberId = subId;
        this.studentNumber = studentNumber;
        this.schoolNumber = schoolNumber;
        this.accountType = accountType;
    }
    
    public String getSubscriberId() {
        return subscriberId;
    }
    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public Integer getSchoolNumber() {
        return schoolNumber;
    }

    public void setSchoolNumber(Integer schoolNumber) {
        this.schoolNumber = schoolNumber;
    }

    public Integer getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }
}
