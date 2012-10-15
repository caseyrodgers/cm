package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.shared.client.CmShared;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.widget.core.client.info.Info;


public class CmLogger  {
    
    static private CmLogger __instance;
    static public CmLogger getInstance() {
        if(__instance == null)
            __instance = new CmLogger();
        return __instance;
    }
    
    boolean isEnabled=false;
    CmLoggerWindow window;
    public CmLogger() { }
    
    
    public void enable(boolean yesNo) {
        isEnabled = yesNo;
    	if(!yesNo) {
    		window.clearLog();
    		window.close();
    		window = null;
    	}
    	else {
    		if(window == null) {
    			window = new CmLoggerWindow();
    			window.show();
    		}
    	}
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
    	if(window != null) {
    		window._info(msg);
    	}
    }
    
    private void _error(String msg, Throwable th) {
    	if(window != null) {
    		window._error(msg, th);
    	}
    }
    
    private void _debug(String msg) {
    	if(window != null) {
    		window._debug(msg);
    	}
    	else {
            if(window == null && CmShared.getQueryParameter("debug") != null) {
                Info.display("Debug", msg);
            }
    	}
    	
    	Log.debug(msg);
    }
}
