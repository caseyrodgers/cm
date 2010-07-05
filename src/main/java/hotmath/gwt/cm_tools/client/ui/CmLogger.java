package hotmath.gwt.cm_tools.client.ui;

import java.util.ArrayList;
import java.util.List;

public class CmLogger  {
    
    static private CmLogger __instance;
    static public CmLogger getInstance() {
        if(__instance == null)
            __instance = new CmLogger();
        return __instance;
    }
    
    List<String> _messages = new ArrayList<String>();
    
    public CmLogger() {
    }
    
    boolean _isEnabled;
    public void enable(boolean yesNo) {
    	_isEnabled=yesNo;
    }
    static public void info(String msg) {
        getInstance()._info(msg);
    }
    static public void debug(String msg) {
        getInstance()._debug(msg);
    }
    static public void error(String msg,Throwable th) {
        getInstance()._error(msg, th);
    }
    static public void error(String msg) {
        getInstance()._error(msg, null);
    }

    private void _info(String msg) {
    	if(!_isEnabled) return;
        _messages.add(msg);
    }
    
    private void _error(String msg, Throwable th) {
    	if(!_isEnabled) return;    	
        _info(msg + "\n" + th.getStackTrace());
    }
    
    private void _debug(String msg) {
    	if(!_isEnabled) return;    	
        _info(msg);
    }
}
