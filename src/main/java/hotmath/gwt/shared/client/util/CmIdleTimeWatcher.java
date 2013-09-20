package hotmath.gwt.shared.client.util;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;

public class CmIdleTimeWatcher {

    private static CmIdleTimeWatcher __instance;

    public static CmIdleTimeWatcher getInstance() {
        if (__instance == null) {
            __instance = new CmIdleTimeWatcher();
        }
        return __instance;
    }

    static final int MAX_IDLE_TIME = 3600000;   // 1 HOUR
    static final int CHECK_IDLE_EVERY = 5000;

    long _lastKeyBoardActivity;
    Timer _timer;
    
    private CmIdleTimeWatcher() {
        _lastKeyBoardActivity = System.currentTimeMillis();
        _timer = new Timer() {
            
            @Override
            public void run() {
                checkIfIdle();
            }
        };
        _timer.schedule(CHECK_IDLE_EVERY);
    }

    boolean idle=false;
    private void checkIfIdle() {
        long timeNow = System.currentTimeMillis();
        long diffKeyboard = timeNow - _lastKeyBoardActivity;
        
        
        if(diffKeyboard > MAX_IDLE_TIME) {
            if(idle == false) {
                Log.info("Catchup Math is idle: " + diffKeyboard);
                idle=true;
            }
        }
        else {
            idle=false;
        }
        
        _timer.schedule(CHECK_IDLE_EVERY);
    }

    public void didKeyBoardActivity() {
        _lastKeyBoardActivity = System.currentTimeMillis();
    }

    public boolean isIdle() {
        return idle;
    }

}
