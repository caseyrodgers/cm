package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.event.SolutionHasBeenLoadedEvent;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TutorWrapperPanel extends Composite {

    @UiField
    Element whiteboardButton;

    @UiField
    Element returnButton;
    
    @UiField
    Element buttonBar;
    
    
    @UiField
    Element debugInfo;

    boolean saveVariableContext;

    private TutorCallback tutorCallback;

    interface MyUiBinder extends UiBinder<Widget, TutorWrapperPanel> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public TutorWrapperPanel(boolean showButtonBar, boolean showReturnButton, boolean showWhiteboardButton, boolean saveVariableContext, TutorCallback tutorCallback) {

        this.tutorCallback = tutorCallback;
        this.saveVariableContext = saveVariableContext;
        initWidget(uiBinder.createAndBindUi(this));

        if(!showButtonBar) {
            buttonBar.addClassName("display_none");
        }
        if (!showWhiteboardButton) {
            whiteboardButton.addClassName("display_none");
        }
        if (!showReturnButton) {
            returnButton.addClassName("display_none");
        }
    }
    
    
    /** Set the tutor widget's value as defined 
     *  by each individual tutor widget type.  
     *  
     *  {type: 'string',value:'the value'};
     *  {type: 'object',value: {prop: 'test'}};
     * @param value
     */
    public void setTutorWidgetValue(String value) {
        jsni_setTutorWidgetValue(value);
    }

    public void loadSolution(final String pid, final String title, final boolean hasShowWork, final boolean shouldExpandSolution, final String jsonConfig,
            final CallbackAfterSolutionLoaded callback) {
        
        
        Log.debug("TutorWrapperPanel->loadSolution: " + pid);
        
        GetSolutionAction action = new GetSolutionAction(UserInfo.getInstance().getUid(), UserInfo.getInstance().getRunId(), pid);
        CmTutor.getCmService().execute(action, new AsyncCallback<SolutionInfo>() {
            @Override
            public void onSuccess(SolutionInfo result) {
                try {
                    
                    Log.debug("TutorWrapperPanel->Solution loaded: " + pid);
                    
                    _solutionInfo = result;
    
                    String variableContext = null;
                    if (_solutionInfo.getContext() != null) {
                        variableContext = _solutionInfo.getContext().getContextJson();
                    }
    
                    initializeTutor(TutorWrapperPanel.this, pid, jsonConfig, result.getJs(), result.getHtml(), title,
                            hasShowWork, shouldExpandSolution, variableContext);
                    callback.solutionLoaded(result);
                }
                finally {
                    callback.solutionLoaded(result);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error loading solution", caught);
                caught.printStackTrace();
                Window.alert(caught.getClass().getName() + ":" + caught.getMessage());
                callback.solutionLoaded(null);
            }
        });

    }
    
    static public interface CallbackAfterSolutionLoaded {
        void solutionLoaded(SolutionInfo solutionInfo);
    }


    /**
     * When the input widget has been updated
     * 
     * @param inputValue
     * @param correct
     */
    public void tutorWidgetComplete(String inputValue, boolean correct) {
        if (this.tutorCallback != null) {
            this.tutorCallback.tutorWidgetComplete(inputValue, correct);
        } else {
            Window.alert("tutorWidgetComplete not defined");
        }
    }

    
    /** Allow solution XML to loaded externally
     * 
     * @param info
     * @param instance
     * @param pid
     * @param jsonConfig
     * @param solutionDataJs
     * @param solutionHtml
     * @param title
     * @param hasShowWork
     * @param shouldExpandSolution
     * @param solutionContext
     */
    public void externallyLoadedTutor(SolutionInfo info, Widget instance, String pid, String jsonConfig, String solutionDataJs, String solutionHtml, String title, boolean hasShowWork,boolean shouldExpandSolution,String solutionContext) {
        _solutionInfo = info;
        initializeTutor(instance, pid, jsonConfig, solutionDataJs,solutionHtml, title, hasShowWork, shouldExpandSolution, solutionContext);
    }
    
    
    private void initializeTutor(Widget instance, String pid, String jsonConfig, String solutionDataJs, String solutionHtml, String title, boolean hasShowWork,boolean shouldExpandSolution,String solutionContext) {
        
        Log.debug("Solution loading: " + pid);
        initializeTutorNative(instance, pid, jsonConfig, solutionDataJs, solutionHtml, title, hasShowWork, shouldExpandSolution, solutionContext);
        
        debugInfo.setInnerHTML(pid);

        CmRpc.EVENT_BUS.fireEvent(new SolutionHasBeenLoadedEvent(_solutionInfo));
    }
    
    
    private native void jsni_setTutorWidgetValue(String value)/*-{
        $wnd.setWidgetValueFromJson(value);
    }-*/;
    
    
    
    /** initialize external tutor JS/HTML and provide glue between external JS
     * methods and GWT.
     * 
     */
    private native void initializeTutorNative(Widget instance, String pid, String jsonConfig, String solutionDataJs, String solutionHtml, String title, boolean hasShowWork,boolean shouldExpandSolution,String solutionContext) /*-{
    
        var that = this;
        $wnd.solutionSetComplete = function(numCorrect, limit) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::solutionSetComplete_Gwt(II)(numCorrect,limit);
        }
    
    
        if (typeof $wnd.TutorDynamic == 'undefined') {
           alert('TutorDynamic has not been defined');
           return;
        }
        
        if (typeof $wnd.TutorManager == 'undefined') {
           alert('TutorManager has not been defined');
           return;
        }
        
        $wnd.TutorDynamic.setSolutionTitle = function(probNum, total) {
            var that = this;
            $wnd.TutorDynamic.setSolutionTitle = 
            function() { 
                that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::setSolutionTitle_Gwt(II)(probNum,total); 
            }
        }
        
        
        $wnd.tutorWidgetCompleteAux = function(yesNo) {
           that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::tutorWidgetCompleteAux(Z)(yesNo);
        }
        
        
        $wnd.tutorWidgetComplete = function(inputValue,yesNo) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::tutorWidgetComplete(Ljava/lang/String;Z)(inputValue, yesNo);
        }
        
        $wnd.tutorInputWidgetComplete_gwt = function() {
             // silent 
        }
        
        $wnd.gwt_scrollToBottomOfScrollPanel = function(top) {
           $wnd.scrollTo(0,top);
        }
        
        $wnd.gwt_tutorNewProblem = function(problemNumber) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_tutorNewProblem(I)(problemNumber);
        }
        
        // called when the solution has been fully
        // initialized and ready to be interacted with 
        $wnd.solutionHasBeenViewed_Gwt = function(value) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::solutionHasBeenViewed_Gwt(Ljava/lang/String;)(value);
        }

        
        
       // called from CatchupMath.js event.tutorHasBeenInitialized
       // used to store current tutor context on server providing
       // a way to restore the tutor to its current var defs.
       //
       $wnd.gwt_solutionHasBeenInitialized = function() {
           try {
               var vars = $wnd._tutorData._variables;
               var pid = $wnd.TutorManager.pid;
               var probNum=0;
               var solutionVariablesJson = $wnd.getTutorVariableContextJson(vars);
               that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_solutionHasBeenInitialized(Ljava/lang/String;Ljava/lang/String;I)(solutionVariablesJson,pid,probNum);
       }
       catch(e) {
           alert('error saving solution context in JSNI: ' + e);}
       }
    
       $wnd.gwt_getSolutionProblemContext = function(probNum) {
           try {
               return that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_getSolutionProblemContext(I)(probNum);
           }
           catch(e) {
               alert(e)
           };
       }
       
       $wnd.TutorManager.initializeTutor(pid, jsonConfig, solutionDataJs,solutionHtml,title,hasShowWork,shouldExpandSolution,solutionContext);
   }-*/;    

    SolutionInfo _solutionInfo;
    Map<Integer, String> _variableContexts = new HashMap<Integer, String>();

    private String gwt_getSolutionProblemContext(int probNum) {
        return _solutionInfo.getContext().getContextJson();
    }

    private void gwt_solutionHasBeenInitialized(final String variablesJson, final String pid, final int problemNumber) {
        
        
        if(!saveVariableContext) {
            Log.debug("Not saving solution context for: " + pid);
            return;
        }
        
        /**
         * if this is already stored in variables, then no need to save on
         * server
         * 
         */
        if (variablesJson == null || variablesJson.length() < 100) {
            return;
        }

        if (_solutionInfo.getContext() != null) {
            /**
             * only store first one, each subsequent read on same prescription
             * (run_id) will have the existing context restored.
             */

        } else {
            Action<RpcData> action = tutorCallback.getSaveSolutionContextAction(variablesJson, pid, problemNumber);
            Log.debug("tutorCallback, save action, " +  action);
            if(action != null) {
                CmTutor.getCmService().execute(action, new AsyncCallback<RpcData>() {
                    @Override
                    public void onSuccess(RpcData result) {
                        _solutionInfo.setContext(new SolutionContext(pid, problemNumber, variablesJson));
                        System.out.println("Context saved");
                    }
    
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        System.out.println("Error saving solution context: " + caught);
                    }
                });
            }
        }
    }

    public static interface TutorCallback {
        /** When the NewProblem button is pressed 
         * 
         * @param problemNumber
         */
        void onNewProblem(int problemNumber);

        /** Return the action used to save this context, null if
         *  no context save should happen.
         *  
         * @param variablesJson
         * @param pid
         * @param problemNumber
         * @return
         */
        Action<RpcData>  getSaveSolutionContextAction(String variablesJson, String pid, int problemNumber);

        /** Called when the tutor widget input is complete
         * 
         * @param inputValue
         * @param correct
         */
        void tutorWidgetComplete(String inputValue, boolean correct);

        /** Call after the last step has been viewed in a solution
         * 
         * @param value
         */
        void solutionHasBeenViewed(String value);
    }
    
    
    protected void solutionHasBeenViewed_Gwt(String value) {
        this.tutorCallback.solutionHasBeenViewed(value);
    }

    /** Define by whiteboard/CM contract */
    protected void solutionSetComplete_Gwt(int numCorrect, int limit) {
    }

    protected void setSolutionTitle_Gwt(int probNum, int total) {
        Window.alert("Set Solution Title via GWT");

    }

    protected void tutorWidgetCompleteAux(boolean yesNo) {
    }

    protected void gwt_tutorNewProblem(int problemNumber) {
        if (this.tutorCallback != null) {
            tutorCallback.onNewProblem(problemNumber);
        } else {
            Window.alert("gwt_tutorNewProblem: no callback defined");
        }
    }
}
