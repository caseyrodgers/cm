package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
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

    long _lastServerAccess;
    long _lastKeyBoardActivity;
    Timer _timer;
    
    private CmIdleTimeWatcher() {
        _timer = new Timer() {
            
            @Override
            public void run() {
                checkIfIdle();
            }
        };
        _timer.schedule(CHECK_IDLE_EVERY);
    }

    private void checkIfIdle() {
        long timeNow = System.currentTimeMillis();
        
        long diffServer = timeNow - _lastServerAccess;
        long diffKeyboard = timeNow - _lastKeyBoardActivity;
        
        
        if(diffServer > MAX_IDLE_TIME || diffKeyboard > MAX_IDLE_TIME) {
            CmMessageBox.showAlert("Info", "System is idle: server=" + diffServer + ", keyboard: " + diffKeyboard);
        }

        _timer.schedule(CHECK_IDLE_EVERY);
    }
    
    /**
     * Server activity has occurred
     * 
     */
    public void didServerAccess() {
        _lastServerAccess = System.currentTimeMillis();
    }

    public void didKeyBoardActivity() {
        _lastKeyBoardActivity = System.currentTimeMillis();
    }

}
