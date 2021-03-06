package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_mobile_shared.client.ui.TouchButton;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.event.ShowTutorWidgetCompleteInfoEvent;
import hotmath.gwt.cm_rpc.client.event.ShowTutorWidgetCompleteInfoHandler;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedHandler;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc_assignments.client.model.ProblemStatus;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.event.SolutionHasBeenLoadedEvent;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCompleteEvent;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCorrectEvent;
import hotmath.gwt.cm_tutor.client.event.UserTutorWidgetStatusUpdatedEvent;
import hotmath.gwt.cm_tutor.client.view.TutorCallback.WidgetStatusIndication;

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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
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
    Element problemStatus;

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

    public TutorWrapperPanel(boolean showButtonBar, boolean showReturnButton, boolean showWhiteboardButton, boolean saveVariableContext, TutorCallback tutorDefine) {
        __lastInstance = this;
        this.tutorCallback = tutorDefine;
        this.saveVariableContext = saveVariableContext;
        initWidget(uiBinder.createAndBindUi(this));

        /** Turn on debugging CSS */
        if (CmCore.getQueryParameter("debug") != null) {
            addStyleName("debug-mode");
        }

        if(!showButtonBar) {
            buttonBar.addClassName("display_none");
        }
        if (!showWhiteboardButton) {
            whiteboardButton.setVisible(false);
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
            returnButton.setVisible(false);
        }
        else {
            
            if(tutorCallback.getTutorReturnButtonAction() != null) {
                returnButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if(tutorCallback.getTutorReturnButtonAction().goBack()) {
                            History.back();
                        }
                    }
                });    
            }
            else {
                returnButton.addClickHandler(new ClickHandler() {
                    @Override
                    native public void onClick(ClickEvent event) /*-{
                        $wnd.TutorManager.newProblem();
                    }-*/;
                });
            }
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

        addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                jsni_setActiveTutorWrapper(TutorWrapperPanel.this.tutorWrapperId);
            }
        }, MouseDownEvent.getType());
    }

    /** Unregister this TutorWrapperPanel with 
     * the external js TutorManager in (tutor_tablet.js)
     */
    public void unregisterTutorWrapper() {
        jsni_unregisterTutorWrapper(this.tutorWrapperId);
    }

    
    native void jsni_unregisterTutorWrapper(int id) /*-{
        $wnd.TutorManager.unregisterTutorWrapper(id);
    }-*/;

	private native void jsni_setActiveTutorWrapper(int id) /*-{
        $wnd.TutorManager.setActiveTutorWrapper(id);
    }-*/;

    /** Provide button bar customization
     *
     * @param setup
     */
    public void setupButtonBar(ButtonBarSetup setup) {
        showElement(stepNext.getElement(), setup.showNextPrev);
        showElement(stepPrev.getElement(), setup.showNextPrev);
        showElement(whiteboardButton.getElement(), setup.showWhiteboard);
        showElement(returnButton.getElement(), setup.showReturn);

        /** Override the RETURN button label */
        if(setup.returnButtonText != null) {
            String sexyTmp = "<span><span>" + setup.returnButtonText + "</span></span>";
            returnButton.getElement().setInnerHTML(sexyTmp);
        }
    }

    private void showElement(Element element, boolean b) {
        if(b) {
            element.removeClassName("display_none");
        }
        else {
            element.addClassName("display_none");
        }
    }

    native public void showTutorMessage(String message) /*-{
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

    /** the dom node where this tutor is installed
     * 
     */
	private com.google.gwt.user.client.Element tutorDomNode;

	private int tutorWrapperId;
    public void setTutorWidgetValue(String value) {
        _lastWidgetValue = value;
        jsni_setTutorWidgetValue(value);

        _wasWidgetAnswered=true;
    }

    public void loadSolution(final String pid, final String title, final boolean hasShowWork, final boolean shouldExpandSolution, final String jsonConfig,
            final CallbackAfterSolutionLoaded callback) {
        Log.debug("TutorWrapperPanel->loadSolution: " + pid);

        GetSolutionAction action = new GetSolutionAction(UserInfo.getInstance().getUid(), UserInfo.getInstance().getRunId(), pid);
        CmRpcCore.getCmService().execute(action, new AsyncCallback<SolutionInfo>() {
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

        Log.debug("tutorWidgetComplete (in GWT) called with value: " + inputValue);

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
                    Action<UserTutorWidgetStats> tc = tutorCallback.getSaveTutorWidgetCompleteAction(inputValue, correct);
                    if(tc != null) {
                        saveTutorWidgetAsComplete(tutorCallback.getSaveTutorWidgetCompleteAction(inputValue, correct));
                    }
                    _wasWidgetAnswered=true;
                    if(correct) {
                        CmRpcCore.EVENT_BUS.fireEvent(new TutorWidgetInputCorrectEvent(true));
                    }
                }

                this.tutorCallback.tutorWidgetComplete(inputValue, correct);
                if(correct && tutorCallback.showTutorWidgetInfoOnCorrect()) {
                    // widgetCorrectInfo.setClassName("widget_correct_info_show");
                }
            } else {
                Window.alert("tutorWidgetComplete not defined");
            }

            CmRpcCore.EVENT_BUS.fireEvent(new TutorWidgetInputCompleteEvent(_solutionInfo.getPid(), inputValue, correct));
        }
    }


    private void saveTutorWidgetAsComplete(Action<UserTutorWidgetStats> action) {
        if(action != null) {
            CmRpcCore.getCmService().execute(action,new AsyncCallback<UserTutorWidgetStats>() {
                @Override
                public void onSuccess(UserTutorWidgetStats userStats) {
                    CmRpcCore.EVENT_BUS.fireEvent(new UserTutorWidgetStatusUpdatedEvent(userStats));
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
     * @Param whiteboardText is text shown on whiteboard only problems.
     */
    public void externallyLoadedTutor(SolutionInfo info, Widget instance, String jsonConfig, String title, boolean hasShowWork,boolean shouldExpandSolution,String solutionContext) {
        _solutionInfo = info;
        initializeTutor(instance, info.getPid(), jsonConfig, info.getJs(),info.getHtml(), title, hasShowWork, shouldExpandSolution, solutionContext);
    }


    public Element getTutorDomNode() {
    	return this.tutorDomNode;
    }

    private void initializeTutor(Widget instance, final String pid, String jsonConfig, String solutionDataJs, String solutionHtml, String title, 
            boolean hasShowWork,boolean shouldExpandSolution,String solutionContext) {

        Log.debug("Solution loading: " + pid);
                _wasWidgetAnswered = false;
        widgetCorrectInfo.setClassName("widget_correct_info_hide");
        setProblemStatusControl("");

        String submitButtonText = tutorCallback.getSubmitButtonText();
        WidgetStatusIndication indicateWidgetStatus = tutorCallback.indicateWidgetStatus();

        boolean installCustomSteps = tutorCallback.installCustomSteps();


        if(CmCore.getQueryParameterValue("debug_problem").equals("true")) {
            tutorCallback.debugLogOut("Problem Debug", "Enabling problem debug");
            enableTutorDebugMode(true);
        }

        this.tutorDomNode = instance.getElement();

        String whiteboardText = tutorCallback.getWhiteboardText();
        try {
            jsni_initializeTutorNative(this.tutorDomNode, pid, jsonConfig, solutionDataJs, solutionHtml, title, hasShowWork, shouldExpandSolution, solutionContext,
                    submitButtonText, indicateWidgetStatus.name(), installCustomSteps, whiteboardText);
        }
        catch(Exception e) {
        	Window.alert("Error initializing tutor: " + e.getMessage());
        }

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


        processStaticWhiteboards();

        CmRpcCore.EVENT_BUS.fireEvent(new SolutionHasBeenLoadedEvent(_solutionInfo));
    }

    /** Enable or disable the tutor loading/problem set debug mode
     *
     * @param trueFalse
     */
    private native void enableTutorDebugMode(boolean trueFalse) /*-{
        $wnd.console.log('Enabling problemDebug mode');
        $wnd.Flashcard_mngr.debugMode=trueFalse;
    }-*/;

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
        $wnd.TutorSolutionWidgetValues.setTutorWidgetValues(value, false);
    }-*/;


    private void gwt_tutorIsReadonlyMessage() {
        tutorCallback.tutorWidgetCompleteDenied(null, false);
    }

    private void gwt_scrollToBottomOfScrollPanel() {
    	tutorCallback.scrollToBottomOfScrollPanel();
    }

    private void gwt_setTutorWrapperId(int id) {
    	this.tutorWrapperId = id;
    }


    /** initialize external tutor JS/HTML 
     * and provide glue between external JS methods and GWT.
     * 
     * Note: all methods need access to 'this'.
     *
     */
    private native void jsni_initializeTutorNative(Element instance, String pid, String jsonConfig,
            String solutionDataJs, String solutionHtml, String title, boolean hasShowWork,
            boolean shouldExpandSolution,String solutionContext, String submitButtonText, 
            String indicateWidgetStatus, boolean installCustomSteps, String whiteboardText) /*-{


        // $wnd.debug =  @hotmath.gwt.cm_core.client.CmGwtUtils::gwt_log(Ljava/lang/String;);
        
        var that=this;
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
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_scrollToBottomOfScrollPanel()();
        }

        $wnd.gwt_tutorNewProblem = function(problemNumber) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_tutorNewProblem(I)(problemNumber);
        }

        // called when the solution has been fully viewed, to the last step
        $wnd.gwt_solutionHasBeenViewed = function(value) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_solutionHasBeenViewed(Ljava/lang/String;)(value);
        }

        $wnd.gwt_showWhiteboard = function() {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_showWhiteBoard()();
        }

        $wnd.gwt_tutorIsReadonlyMessage = function() {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_tutorIsReadonlyMessage()();
        }

       // called from CatchupMath.js event.tutorHasBeenInitialized
       // used to store current tutor context on server providing
       // a way to restore the tutor to its current var defs.
       //
       $wnd.gwt_solutionHasBeenInitialized = function(tutorWrapper) {
           try {
               var vars = $wnd._tutorData._variables;
               
               var pid = $wnd.TutorManager.pid;
               var probNum=0;
               var solutionVariablesJson = $wnd.getTutorVariableContextJson(vars);
               
               that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_setTutorWrapperId(I)(tutorWrapper.id);
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

       $wnd.TutorManager.initializeTutor(instance, pid, jsonConfig, solutionDataJs,solutionHtml,title,hasShowWork,shouldExpandSolution,solutionContext,submitButtonText, indicateWidgetStatus, installCustomSteps, whiteboardText);

       $wnd.Flashcard_mngr.problemGenDebugInfo =  function (code, info) {
            that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_problemGenDebugInfo(Ljava/lang/String;Ljava/lang/String;)(code, info);
       }

   }-*/;

    private void gwt_problemGenDebugInfo(String code, String info) {
        tutorCallback.debugLogOut("Problem Debug", "gwt_problemGenDebugInfo: code: " + code + ", info: " + info);
    }

    private void gwt_showWhiteBoard() {
        this.tutorCallback.showWhiteboard();
    }

    private void resizeTutor() {
        if(_readOnly) {
            setupReadonlyMask(_readOnly);
        }
    }


    private void gwt_tutorQuestionGuessChanged(String id, String selection, String correct) {
        boolean isCorrect = correct != null && correct.equalsIgnoreCase("true")?true:false;
        //tutorWidgetComplete(selection, isCorrect);
        this.tutorCallback.tutorWidgetComplete(selection, isCorrect);

        jsni_markMultiChoiceWidgetStatus(isCorrect);


        CmRpcCore.EVENT_BUS.fireEvent(new TutorWidgetInputCompleteEvent(_solutionInfo.getPid(), selection, isCorrect));
    }

    native private void jsni_markMultiChoiceWidgetStatus(boolean correct) /*-{
         $wnd.TutorSolutionWidgetValues.getActiveWidget().setWidgetMessage(correct?'true':'false');
    }-*/;

    SolutionInfo _solutionInfo;
    Map<Integer, String> _variableContexts = new HashMap<Integer, String>();

    private String gwt_getSolutionProblemContext(int probNum) {
        return _solutionInfo.getContext().getContextJson();
    }


    native private boolean isThereIsATutorWidget() /*-{
        return $wnd.TutorSolutionWidgetValues.isWidgetOnSolution();
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

        Log.debug("Calling solutionHasBeenInitialized: " + tutorCallback.getClass().getName());

        this.tutorCallback.solutionHasBeenInitialized();

        Log.debug("solutionHasBeenInitialized complete");

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
                CmRpcCore.getCmService().execute(action, new AsyncCallback<RpcData>() {
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
        CmRpcCore.EVENT_BUS.addHandler(WindowHasBeenResizedEvent.TYPE, new WindowHasBeenResizedHandler() {
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


        CmRpcCore.EVENT_BUS.addHandler(ShowTutorWidgetCompleteInfoEvent.TYPE, new ShowTutorWidgetCompleteInfoHandler() {
            @Override
            public void showTutorWidgetCompleteInfo() {
                __lastInstance.showTutorWidgetCompleteInfo();
            }
        });
    }


    protected void showTutorWidgetCompleteInfo() {
        Log.debug("showTutorWidgetCompleteInfo called");
        widgetCorrectInfo.addClassName(".show");
    }

    /** Provide granular customization of button bar
     *
     * @author casey
     *
     */
    static public class ButtonBarSetup {
        boolean showNextPrev;
        boolean showWhiteboard;
        boolean showReturn;

        String returnButtonText;

        public ButtonBarSetup(boolean showNextPrev, boolean showWhiteboard, boolean showReturn, String returnButtonText) {
            this.showNextPrev = showNextPrev;
            this.showWhiteboard = showWhiteboard;
            this.showReturn = showReturn;
            this.returnButtonText = returnButtonText;
        }
    }



    public native void jsni_addWhiteboardSubmitButton() /*-{
        var widgetHolder = $wnd.$get("hm_flash_widget");
        if(widgetHolder) {
            var ih = widgetHolder.innerHTML;
            widgetHolder.innerHTML = ih + "<p><input type='button' onclick='gwt_submitWhiteboardAnswer()' class='sexybutton sexysimple sexylarge sexyred' value='Submit Whiteboard Answer'/></p>";
        }
        var that = this;
        $wnd.gwt_submitWhiteboardAnswer = function() {
             that.@hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel::gwt_submitWhiteboardAnswer()();
        }

    }-*/;


    static native public void jsni_showWhiteboardStatus(String status, Element element) /*-{
        if(status == 'Submitted' || status == 'Correct' || status == 'Incorrect' || status == 'Half Correct') {
            var widgetHolder = $wnd.$get('hm_flash_widget', element);
            widgetHolder.innerHTML = "<div name='hm_flash_widget_head' style='display: block'>" + status + "</div>";
            //$wnd.setWidgetMessage(status);
        }
    }-*/;



    private void gwt_submitWhiteboardAnswer() {
        TutorWrapperPanel.jsni_showWhiteboardStatus("Submitted", this.tutorDomNode);
        tutorCallback.showWorkHasBeenSubmitted();
   }

    static native public void jsni_showWhiteboardWidgetMessage(String message) /*-{
        var widgetHolder = $wnd.$get("hm_flash_widget");
        if(widgetHolder) {
            widgetHolder.innerHTML = message;
        }
    }-*/;


    /** Remove the entire whiteboard message area
     *
     */
    static native public void jsni_hideWhiteboardStatus() /*-{
    var widgetHolder = $wnd.$get("hm_flash_widget");
    if(widgetHolder) {
        widgetHolder.style.display = 'none';
    }
}-*/;

    private void setProblemStatusControl(String status) {
        problemStatus.setInnerHTML(status);
    }

    public void setProblemStatus(AssignmentProblem problem) {

            if (problem.getProblemType() == ProblemType.WHITEBOARD) {
                String status = problem.getStatus();
                if ((!problem.isAssignmentClosed() && !problem.isGraded()) && !status.equals(ProblemStatus.SUBMITTED.toString())) {
                    TutorWrapperPanel.jsni_showWhiteboardWidgetMessage("<div><p>Use the whiteboard to enter your answer</p></div>");
                } else {
                    TutorWrapperPanel.jsni_hideWhiteboardStatus();
                }
            }

            String msg = "";
            if(problem.isAssignmentClosed() || problem.isGraded()) {
                msg = problem.getStudentProblem().getStatus();
            }
            else {
                String s = problem.getStatus().toLowerCase();
                if(problem.getLastUserWidgetValue() != null || s.equals("submitted") || s.equals("correct") || s.equals("incorrect") || s.equals("half credit")) {
                    msg = "Submitted";
                }
            }

            if(msg.equals("Viewed")) {
                msg = "Unanswered";
            }


            setProblemStatusControl(msg);
        }

        static native public void processStaticWhiteboards() /*-{
            $wnd.setupStaticWhiteboards();
        }-*/;


        /** Extract just the widget JSON
         *
         * @param html
         * @return
         */
        static public String extractWidgetJson(String html) {

           String START_TOKEN="hm_flash_widget_def";

           int startPos = html.indexOf(START_TOKEN);
           if(startPos == -1) {
               return null;
           }

           startPos = html.indexOf("{", startPos);
           int endPos = html.indexOf("}", startPos);
           String json = html.substring(startPos, endPos+1);

           return json;
        }

        /** Return html without any widget definition
         *
         * @param html
         * @return
         */
        static public String stripWidgetFromHtml(String html) {
        	String token = "hm_flash_widget";

            String START_TOKEN_SINGLE="<div name='" + token + "'";
            String START_TOKEN_DOUBLE="<div name=\"" + token + "\"";
            int startPos = html.indexOf(START_TOKEN_SINGLE);
            if(startPos == -1) {
            	startPos = html.indexOf(START_TOKEN_DOUBLE);
            	if(startPos == -1) {
            		return html;
            	}
            }
            
            int endPos = html.indexOf("</div>", startPos);
            endPos = html.indexOf("</div>", endPos+1);

            String htmlNew = html.substring(0, startPos);
            htmlNew += html.substring(endPos+6);

            return htmlNew;
        }

        /** Return the current widget json */
        public static native String jsni_getWidgetJson() /*-{
            return $wnd.TutorSolutionWidgetValues.getActiveWidget().widgetJson;
        }-*/;
}
