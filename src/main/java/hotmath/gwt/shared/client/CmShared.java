package hotmath.gwt.shared.client;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CmPartner;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.CmExceptionLoginInvalid;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;
import hotmath.gwt.shared.client.util.SystemVersionUpdateChecker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class CmShared implements EntryPoint {
    
    static public String __loginName;

    static {
    	setupServices();
    }

    @Override
    public void onModuleLoad() { }

    static Map<String, String> _queryParameters = new HashMap<String, String>();

    /**
     * Return the parameter passed on query string
     * 
     * returns null if parameter not set
     * 
     * @param name
     * @return
     */
    static public String getQueryParameter(String name) {
        return _queryParameters.get(name);
    }
    
    static public void setQueryParameter(String name, String value) {
        _queryParameters.put(name, value);
    }

    static public void removeQueryParameter(String name) {
        _queryParameters.remove(name);
    }
    
    /** Return parameter for value or empty string if not set
     * 
     * @param name
     * @return
     */
    static public String getQueryParameterValue(String name) {
        String v = _queryParameters.get(name);
        return (v != null)?v:"";
    }

    /** return string used to launch cm_student.
     * 
     * This is complicated due to various deployment arrangements.
     * 
     * @return
     */
    static public String getServerForCmStudent() {
        String server = CmShared.CM_HOME_URL;
        return server;
    }

    /** The URL to default to if cannot log in */
    // final public static String CM_HOME_URL = "http://catchupmath.com";

    /**
     * @TODO: how to set dynamically?
     * 
     */
    final public static String CM_HOME_URL = getHostURL();

    /**
     * uid that corresponds to a demo account
     * 
     */
    final public static int DEMO_UID = 547;

    final public static int FLASH_MIN_VERSION = 8;

    final public static String FLASH_ALT_CONTENT = "<div id='flash-alt-content'>"
            + "<p>Flash version 8 or higher is required.  Either download Flash now, try a different browser, or try a different computer.  You may proceed, but many of our learning resources require Flash.</p> "
            + "<p> "
            + "<a href='http://www.adobe.com/go/getflashplayer'><img src='http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' /></a>"
            + "</p>" + "</div>";

    
    static {
        _queryParameters = readQueryString();
        
        EventBus.getInstance().addEventListener(new CmEventListener() {
            
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_MATHJAX_RENDER) {
                    /** Call JSNI routine to process any embedded MathML
                     * 
                     */
                    CmLogger.info("Processing MathML with MathJax");
                    //processMathJax();
                }
            }
        });
    }
    

    /** Return the current browser User Agent
     * 
     * @return
     */
    public static String getBrowserInfo() {
        try {
            String userAgent = getUserAgent();
            String flashVersion = "Flash version: " + SWFObjectUtil.getPlayerVersion().toString();
            return userAgent + ", " + flashVersion;
        }
        catch(Throwable th) {
            th.printStackTrace();
            return "Could not read browser info: " + th.getMessage();
        }
    }
    public static native String getUserAgent() /*-{
        return navigator.userAgent.toLowerCase();
    }-*/;

    static native void processMathJax() /*-{
        // defined in CatchupMath.js
        $wnd.processMathJax();
    }-*/; 
    
    /**
     * Verify login attempt by reading security key and making sure it
     * Validated only once ..
     * 
     * If cookie contains current key, assume already verified.
     * 
     * 
     * If error occurs, user is shown dialog and redirected back to home page.
     * 
     * @param callback
     * @throws CmException
     */
    static public void handleLoginProcessAsync(final CmLoginAsync callback) {
        try {
        	 SystemVersionUpdateChecker.monitorVersionChanges();
        	 
            /** Provide shortcut, single argument entry for debugging a user
             */
            if(_queryParameters.get("debug_uid") != null) {
                _queryParameters.put("debug", "true");
                _queryParameters.put("uid",_queryParameters.get("debug_uid"));
            }
            // first see if run_id is passed, if so
            // the user is in 'view' mode and we must
            // inform the server not to update the
            // current state as the user moves around system.
            int userId = 0;

//            if (_queryParameters.get("uid") != null) {
//            	userId = Integer.parseInt(_queryParameters.get("uid"));
//            }
            /** 
             *  for testing, if uid is passed allow override.
             */
            if (userId > 0) {
            	callback.loginSuccessful(userId);
            } else {
                final String key2 = getSecurityKey();
                if (key2 == null || key2.length() == 0) {
                    throw new CmExceptionLoginInvalid("Invalid login: no security key found!");
                }

                boolean needToValidate = false;
                final String cmJson = getLoginInfoFromExtenalJs(); 
                // Cookies.getCookie("cm_key");
                // if no cookie, then we must validate
                if (cmJson != null) {
                    JSONValue jsonValue = JSONParser.parse(cmJson);
                    JSONObject o = jsonValue.isObject();
                    String keyVal = o.get("key").isString().stringValue();
                    if (keyVal == null) {
                        throw new CmException("Invalid security key found");
                    }
                    if (key2.equals(keyVal)) {
                        userId = (int) o.get("userId").isNumber().doubleValue();
                        UserInfoBase.getInstance().setUid(userId);
                        needToValidate = false;
                    }
                    String cmStartType = o.get("type").isString().stringValue();
                    if(cmStartType != null && cmStartType.equals("AUTO_CREATE")) {
                    	UserInfoBase.getInstance().setCmStartType(cmStartType);
                    }

                    if(o.containsKey("partner")) {
                    	String partner = o.get("partner").isString().stringValue();
                    	if(partner != null && partner.length() > 1) {
                    		if(partner.equals(CmPartner.LCOM.key)) {
                    			UserInfoBase.getInstance().setPartner(CmPartner.LCOM);
                    		}
                    		else {
                    			CmLogger.error("Invalid partner setup for user: " + partner);
                    		}
                    	}
                    }
                    
                    String email = o.get("email").isString().stringValue();
                    UserInfoBase.getInstance().setEmail(email);
                    
            		
            		EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USER_LOGIN));

                }
                if (!needToValidate) {
                    callback.loginSuccessful(userId);
                } else {
                	throw new CmException("Invalid login operation!");
                }
            }

        } catch (Exception e) {
            displayLoginError(e);
        }
    }
    
    
    /** Reload the current user's page allowing any changed
     * program configuration to take effect.
     * 
     * 
     */
    static public void reloadUser() {
        int uid = UserInfoBase.getInstance().getUid();
        String url="/loginService?uid=" + uid;
        if(UserInfo.getInstance() == null) {
            url += "&type=ADMIN";
        }
        if(CmShared.getQueryParameter("debug") != null) {
            url += "&debug=true";
            
            if(UserInfo.getInstance().isAutoTestMode()) {
                url += "&type=auto_test";
            }
            
            if(CmShared.getQueryParameter("test_rpp_only") != null ) {
                url += "&test_rpp_only=true";
            }
        }
        Window.Location.assign(url);        
    }    
    
    
    static private String getSecurityKey() {
    	String key = _queryParameters.get("key");
    	if(key == null) {
    		// see if pass in JS variable
    		key = getSecurityKeyFromExtenalJs();
    	}
    	
    	return key;
    }

    static private void displayLoginError(Exception exception) {
        exception.printStackTrace();
        CmLogger.info("Login error: " + exception.getMessage());
        

        String msg = "We suggest you refresh this page by pressing the F5 function key.";
        if (_queryParameters.get("debug") != null)
            msg += "<br/>" + exception.getMessage() + "";

        if(exception instanceof CmExceptionLoginInvalid) {
            /** provide message, but force back to home page
             * 
             */
            CatchupMathTools.showAlert("Login Error", msg, new CmAsyncRequestImplDefault() {
                @Override
                public void requestComplete(String requestData) {
                    Window.Location.assign(CmShared.CM_HOME_URL);                    
                }
            });
        }
        else {
            /** show window that cannot be dismissed .. END OF LINE */
            Window.alert(msg);
        }
    }

   

    /**
     * Convert string+list to string+string of all URL parameters
     * 
     */
    static private Map<String, String> readQueryString() {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, List<String>> query = Window.Location.getParameterMap();
        for (String s : query.keySet()) {
            m.put(s, query.get(s).get(0));
        }
        Window.Location.getHostName();
        return m;
    }

    /**
     * This is the link we need to return to on logout.
     * 
     * If on hotmath.kattare.com, we need to go from port 80, to 8081 ..
     * 
     * This needs to parameterized.
     * 
     * @return
     */
    static private String getHostURL() {
        String hostName = Window.Location.getHostName();
        if (hostName.indexOf("hotmath.com") > -1) {
            hostName = "catchupmath.com";
        } 
        else {
            String hostPort = Window.Location.getPort();
            int port = (hostPort != null && hostPort.trim().length() > 0) ?
            		Integer.parseInt(hostPort) : 80;
            // only append port number if > 80
            if (port > 80) {
                hostName += ":" + port;
            }
        }
        
        String url = "http://" + hostName;
        return url;
    }
    
    
    /** 
     *  Get the single CmServiceAsync instance to all
     *  for sending RPC commands.
     *  
     * @return
     */
    static CmServiceAsync _serviceInstance;
    static public CmServiceAsync getCmService() {
        return _serviceInstance;
    }
    

    /**
     * Register any RPC services with the system
     * 
     */
    static private void setupServices() {
        CmLogger.info("CatchupMathTools: Setting up services");
        
        
        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";
        
        final CmServiceAsync cmService = (CmServiceAsync)GWT.create(CmService.class);
        ((ServiceDefTarget) cmService).setServiceEntryPoint(point + "services/cmService");
        _serviceInstance = cmService;
        
        
        registerGlobalGwtJsMethods();
    }
    
    static private native void registerGlobalGwtJsMethods() /*-{
        $wnd.resetProgram_Gwt = @hotmath.gwt.shared.client.CmShared::resetProgram_Gwt();
    }-*/;

    /** create JSNI to return KEY JS variable set during login
     * 
     * @return
     */
    static private native String getSecurityKeyFromExtenalJs() /*-{
        return $wnd.__securityKey;
    }-*/;
    
    
    /** return the generic loginInfo jsonized string from bootstrap html 
     * 
     * @return
     */
    static private native String getLoginInfoFromExtenalJs() /*-{
        var d = $doc.getElementById('login_info');
        return d.innerHTML;
    }-*/;
    
    
    
    
    static private native String getHostName() /*-{
                                               var host = window.location.host;
                                               if(host.indexOf("hotmath.com") > -1) {
                                               host = "http://catchupmath.com";
                                               }
                                               else if(host.indexOf("kattare.com") > -1) {
                                               host = "http://hotmath.kattare.com:8081";
                                               }
                                               else {
                                               host = "http://" + host;
                                               }
                                               return host;
                                               }-*/;
    
    /**
     * Reset the current user's path through CM
     * 
     */
    static public void resetProgram_Gwt() {
        resetProgram_Gwt(UserInfo.getInstance().getUid());
    }
    static public void resetProgram_Gwt(final int uid) {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                CmServiceAsync s = CmShared.getCmService();
                int altTest=0;
                try {
                    altTest = Integer.parseInt(CmShared.getQueryParameter("alt_test"));
                    CmLogger.info("Resetting to alternate test " + altTest);
                }
                catch(Exception e) {
                    //quiet
                }
                
                s.execute(new ResetUserAction(uid,altTest),
                        new AsyncCallback<RpcData>() {

                            @Override
                            public void onSuccess(RpcData result) {
                                refreshPage();
                            }

                            public void onFailure(Throwable caught) {
                                CatchupMathTools.showAlert(caught.getMessage());
                            }
                        });
            }
        });
    }    
    

    /**
     * Reload current page
     * 
     */
    static public void refreshPage() {
        CmShared.reloadUser();
    }    
}
