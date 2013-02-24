package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_mobile_shared.client.ui.TouchButton;
import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.event.ShowTutorWidgetCompleteInfoEvent;
import hotmath.gwt.cm_rpc.client.event.ShowTutorWidgetCompleteInfoHandler;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedHandler;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.event.SolutionHasBeenLoadedEvent;
import hotmath.gwt.cm_tutor.client.event.UserTutorWidgetStatusUpdatedEvent;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/** Provides a standard tutor viewer that handles the complications
 *  of providing a tutor viewer, such as:
 *  
 *  MathMl processing
 *  Next/Prev Step
 *  Tutor widget handling, event processing
 *  Button bar management (which buttons are active)
 * 
 * @author casey
 *
 */
public class TutorWrapperPanel extends Composite {

    @UiField
    TouchButton whiteboardButton;

    @UiField
    TouchButton returnButton;
    
    @UiField 
    TouchButton stepNext, stepPrev;
    
    @UiField
    Element buttonBar;
    
    @UiField
    DivElement readonlyMask;
    
    @UiField
    Button debugInfo;
    
    
    @UiField
    DivElement widgetCorrectInfo;

    boolean saveVariableContext;
    boolean _showCorrectInfo;

    private TutorCallback tutorCallback;

    interface MyUiBinder extends UiBinder<Widget, TutorWrapperPanel> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    static TutorWrapperPanel __lastInstance;
    
    boolean _wasWidgetAnswered;
    
    public TutorWrapperPanel(boolean showButtonBar, boolean showReturnButton, boolean showWhiteboardButton, boolean saveVariableContext, TutorCallback tutorCallback) {
        __lastInstance = this;
        this.tutorCallback = tutorCallback;
        this.saveVariableContext = saveVariableContext;
        initWidget(uiBinder.createAndBindUi(this));

        if(!showButtonBar) {
            buttonBar.addClassName("display_none");
        }
        if (!showWhiteboardButton) {
            whiteboardButton.getElement().addClassName("display_none");
        }
        else {
            whiteboardButton.addClickHandler(new ClickHandler() {
                @Override
                native public void onClick(ClickEvent event) /*-{
                   $wnd.TutorManager.showWhiteboard();
                }-*/;
            });
        }
        if (!showReturnButton) {
            returnButton.getElement().addClassName("display_none");
        }
        else {
            returnButton.addClickHandler(new ClickHandler() {
                @Override
                native public void onClick(ClickEvent event) /*-{
                    $wnd.TutorManager.newProblem();
                }-*/;
            });
        }
        
        stepNext.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveTutorNextStep(); 
            };
        });

        stepPrev.addClickHandler(new ClickHandler() {
            @Override
            native public void onClick(ClickEvent event) /*-{
                $wnd.TutorManager.showPreviousStep(); 
            }-*/;
        });
        
    }
    
    native private void showTutorMessage(String message) /*-{
        $wnd.TutorManager.showMessage(message);
    }-*/;
    
    
    /** Unlock the solution so user can move through without
     *  having to enter a value.
     *  
     */
    public void unlockSolution() {
        _wasWidgetAnswered = true;
        jsni_moveToFirstStep();
    }
    
    private void moveTutorNextStep() {
        if(isWidgetAndNotAnswered()) {
            showTutorMessage("Check your answer first");
            return;
        }
        else {
            jsniMoveTutorNextStep();
        }
    }
    
    
    private boolean isWidgetAndNotAnswered() {
        if(isThereIsATutorWidget()) {
            return  !_wasWidgetAnswered;
        }
        else {
            return false;
        }
    }
    
    native private void jsniMoveTutorNextStep() /*-{
         $wnd.TutorManager.showNextStep();
    }-*/;

    
    /** Set the tutor widget's value as defined 
     *  by each individual tutor widget type.  
     *  
     *  {type: 'string',value:'the value'};
     *  {type: 'object',value: {prop: 'test'}};
     * @param value
     */
    String _lastWidgetValue;
    public void setTutorWidgetValue(String value) {
        _lastWidgetValue = value;
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


    /**
     * When the input widget has been updated.
     * 
     * If tutor is in readonly mode, then disallow.
     * 
     * Inform callback of either case. 
     * 
     * @param inputValue
     * @param correct
     */
    public void tutorWidgetComplete(String inputValue, boolean correct) {
        Log.debug("tutorWidgetComplete (in GWT) called: " + inputValue);

        if(_readOnly) {
            jsni_setTutorWidgetValue(_lastWidgetValue);
            this.tutorCallback.tutorWidgetCompleteDenied(inputValue, correct);
            return;
        }
        else {
        	if(!correct  && tutorCallback.moveFirstHintOnWidgetIncorrect()) {
        	    jsni_moveToFirstStep();
        	}
        	
            if (this.tutorCallback != null) {
                
                /** Only save first widget value
                 * 
                 */
                if(!_wasWidgetAnswered) {
                    saveTutorWidgetAsComplete(tutorCallback.getSaveTutorWidgetCompleteAction(inputValue, correct));
                    _wasWidgetAnswered=true;
                }
                
                this.tutorCallback.tutorWidgetComplete(inputValue, correct);
                if(correct && tutorCallback.showTutorWidgetInfoOnCorrect()) {
                    // widgetCorrectInfo.setClassName("widget_correct_info_show");
                }
            } else {
                Window.alert("tutorWidgetComplete not defined");
            }
        }
    }
    
    
    private void saveTutorWidgetAsComplete(Action<UserTutorWidgetStats> action) {
        if(action != null) {
            CmTutor.getCmService().execute(action,new AsyncCallback<UserTutorWidgetStats>() {
                @Override
                public void onSuccess(UserTutorWidgetStats userStats) {
                    CmRpc.EVENT_BUS.fireEvent(new UserTutorWidgetStatusUpdatedEvent(userStats));
                }
                @Override
                public void onFailure(Throwable caught) {
                    Log.error("Error saving tutor widget value", caught);
                }
            });
        }
    }

    
    /** Move to the first step, if not not already viewed */
    private native void jsni_moveToFirstStep() /*-{
        if ($wnd.getCurrentStepUnitNumber() < 1) {
            $wnd.gotoStepUnit(0);
        }
    }-*/;
    
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
    
    
    
    private void initializeTutor(Widget instance, final String pid, String jsonConfig, String solutionDataJs, String solutionHtml, String title, boolean hasShowWork,boolean shouldExpandSolution,String solutionContext) {
        
        Log.debug("Solution loading: " + pid);
        
        _wasWidgetAnswered = false;
        widgetCorrectInfo.setClassName("widget_correct_info_hide");
        
        
        String submitButtonText = tutorCallback.getSubmitButtonText();
        boolean indicateWidgetStatus = tutorCallback.indicateWidgetStatus();
        
        initializeTutorNative(instance, pid, jsonConfig, solutionDataJs, solutionHtml, title, hasShowWork, shouldExpandSolution, solutionContext, submitButtonText, indicateWidgetStatus);

        debugInfo.setText(pid);
        debugInfo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open("/solution_editor/SolutionEditor.html?pid=" + pid.split("\\$")[0], "SolutionEditor", "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes");
            }
        });
        
        
        
        /** If there was an answer entered
         * 
         */
        if(_solutionInfo.getWidgetResult() != null && _solutionInfo.getWidgetResult().getValue() != null) {
            Log.debug("TutorWrapperPanel->Setting widget value: " + _solutionInfo.getWidgetResult() );
            // setTutorWidgetValue(result.getWidgetResult().getValue());
            jsni_moveToFirstStep();
            _wasWidgetAnswered = true;
        }
        
        CmRpc.EVENT_BUS.fireEvent(new SolutionHasBeenLoadedEvent(_solutionInfo));
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        /** Call the readonly mask only once the widget
         *  is inserted into DOM, otherwise the resize
         *  of the mask does not know the dimensions.
         */
        setupReadonlyMask(_readOnly);
    }
    private void setupReadonlyMask(boolean readOnly) {
        _readOnly=readOnly;
        Log.debug("Setting readonly mask: " + readOnly);
        if(readOnly) {
            int w = getWidget().getElement().getOffsetWidth();
            int h = getWidget().getElement().getOffsetHeight();
            Log.debug("Setting tutor readonly mask to size: " + w + ", " + h);
            
            readonlyMask.getStyle().setOpacity(.1);
            readonlyMask.getStyle().setWidth(w, Unit.PX);
            readonlyMask.getStyle().setHeight(h, Unit.PX);
            readonlyMask.getStyle().setDisplay(Display.BLOCK);
        }
        else {
            readonlyMask.getStyle().setDisplay(Display.NONE);
        }
    }
    
    
    private native void jsni_setTutorWidgetValue(String value)/*-{
        $wnd.TutorSolutionWidgetValues.setTutorWidgetValues(value);
    }-*/;
    
    
    private void gwt_tutorIsReadonlyMessage() {
        tutorCallback.tutorWidgetCompleteDenied(null, false);
    }
    
    private void gwt_enableTutorButton(String btnName, boolean enableYesNo) {
        if(true)
            return;
       
        
        if(btnName.equals("steps_next")) {
            stepNext.setEnabled(enableYesNo);
        }
        else if(btnName.equals("steps_prev")) {
            stepPrev.setEnabled(enableYesNo);
        }
        
       Log.debug("Showing btnName: " + btnName);
    }
    
    /** initialize external tutor JS/HTML and provide glue between external JS
     * methods and GWT.
     * 
     */
    private native void initializeTutorNative(Widget instance, String pid, String jsonConfig, String solutionDataJs, String solutionHtml, String title, boolean hasShowWork,boolean shouldExpandSolution,String solutionContext, String submitButtonText, boolean indicateWidgetStatus) /*-{
    
        
        var that = this;
        
        
        $wnd.gwt_enableTutorButton = function(btnName, enabledYesNo) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_enableTutorButton(Ljava/lang/String;Z)(btnName,enabledYesNo);
        }
        
        $wnd.solutionSetComplete = function(numCorrect, limit) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_solutionSetComplete(II)(numCorrect,limit);
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
            $wnd.TutorDynamic.setSolutionTitle = 
            function() { 
                that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_setSolutionTitle(II)(probNum,total); 
            }
        }
        
        $wnd.tutorWidgetCompleteAux = function(yesNo) {
           that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::tutorWidgetCompleteAux(Z)(yesNo);
        }
        
        
        $wnd.tutorWidgetComplete = function(inputValue,yesNo) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::tutorWidgetComplete(Ljava/lang/String;Z)(inputValue, yesNo);
        }
        
        $wnd.gwt_scrollToBottomOfScrollPanel = function(top) {
           $wnd.scrollTo(0,top);
        }
        
        $wnd.gwt_tutorNewProblem = function(problemNumber) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_tutorNewProblem(I)(problemNumber);
        }
        
        // called when the solution has been fully viewed, to the last step
        $wnd.gwt_solutionHasBeenViewed = function(value) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_solutionHasBeenViewed(Ljava/lang/String;)(value);
        }
        
        $wnd.gwt_showWhiteboard = function() {
//            var ua = navigator.userAgent;
//            if(ua.indexOf("4_2") > -1) {
//                alert("Whiteboard functionality does not work on older IPads.");
//                return;
//            }
//        
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_showWhiteBoard()();
        }
        
        
        $wnd.gwt_tutorIsReadonlyMessage = function() {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_tutorIsReadonlyMessage()();
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
       
       
       $wnd.gwt_tutorQuestionGuessChanged = function(id,selection,value) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_tutorQuestionGuessChanged(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(id,selection,value);
       }
       
       $wnd.TutorManager.initializeTutor(pid, jsonConfig, solutionDataJs,solutionHtml,title,hasShowWork,shouldExpandSolution,solutionContext,submitButtonText, indicateWidgetStatus);
   }-*/;    

    private void gwt_showWhiteBoard() {
        this.tutorCallback.showWhiteboard();
    }
    
    private void resizeTutor() {
        Log.debug("Resizing tutor");
        if(_readOnly) {
            setupReadonlyMask(_readOnly);
        }
    }
    
    
    private void gwt_tutorQuestionGuessChanged(String id, String selection, String correct) {
        boolean isCorrect = correct != null && correct.equalsIgnoreCase("true")?true:false;
        this.tutorCallback.tutorWidgetComplete(selection, isCorrect); 
    }
    
    SolutionInfo _solutionInfo;
    Map<Integer, String> _variableContexts = new HashMap<Integer, String>();

    private String gwt_getSolutionProblemContext(int probNum) {
        return _solutionInfo.getContext().getContextJson();
    }
    
    
    native private boolean isThereIsATutorWidget() /*-{
        return $wnd.TutorSolutionWidgetValues.getActiveWidget() != null;
    }-*/;
    
    private void setTutorBarStateEnabled(boolean enableYesNo) {
        if(true) {
            return;
        }
        
        
        if(!enableYesNo) {
            stepNext.setEnabled(false);
            stepPrev.setEnabled(false);
        }
    }
        
    private void gwt_solutionHasBeenInitialized(final String variablesJson, final String pid, final int problemNumber) {
        
        this.tutorCallback.solutionHasBeenInitialized();
        
        if(isThereIsATutorWidget()) {
            setTutorBarStateEnabled(false);
        }
        
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

        if (_solutionInfo.getContext() != null && _solutionInfo.getContext().getContextJson() != null) {
            /**
             * only store first one, each subsequent read on same prescription
             * (run_id) will have the existing context restored.
             */
            Log.debug("Context is already saved: " + pid);

        } else {
            Log.debug("Saving solution context: " + pid);
            Action<RpcData> action = tutorCallback.getSaveSolutionContextAction(variablesJson, _solutionInfo.getPid(), problemNumber);
            if(action != null) {    
                CmTutor.getCmService().execute(action, new AsyncCallback<RpcData>() {
                    @Override
                    public void onSuccess(RpcData result) {
                        _solutionInfo.setContext(new SolutionContext(pid, problemNumber, variablesJson));
                        Log.debug("Context saved");
                    }
    
                    @Override
                    public void onFailure(Throwable caught) {
                        Log.debug("Error saving solution context: " + caught, caught);
                    }
                });
            }
        }
    }

    
    protected void gwt_solutionHasBeenViewed(String value) {
        this.tutorCallback.solutionHasBeenViewed(value);
    }

    /** Define by whiteboard/CM contract */
    protected void gwt_solutionSetComplete(int numCorrect, int limit) {
    }

    protected void gwt_setSolutionTitle(int probNum, int total) {
        Window.alert("Set Solution Title via GWT");
    }

    protected void tutorWidgetCompleteAux(boolean yesNo) {
        Log.debug("Tutor widget correct: " + yesNo);
    }

    protected void gwt_tutorNewProblem(int problemNumber) {
        if (this.tutorCallback != null) {
            tutorCallback.onNewProblem(problemNumber);
        } else {
            Window.alert("gwt_tutorNewProblem: no callback defined");
        }
    }
    
    static public interface CallbackAfterSolutionLoaded {
        void solutionLoaded(SolutionInfo solutionInfo);
    }


    boolean _readOnly;
    public void setReadOnly(boolean b) {
        _readOnly = b;
    }
    
    static {
        CmRpc.EVENT_BUS.addHandler(WindowHasBeenResizedEvent.TYPE, new WindowHasBeenResizedHandler() {
            @Override
            public void onWindowResized(WindowHasBeenResizedEvent windowHasBeenResizedEvent) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (__lastInstance != null) {
                            __lastInstance.resizeTutor();
                        }
                    }
                });
            }
        });
        
        
        CmRpc.EVENT_BUS.addHandler(ShowTutorWidgetCompleteInfoEvent.TYPE, new ShowTutorWidgetCompleteInfoHandler() {
            @Override
            public void showTutorWidgetCompleteInfo() {
                __lastInstance.showTutorWidgetCompleteInfo();   
            }
        });
    }

    
    public static interface TutorCallback {
        /** When the NewProblem button is pressed 
         * 
         * @param problemNumber
         */
        void onNewProblem(int problemNumber);


        /** Return the text that is shown in the check widget save button
         * 
         * @return
         */
        String getSubmitButtonText();


        /** Return action that will define the tutor widget action save
         * 
         * @param value
         * @param yesNo
         * @return
         */
        Action<UserTutorWidgetStats> getSaveTutorWidgetCompleteAction(String value, boolean yesNo);
        

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
        
        /** Called if a widget value change was detected when 
         * such change is not allowed.
         */
        void tutorWidgetCompleteDenied(String inputValue, boolean correct);

        /** Call after the last step has been viewed in a solution
         * 
         * @param value
         */
        void solutionHasBeenViewed(String value);
        
        /** Show the associated whiteboard for this tutor
         * 
         */
        void showWhiteboard(); 
        
        
        /** Show the standard tutor widget info mesaage on the tutor be
         *  shown when the correct answer was entered?
         *  
         * @return
         */
        boolean showTutorWidgetInfoOnCorrect();
        
        /** Called when the solution has been fully initialized
         * 
         */
        void solutionHasBeenInitialized();
        
        
        /** Should the first hint be shown on incorrect widget value
         * 
         * @return
         */
        boolean moveFirstHintOnWidgetIncorrect();
        
        /** Should widget status of correct/incorrect be 
         *  shown to user?
         *  
         * @return
         */
        boolean indicateWidgetStatus();
    }


    protected void showTutorWidgetCompleteInfo() {
        Log.debug("showTutorWidgetCompleteInfo called");
        widgetCorrectInfo.addClassName(".show");    
    }

}
