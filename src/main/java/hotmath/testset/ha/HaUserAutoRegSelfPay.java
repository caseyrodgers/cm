package hotmath.testset.ha;

/** Defines an account that will trigger the creation
 *  of a new self pay account.
 *  
 * @author bob
 *
 */
public class HaUserAutoRegSelfPay extends HaUserAutoRegistration {

    public HaUserAutoRegSelfPay(){}
    
    public HaUserAutoRegSelfPay(Integer key) {
        super(key);
    }
    
    @Override
    public UserType getUserType() {
        return UserType.AUTO_REG_SELF_PAY;
    }
    
}
