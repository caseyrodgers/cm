package hotmath.gwt.cm_core.client;

import hotmath.gwt.cm_core.client.util.LoginInfoEmbedded;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserInfo.AccountType;
import hotmath.gwt.cm_rpc.client.UserInfo.UserProgramCompletionAction;
import hotmath.gwt.cm_rpc.client.rpc.CmDestination;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

public class CmGwtUtils {

    static Map<String, String> _queryParameters = null;
    static {
        _queryParameters = CmGwtUtils.readQueryString();
    }

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

    /**
     * Return parameter for value or empty string if not set
     * 
     * @param name
     * @return
     */
    static public String getQueryParameterValue(String name) {
        String v = _queryParameters.get(name);
        return (v != null) ? v : "";
    }

    /**
     * Convert string+list to string+string of all URL parameters
     * 
     */
    static public Map<String, String> readQueryString() {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, List<String>> query = Window.Location.getParameterMap();
        for (String s : query.keySet()) {
            m.put(s, query.get(s).get(0));
        }
        Window.Location.getHostName();
        return m;
    }

    static native public String getUserAgent() /*-{
                                               if(typeof navigator !== 'undefined') {
                                               return navigator.userAgent;
                                               }
                                               else {
                                               return 'unknown';
                                               }
                                               }-*/;

    static native public void scrollWindowTo(int position) /*-{
                                                           try {
                                                           setTimeout(function() {
                                                           $wnd.scrollTo(0,position);
                                                           },0);
                                                           }
                                                           catch(e) {
                                                           alert(e);
                                                           }
                                                           }-*/;

    /**
     * return the user_info data passed in the login bootstrap html
     * 
     * @return
     */
    static public native String getUserInfoFromExtenalJs() /*-{
                                                            var d = $doc.getElementById('user_info');
                                                            return d.innerHTML;
                                                            }-*/;

    public static UserInfo extractUserFromJsonString(String json) {
        Log.debug("UserInfo JSON: " + json);

        JSONValue loginInfo = JSONParser.parseStrict(json);

        JSONObject o = loginInfo.isObject().get("userInfo").isObject();
        JSONObject nextAction = loginInfo.isObject().get("nextAction").isObject();

        UserInfo ui = new UserInfo();
        ui.setUid(getJsonInt(o.get("uid")));
        ui.setUserName(getJsonString(o.get("userName")));
        ui.setAutoTestMode(o.get("autoTestMode").isBoolean().booleanValue());
        ui.setBackgroundStyle(getJsonString(o.get("backgroundStyle")));
        ui.setCorrectPercent(getJsonInt(o.get("correctPercent")));
        ui.setCustomProgram(o.get("customProgram").isBoolean().booleanValue());
        ui.setDemoUser(o.get("demoUser").isBoolean().booleanValue());
        ui.setFirstView(o.get("firstView").isBoolean().booleanValue());
        ui.setLimitGames(o.get("limitGames").isBoolean().booleanValue());
        ui.setDisableCalcAlways(o.get("disableCalcAlways").isBoolean().booleanValue());
        ui.setDisableCalcQuizzes(o.get("disableCalcQuizzes").isBoolean().booleanValue());
        ui.setLoginName(getJsonString(o.get("loginName")));
        ui.setPassPercentRequired(getJsonInt(o.get("passPercentRequired")));
        ui.setPassword(getJsonString(o.get("password")));
        ui.setRunId(getJsonInt(o.get("runId")));
        ui.setSessionCount(getJsonInt(o.get("sessionCount")));
        ui.setSessionNumber(getJsonInt(o.get("sessionNumber")));
        ui.setShowWorkRequired(o.get("showWorkRequired").isBoolean().booleanValue());
        ui.setSubTitle(getJsonString(o.get("subTitle")));
        ui.setTestId(getJsonInt(o.get("testId")));
        ui.setProgramName(getJsonString(o.get("testName")));
        ui.setProgramSegment(getJsonInt(o.get("testSegment")));
        ui.setProgramSegmentCount(getJsonInt(o.get("programSegmentCount")));
        ui.setTestSegmentSlot(getJsonInt(o.get("testSegmentSlot")));
        ui.setTutoringAvail(o.get("tutoringAvail").isBoolean().booleanValue());
        ui.setViewCount(getJsonInt(o.get("viewCount")));

        String placeVal = null;
        if (nextAction.get("place").isString() != null) {
            placeVal = nextAction.get("place").isString().stringValue();
        }
        String place = (placeVal != null && !placeVal.equals("null")) ? placeVal : "PRESCRIPTION";
        ui.setFirstDestination(new CmDestination(place));

        /**
         * Extact the Tutor Widget Stats If not available, create default with
         * no stats
         */
        JSONValue widgetStatsJson = o.get("tutorInputWidgetStats");
        UserTutorWidgetStats widgetStats = null;
        if (widgetStatsJson != null && widgetStatsJson.isObject() != null) {
            int countWidgets = (int) widgetStatsJson.isObject().get("countWidgets").isNumber().doubleValue();
            int percentCorrect = (int) widgetStatsJson.isObject().get("correctPercent").isNumber().doubleValue();
            int countCorrect = (int) widgetStatsJson.isObject().get("countCorrect").isNumber().doubleValue();
            widgetStats = new UserTutorWidgetStats(ui.getUid(), percentCorrect, countWidgets, countCorrect);
        } else {
            widgetStats = new UserTutorWidgetStats(ui.getUid(), 0, 0, 0);
        }
        ui.setTutorInputWidgetStats(widgetStats);

        String accountType = getJsonString(o.get("userAccountType"));
        if (accountType.equals("SCHOOL_USER")) {
            ui.setUserAccountType(AccountType.SCHOOL_TEACHER);
        } else {
            ui.setUserAccountType(AccountType.PARENT_STUDENT);
        }

        String onCompletion = getJsonString(o.get("onCompletion"));
        if (onCompletion.equals("AUTO_ADVANCE")) {
            ui.setOnCompletion(UserProgramCompletionAction.AUTO_ADVANCE);
        } else {
            ui.setOnCompletion(UserProgramCompletionAction.STOP);
        }

        // if run_id passed in, then allow user to view_only
        if (CmGwtUtils.getQueryParameter("run_id") != null) {
            int runId = Integer.parseInt(CmGwtUtils.getQueryParameter("run_id"));
            // setup user to masquerade as real user
            ui.setRunId(runId);
            ui.setActiveUser(false);
        } else {
            ui.setActiveUser(true);
        }
        Log.debug("UserInfo object set to: " + ui);

        return ui;
    }

    /**
     * return the generic loginInfo jsonized string from bootstrap html
     * 
     * @return
     */
    static private native String getLoginInfoFromExtenalJs() /*-{
                                                             var d = $doc.getElementById('login_info');
                                                             return d.innerHTML;
                                                             }-*/;

    static private String getJsonString(JSONValue o) {
        return o.isString() != null ? o.isString().stringValue() : null;
    }

    static private int getJsonInt(JSONValue o) {
        return (int) (o.isNumber() != null ? o.isNumber().doubleValue() : 0);
    }

    /**
     * extract and store the externalized json login_info created by
     * LoginService
     * 
     * @return
     */
    public static LoginInfoEmbedded getLoginInfo() {
        String cmJson = getLoginInfoFromExtenalJs();
        JSONValue jsonValue = JSONParser.parse(cmJson);
        JSONObject o = jsonValue.isObject();
        String keyVal = o.get("key").isString().stringValue();
        int userId = (int) o.get("userId").isNumber().doubleValue();
        String cmStartType = o.get("type").isString().stringValue();
        String partner = null;
        if (o.containsKey("partner")) {
            partner = o.get("partner").isString().stringValue();
        }
        String email = o.get("email").isString().stringValue();

        boolean isMobile = false;
        if (o.containsKey("is_mobile")) {
            isMobile = o.get("is_mobile").isBoolean().booleanValue();
        }

        return new LoginInfoEmbedded(keyVal, userId, cmStartType, partner, email, isMobile);
    }

    /**
     * Use the external Hammer.js library to watch for doubletap Close the
     * whiteboard once detected.
     * 
     * @param instance
     * @param element
     */
    static native public void addDoubleTapRemoveEvent(Element element) /*-{
                                                                       var that = this;
                                                                       $wnd.Hammer(element).on("doubletap", function(event) {
                                                                       that.@hotmath.gwt.cm_mobile_shared.client.view.AssignmentProblemViewImpl::hideWhiteboard()();
                                                                       });
                                                                       
                                                                       //$wnd.Hammer(element).on("pinch", function(event) {
                                                                       //that.@hotmath.gwt.cm_mobile_shared.client.view.AssignmentProblemViewImpl::hideWhiteboard()();
                                                                       //});

                                                                       }-*/;

    public static void moveToTopOfViewableScreen(Element widget, Element container) {
        int width = container.getParentElement().getClientWidth();
        int height = container.getParentElement().getClientWidth();
        int top = CatchupMathMobileShared.getScrollHeight();
        if(top < 35) {
            top = 35; // move  past header
        }
        String styles = "width: " + width + ";height: " + height + ";top: " + top;
        widget.setAttribute("style", styles);
    }
    

    /** Resize the whiteboard to fully fit the viewable area
     * 
     * @param tutorElement
     */
    static public void resizeElement(Element element) {
        int width = element.getParentElement().getClientWidth();
        int height = element.getParentElement().getClientWidth();

        String styles = "width: " + width + ";height: " + height;
        element.setAttribute("style", styles);
    }


    
    


    /** Search for a quiz question and scroll it to the top
     *  of viewable area.
     *  
     * @param pid
     */
    static native public void jsni_positionQuestionToTopOfViewable(String pid) /*-{
        var questions = $wnd.$('.question_div');
        questions.each(function(index) {
            var pp = $wnd.$( this ).attr('guid');
            if(pid == pp) {
                var el = this;
                var top = $wnd.DL_GetElementTop(el)
                $wnd.scrollTo(1, top - 70);
            } 
        });
    }-*/;
    
    
    static public native void printWindow() /*-{
        $wnd.print();
    }-*/;
    
    
    /** White to a whiteboard object.
     * 
     *  If whiteboard is null, then the last created whitebaord
     *  which is stored in _theWhiteboard is used.  Otherwise,
     *  we update the whiteboard 'whiteboardId'.
     *  
     * @param whiteboardId
     * @param command
     * @param commandData
     */
    static public native void jsni_updateWhiteboardAux(String whiteboardId, String command, String commandData) /*-{
    
        var theWhiteboard = whiteboardId == null?$wnd._theWhiteboard: $wnd._cmWhiteboards[whiteboardId];
        if(!theWhiteboard) {
            alert('whiteboard ' + whiteboardId + ' cannot be found in jsni_updateWhiteboardAux');
            return;
        }
        
        var cmdArray = [];
        if (command == 'draw') {
            cmdArray = [['draw', [commandData]]];
        } else if (command == 'clear') {
            cmdArray = [['clear', []]];                                                                                                  
        }
    
    
        var realArray = [];
        for (var i = 0, t = cmdArray.length; i < t; i++) {
            var ele = [];
            ele[0] = cmdArray[i][0];
            ele[1] = cmdArray[i][1];
            realArray[i] = ele;
        }
        
        theWhiteboard.updateWhiteboard(realArray);
    }-*/;
    

    /** Called from JSNI to insert into main log
     * 
     * @param message
     */
    static public void gwt_log(String message) {
        Log.info("JSNI: " + message);
    }
    
    
}
