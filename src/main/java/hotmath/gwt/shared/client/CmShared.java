package hotmath.gwt.shared.client;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.ProcessLoginRequestAction;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.CmExceptionLoginInvalid;
import hotmath.gwt.shared.client.util.SystemVersionUpdateChecker;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;
import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

public class CmShared implements EntryPoint {
    
    static public String __loginName;
    

    // @Override
    public void onModuleLoad() {
    }

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
        if(server.indexOf("kattare") > -1)
            server = "http://hotmath.kattare.com";
        else if(server.indexOf("catchup") > -1)
            server = "http://hotmath.com";
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
    }
    
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
            if (_queryParameters.get("debug") != null) {
                if (_queryParameters.get("uid") != null) {
                    // for debugging .. perhaps this should not be allowed
                    // during normal processing.

                    // check for special case demo user
                    if (_queryParameters.get("uid").equals("demo")) {
                        userId = DEMO_UID;
                    } else {
                        userId = Integer.parseInt(_queryParameters.get("uid"));
                    }
                }
            }

            // for testing, if uid is passed allow it override cookie
            if (userId > 0) {
                callback.loginSuccessful(userId);
            } else {
                final String key2 = _queryParameters.get("key");
                if (key2 == null || key2.length() == 0) {
                    throw new CmExceptionLoginInvalid("Invalid login: no security key found on URL");
                }

                boolean needToValidate = true;
                final String cmKey = Cookies.getCookie("cm_key");
                // if no cookie, then we must validate
                if (cmKey != null) {
                    JSONValue jsonValue = JSONParser.parse(cmKey);
                    JSONObject o = jsonValue.isObject();
                    String keyVal = o.get("key").isString().stringValue();
                    if (keyVal == null) {
                        throw new CmException("Invalid security key found");
                    }
                    if (key2.equals(keyVal)) {
                        userId = (int) o.get("uid").isNumber().doubleValue();
                        needToValidate = false;
                    }
                }
                if (!needToValidate) {
                    UserInfoBase user = UserInfoBase.getInstance();
                    user.setUid(userId);
                    callback.loginSuccessful(userId);
                } else {
                    new RetryAction<UserInfo>() {
                        @Override
                        public void attempt() {
                            ProcessLoginRequestAction action = new ProcessLoginRequestAction(key2);
                            setAction(action);
                            CmShared.getCmService().execute(action,this);
                        }
                        
                        public void oncapture(UserInfo userInfo) {

                            __loginName = userInfo.getLoginName();
                            /**
                             * now store in cookie, so next refresh on this page
                             * can read info from cookie.
                             */
                            Cookies.setCookie("cm_key", "{key:'" + key2 + "',uid:" + userInfo.getUid() + "}");

                            /**
                             * Check Flash, if not supported show error .. but
                             * allow system to continue and initialize.
                             */
                            if (!SWFObjectUtil.isVersionIsValid(new PlayerVersion(CmShared.FLASH_MIN_VERSION))) {
                                new FlashVersionNotSupportedWindow();
                            }

                            /** Call back app waiting to be logged in ... */
                            callback.loginSuccessful(userInfo.getUid());

                        }
                    }.register();
                }
            }

        } catch (Exception e) {
            displayLoginError(e);
        }
    }

    static private void displayLoginError(Exception exception) {
        exception.printStackTrace();
        Log.info("Login error: " + exception.getMessage());
        

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
            CmWindow cm = new CmWindow();
            cm.setModal(true);
            cm.setHeading("Login Error");
            cm.setClosable(false);
            cm.setResizable(false);
            cm.add(new Html("<div style='padding: 10px;font-weight: bold'>" + msg + "</div>"));
            cm.setSize(250,100);
            cm.setVisible(true);
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

        if (hostName.indexOf("hotmath.com") > -1) {
            hostName = "catchupmath.com";
        } else if (hostName.equals("hotmath.kattare.com"))
            hostName = "hotmath.kattare.com:8081";
        else {
            String hostPort = Window.Location.getPort();
            if (!"80".equals(hostPort)) {
                hostName += ":" + hostPort;
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
    static public CmServiceAsync getCmService() {
        return (CmServiceAsync)Registry.get("cmService");
    }

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
}
