package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;

import com.google.gwt.user.client.Timer;

public class CmIdleTimeWatcher {

    private static CmIdleTimeWatcher __instance;

    public static CmIdleTimeWatcher getInstance() {
        if (__instance == null) {
            __instance = new CmIdleTimeWatcher();
        }
        return __instance;
    }

    static final int MAX_IDLE_TIME = 3600000;   // one HOUR
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
            idle=true;
            CmMessageBox.showAlert("System Is Idle", "System is idle: last activity=" + diffKeyboard, new CallbackOnComplete() {
                @Override
                public void isComplete() {
                    idle=false;
                    _timer.schedule(CHECK_IDLE_EVERY);
                }
            });
        }
        else {
            _timer.schedule(CHECK_IDLE_EVERY);
        }
    }

    public void didKeyBoardActivity() {
        _lastKeyBoardActivity = System.currentTimeMillis();
    }

    public boolean isIdle() {
        return idle;
    }

}