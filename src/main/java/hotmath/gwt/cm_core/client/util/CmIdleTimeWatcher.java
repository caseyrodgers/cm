package hotmath.gwt.cm_core.client.util;

import hotmath.gwt.cm_core.client.event.ForceSystemSyncCheckEvent;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

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

    static final int MAX_IDLE_TIME =  1000 * 60 * 10; // 10 minutes
    static final int CHECK_IDLE_EVERY = 5000; // 30000;
    final static int MAX_BUSY_TIME = 15; // minutes


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
        
        // start off active
        didKeyBoardActivity();
        
        _timer.schedule(CHECK_IDLE_EVERY);
    }

    boolean idle = true;

    static int NO_BASE_INDEX = -1;
    int _baseIndex = NO_BASE_INDEX;
    int _lastIndex = NO_BASE_INDEX;

    private void checkIfIdle() {

        //Log.debug("CmIdleTimeWatcher","check if idle (active minutes: " + getActiveMinutes(false) + ")");

        long timeNow = System.currentTimeMillis();
        long diffKeyboard = timeNow - _lastKeyBoardActivity;

        if (diffKeyboard > MAX_IDLE_TIME) {
            if (idle == false) {
                Log.debug("Catchup Math is idle: " + diffKeyboard);
                
                // force a flush of any pending time 
                // and then reset fully
                CmRpcCore.EVENT_BUS.fireEvent(new ForceSystemSyncCheckEvent(true, new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        Log.debug("ForceSystemSyncCheckEvent: complete ");
                    }
                }));
                
                idle = true;
                _baseIndex = NO_BASE_INDEX;
                _lastIndex = NO_BASE_INDEX;
            }
        }
        else {
            idle = false;
        }

        _timer.schedule(CHECK_IDLE_EVERY);
    }

    public void didKeyBoardActivity() {

        idle = false;

        _lastKeyBoardActivity = System.currentTimeMillis();

        @SuppressWarnings("deprecation")
        int min = new Date().getMinutes();

        if (_baseIndex == NO_BASE_INDEX) {
            _baseIndex = min;
        }
        _lastIndex = min;
        activeMinutes[min] = true;
        
        Log.debug("CmIdleTimeWatcher", "activity occurred at " + min);
    }
    
    
    /**
     * Determine how many minutes the user has been active.
     * 
     * This will be the interval between the first and last activity minute.
     * 
     * 
     * Never return any value > MAX_BUSY_TIME
     * 
     * 
     * @return
     */
    public int getActiveMinutes(boolean doSetToIdle) {
        if (_baseIndex == NO_BASE_INDEX || _lastIndex == NO_BASE_INDEX) {
            return 0;
        }
        
        /**
         * now determine interval distance
         * 
         * 'wrap' around minutes if interval crosses hour boundary.
         * 
         * */
        int interval;
        if (_baseIndex > _lastIndex) {
            interval = (activeMinutes.length - _baseIndex) + _lastIndex;
        }
        else if(_baseIndex == _lastIndex) {
            interval = 0;
        }
        else {
            interval = _lastIndex - _baseIndex;
        }
        
        if(doSetToIdle) {
            setToIdle();
        }
        
        return (interval<MAX_BUSY_TIME)?interval:MAX_BUSY_TIME;
    }

    public void setToIdle() {

        Log.debug("CmIdleTimeWatcher", "set to idle");

        for (int i = 0; i < activeMinutes.length; i++) {
            activeMinutes[i] = false;
        }
        _baseIndex = _lastIndex;
        _lastIndex = NO_BASE_INDEX;
        idle = true;
    }

    public boolean isIdle() {
        return idle;
    }
    
    public void reset() {
        _baseIndex = NO_BASE_INDEX;
        _lastIndex = NO_BASE_INDEX;
        
        activeMinutes = new boolean[60];
    }

}
