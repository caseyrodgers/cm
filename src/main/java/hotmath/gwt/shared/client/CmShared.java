package hotmath.gwt.shared.client;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.UserInfoBase.Mode;
import hotmath.gwt.cm_core.client.util.LoginInfoEmbedded;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CmPartner;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction.ResetType;
import hotmath.gwt.shared.client.util.CmAsyncCallback;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.CmExceptionLoginInvalid;
import hotmath.gwt.shared.client.util.CmLoggerWindow;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.StatusCodeException;

public class CmShared implements EntryPoint {
    static public String __loginName;

    @Override
    public void onModuleLoad() {
        
        
        if(CmCore.getQueryParameter("debugjs") != null) {
            CmLoggerWindow.getInstance().setVisible(true);
        }
        
        GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {

                if (CmCore.isDebug()) {
                    Window.alert("Uncaught Exception: " + e.toString());
                    e.printStackTrace();
                }

                try {
                    String nameAndTime = getClass().getName() + ": Uncaught exception: " + new Date();
                    /**
                    CmRpcCore.getCmService().execute(

                            new LogRetryActionFailedAction("uncaught exception", UserInfo.getInstance().getUid(), nameAndTime, null, CmShared
                                    .getStackTraceAsString(e)), new AsyncCallback<RpcData>() {
                                @Override
                                public void onSuccess(RpcData result) {
                                    CmLogger.info("Retry operation logged");
                                }

                                @Override
                                public void onFailure(Throwable exe) {
                                    if (isDebug())
                                        Window.alert("Error sending info about uncaught exception: " + exe);
                                }
                            });
                   */                            
                } catch (Exception x) {
                    CmLogger.error("Uncaught exception: " + x.getMessage(), x);
                }
            }
        });
    }

    
    
    /**
     * return string used to launch cm_student.
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
        EventBus.getInstance().addEventListener(new CmEventListener() {

            @Override
            public void handleEvent(CmEvent event) {
                if (event.getEventType() == EventType.EVENT_TYPE_MATHJAX_RENDER) {
                    /**
                     * Call JSNI routine to process any embedded MathML
                     * 
                     */
                    CmLogger.info("Processing MathML with MathJax");
                    // processMathJax();
                }
            }
        });
    }

    /**
     * Return the current browser User Agent
     * 
     * @return
     */
    public static String getBrowserInfo() {
        try {
            String userAgent = getUserAgent();
            String flashVersion = "Flash version: " + SWFObjectUtil.getPlayerVersion().toString();
            return userAgent + ", " + flashVersion;
        } catch (Throwable th) {
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
     * Verify login attempt by reading security key and making sure it Validated
     * only once ..
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
            // first see if run_id is passed, if so
            // the user is in 'view' mode and we must
            // inform the server not to update the
            // current state as the user moves around system.
            int userId = 0;

            // if (_queryParameters.get("uid") != null) {
            // userId = Integer.parseInt(_queryParameters.get("uid"));
            // }
            /**
             * for testing, if uid is passed allow override.
             */
            if (userId > 0) {
                callback.loginSuccessful(userId);
            } else {
                final String key2 = getSecurityKey();
                if (key2 == null || key2.length() == 0) {
                    throw new CmExceptionLoginInvalid("Invalid login: no security key found!");
                }

                boolean needToValidate = false;
                final LoginInfoEmbedded loginInfo = CmGwtUtils.getLoginInfo();
                // Cookies.getCookie("cm_key");
                // if no cookie, then we must validate
                if (loginInfo != null) {
                    userId = loginInfo.getUid();

                    if (loginInfo.getSecurityKeyVal() == null) {
                        throw new CmException("Invalid security key found");
                    }
                    if (key2.equals(loginInfo.getSecurityKeyVal())) {
                        UserInfoBase.getInstance().setUid(loginInfo.getUid());
                        needToValidate = false;
                    }
                    UserInfoBase.getInstance().setCmStartType(loginInfo.getStartType());

                    if (loginInfo.getPartner() != null && loginInfo.getPartner().length() > 1) {
                        if (loginInfo.getPartner().equals(CmPartner.LCOM.key)) {
                            UserInfoBase.getInstance().setPartner(CmPartner.LCOM);
                        } else {
                            CmLogger.error("Invalid partner setup for user: " + loginInfo.getPartner());
                        }
                    }
                    
                    
                    UserInfoBase.getInstance().setMobile(loginInfo.isMobile());
                    
                }

                UserInfoBase.getInstance().setEmail(loginInfo.getEmail());

                if (CmCore.getQueryParameter("mode") != null && CmCore.getQueryParameter("mode").equals("t")) {
                    UserInfoBase.getInstance().setMode(Mode.TEACHER_MODE);
                }

                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USER_LOGIN));

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


    static public String getSecurityKey() {
        String key = CmCore.getQueryParameter("key");
        if (key == null) {
            // see if key in JS variable
            key = getSecurityKeyFromExternalJs();
        }

        return key;
    }

    static private void displayLoginError(Exception exception) {
        exception.printStackTrace();
        CmLogger.error("Login error: ", exception);

        String msg = "We suggest you refresh this page by pressing the F5 function key.";
        if (CmCore.isDebug())
            msg += "<br/>" + exception.getMessage() + "";

        if (exception instanceof CmExceptionLoginInvalid) {
            /**
             * provide message, but force back to home page
             * 
             */
            CatchupMathTools.showAlert("Login Error", msg, new CmAsyncRequestImplDefault() {
                @Override
                public void requestComplete(String requestData) {
                    Window.Location.assign(CmShared.CM_HOME_URL);
                }
            });
        } else {
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
        } else {
            String hostPort = Window.Location.getPort();
            int port = (hostPort != null && hostPort.trim().length() > 0) ? Integer.parseInt(hostPort) : 80;
            // only append port number if > 80
            if (port > 80) {
                hostName += ":" + port;
            }
        }

        String url = "http://" + hostName;
        return url;
    }

    static {
        registerGlobalGwtJsMethods();
    }

    static public void gwt_debugLog(final String msg) {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                CmLoggerWindow.getInstance()._info(msg);
            }
        });        
        
    }
    
    static private native void registerGlobalGwtJsMethods() /*-{
        $wnd.resetProgram_Gwt = @hotmath.gwt.shared.client.CmShared::resetProgram_Gwt();
                                                     
        $wnd.gwt_debugLog = function(msg) {
             $wnd.console.log(msg);
             
             // @hotmath.gwt.shared.client.CmShared::gwt_debugLog(Ljava/lang/String;)(msg);
        }
        
    }-*/;

    /**
     * create JSNI to return KEY JS variable set during login
     * 
     * @return
     */
    static private native String getSecurityKeyFromExternalJs() /*-{
                                                                return $wnd.__securityKey;
                                                                }-*/;

    static public native String getHostName() /*-{
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
                CmServiceAsync s = CmRpcCore.getCmService();
                int altTest = 0;
                try {
                    altTest = Integer.parseInt(CmCore.getQueryParameter("alt_test"));
                    CmLogger.info("Resetting to alternate test " + altTest);
                } catch (Exception e) {
                    // quiet
                }

                s.execute(new ResetUserAction(ResetType.FULL, uid, altTest), new CmAsyncCallback<RpcData>() {
                    @Override
                    public void onSuccess(RpcData result) {
                        refreshPage();
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
        CmCore.reloadUser();
    }

    /**
     * Return stack trace as string, or null if throwable is null.
     * 
     * @param th
     * @return
     */
    static public String getStackTraceAsString(Throwable th) {
        if (th == null)
            return null;

        final StringBuilder result = new StringBuilder();
        if (th instanceof StatusCodeException)
            result.append("HTTP ERROR CODE: ").append(((StatusCodeException) th).getStatusCode()).append("\n");

        result.append(th.toString()).append("\n");

        for (StackTraceElement element : th.getStackTrace()) {
            result.append(element).append("\n");
        }
        return result.toString();
    }
}
