package hotmath.cm.signup;

import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.PurchasePlan;
import hotmath.subscriber.service.HotMathSubscriberService;

public class HotmathSubscriberServiceCatchup extends HotMathSubscriberService {
    

    /** Add or re-use existing HaAdmin record for
     *  this new Catchup Math user
     */
    public void installService(HotMathSubscriber sub, PurchasePlan plan) {
        System.out.println("Creating new Catchup User");
    }
    
    @Override
    public String getUserName() {
        return "THE USERNAME";
    }
    
    
    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return "THE PASSWORD";
    }
}
