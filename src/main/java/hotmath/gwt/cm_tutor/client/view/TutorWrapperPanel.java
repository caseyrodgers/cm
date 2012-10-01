package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.event.SolutionHasBeenLoadedEvent;

import java.util.HashMap;
import java.util.Map;

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
    
    

    private TutorCallback tutorCallback;

    interface MyUiBinder extends UiBinder<Widget, TutorWrapperPanel> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public TutorWrapperPanel(boolean showButtonBar, boolean showReturnButton, boolean showWhiteboardButton, TutorCallback tutorCallback) {

        this.tutorCallback = tutorCallback;
        initWidget(uiBinder.createAndBindUi(this));

        if(!showButtonBar) {
            buttonBar.setAttribute("style", "display: none");
        }
        if (!showWhiteboardButton) {
            whiteboardButton.setAttribute("style", "display:none");
        }
        if (!showReturnButton) {
            returnButton.setAttribute("style", "display:none");
        }
    }
    
    

    public void loadSolution(final String pid, final String title, final boolean hasShowWork, final boolean shouldExpandSolution, final String jsonConfig,
            final CallbackAfterSolutionLoaded callback) {
        GetSolutionAction action = new GetSolutionAction(UserInfo.getInstance().getUid(), UserInfo.getInstance()
                .getRunId(), pid);
        CmTutor.getCmService().execute(action, new AsyncCallback<SolutionInfo>() {
            @Override
            public void onSuccess(SolutionInfo result) {
                try {
                    _solutionInfo = result;
    
                    //System.out.println("SolutionInfo: " + result);
    
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
        initializeTutorNative(instance, pid, jsonConfig, solutionDataJs, solutionHtml, title, hasShowWork, shouldExpandSolution, solutionContext);
        
        CmRpc.EVENT_BUS.fireEvent(new SolutionHasBeenLoadedEvent(_solutionInfo));
    }
    
    
    
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
           alert('error saving solution context: ' + e);}
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
            SaveSolutionContextAction action = new SaveSolutionContextAction(UserInfo.getInstance().getUid(), UserInfo
                    .getInstance().getRunId(), pid, problemNumber, variablesJson);
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

    public static interface TutorCallback {
        void onNewProblem(int problemNumber);

        void tutorWidgetComplete(String inputValue, boolean correct);

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
