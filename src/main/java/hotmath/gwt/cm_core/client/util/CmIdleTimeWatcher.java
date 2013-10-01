package hotmath.gwt.cm_core.client.util;

import java.util.Date;

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
    boolean activeMinutes[] = new boolean[60];
    
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

    boolean idle=true;
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
        idle = false;
        
        _lastKeyBoardActivity = System.currentTimeMillis();
        
        @SuppressWarnings("deprecation")
        int min = new Date().getMinutes();
        activeMinutes[min] = true;
    }
    
    public int getActiveMinutes() {
        try {
            int low=-1,high=-1;
            // find low number
            for(int i=0;i<activeMinutes.length;i++) {
                if(activeMinutes[i]) {
                    low=i;
                    break;
                }
            }
            if(low==-1) {
                Log.warn("Idle low value not found!");
                return 0; // not busy
            }
            else {
                // find high number
                for(int i=activeMinutes.length;i >-1;i--) {
                    if(activeMinutes[i-1]) {
                        high=i;
                        break;
                    }
                }
                return (high-low)+1;
            }
        }
        finally {
            // reset
            activeMinutes = new boolean[activeMinutes.length];
            idle = true;
        }
    }

    public boolean isIdle() {
        return idle;
    }

}
