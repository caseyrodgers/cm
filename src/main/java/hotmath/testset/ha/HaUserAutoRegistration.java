package hotmath.testset.ha;

/** Defines an account that will trigger the creation
 *  of a new account.
 *  
 * @author casey
 *
 */
public class HaUserAutoRegistration extends HaBasicUserImpl {

    
    int uid;
    
    public HaUserAutoRegistration(){}
    
    public HaUserAutoRegistration(Integer key) {
        uid = key;
    }
    
    @Override
    public Object getUserObject() {
        return null;
    }

    @Override
    public UserType getUserType() {
        return UserType.AUTO_CREATE;
    }
    
    public void setUserKey(int key) {
        this.uid = key;
    }

    //@Override
    public int getUserKey() {
        return uid;
    }
}
