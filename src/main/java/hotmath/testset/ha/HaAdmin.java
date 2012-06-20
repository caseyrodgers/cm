package hotmath.testset.ha;

/** Represents an Catchup Math administrator 
 * 
 * @author casey
 *
 */
public class HaAdmin extends HaBasicUserImpl {

    int adminId;
    boolean freeAccount;
    
    public boolean isFreeAccount() {
        return freeAccount;
    }

    public void setFreeAccount(boolean freeAccount) {
        this.freeAccount = freeAccount;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public Object getUserObject() {
        return this;
    }

    public UserType getUserType() {
        return UserType.ADMIN;
    }

    @Override
    public int getUserKey() {
        return adminId;
    }


}
