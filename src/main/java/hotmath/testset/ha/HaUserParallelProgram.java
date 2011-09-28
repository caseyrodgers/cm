package hotmath.testset.ha;

/** Defines an account that will provide access to a Parallel Program
 *  
 * @author bob
 *
 */
public class HaUserParallelProgram extends HaBasicUserImpl {

    
    int uid;
    
    public HaUserParallelProgram(){}
    
    public HaUserParallelProgram(Integer key) {
        uid = key;
    }
    
    @Override
    public Object getUserObject() {
        return null;
    }

    @Override
    public UserType getUserType() {
        return UserType.PARALLEL_PROGRAM;
    }
    
    public void setUserKey(int key) {
        this.uid = key;
    }

    @Override
    public int getUserKey() {
        return uid;
    }
}
