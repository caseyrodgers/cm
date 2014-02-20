package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.CmGwtTestUi;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc.client.model.SolutionMetaStep;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction.SaveType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class ProblemDesigner extends Composite {
    
    static private ProblemDesigner __lastInstance;
    
    BorderLayoutContainer _main;
    SolutionInfo _solutionInfo;
    private TutorWrapperPanel _tutorWrapper;
    private SolutionMeta _solutionMeta;
    ContentPanel _problemPanel = new ContentPanel();

    static private ToggleButton _editMode;
    public ProblemDesigner() {
        __lastInstance = this;

        if(_editMode == null) {
            _editMode = new ToggleButton("Preview Mode");
            _editMode.setValue(false);
            _editMode.addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    __lastInstance.loadProblem();
                }
            });
        }
        setupJsniHooks();
        
        _main = new BorderLayoutContainer();
        _main.setCenterWidget(new DefaultGxtLoadingPanel());
        

        _problemPanel.addTool(_editMode);
        
        _problemPanel.addTool(new TextButton("Edit Input", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                new ProblemDesignerEditorWidget(_solutionInfo,TutorWrapperPanel.jsni_getWidgetJson(), callback);
            }
        }));
        
        _problemPanel.addTool(new TextButton("Add Step/Hint", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                addNewHintStep();
            }
        }));
        

        initWidget(_main);
    }
    
    public SolutionMeta getSolutionMeta() {
        return _solutionMeta;
    }

    protected void addNewHintStep() {
        new ProblemDesignerEditorHintStep(_solutionInfo,_solutionMeta,ProblemDesignerEditorHintStep.NEW_HINTSTEP,callback);
    }

    native private void setupJsniHooks() /*-{
        var that = this;
        $wnd.gwt_editPart = function(partType, data) {
            that.@hotmath.gwt.cm_admin.client.custom_content.problem.ProblemDesigner::gwt_editPart(Ljava/lang/String;Ljava/lang/String;)(partType, data);
        }
    }-*/;

    public void loadProblem(final String pid) {
        
        new RetryAction<CmList<Response>>() {
            @Override
            public void attempt() {
                MultiActionRequestAction mAction = new MultiActionRequestAction();
                mAction.getActions().add(new GetSolutionAction(0, 0, pid));
                mAction.getActions().add(new LoadSolutionMetaAction(pid));
                
                setAction(mAction);
                CmShared.getCmService().execute(mAction, this);
            }

            @Override
            public void oncapture(CmList<Response> responses) {
                
                SolutionInfo si = (SolutionInfo)responses.get(0);
                SolutionMeta sm = (SolutionMeta)responses.get(1);
                
                // Window.alert("Solution: " + responses);
                loadProblem(si, sm);
            }
        }.register();
    }
    
    private void loadProblem() {
        loadProblem(_solutionInfo, _solutionMeta);   
    }
    
    
    CallbackOnComplete callback = new CallbackOnComplete() {
        @Override
        public void isComplete() {
            loadProblem(_solutionInfo.getPid());
        }
    };
    
    
    private void gwt_editPart(String partType, String data) {

        if(partType.equals("whiteboard")) {
            new ProblemDesignerEditorWhiteboard(_solutionInfo, "wb_ps", callback);
        }
        else if(partType.equals("widget")) {
            String widgetJson = TutorWrapperPanel.jsni_getWidgetJson();
            if(widgetJson == null) {
                widgetJson = "{type:''}";
            }
            Log.debug("Widget json: " + widgetJson);
            new ProblemDesignerEditorWidget(_solutionInfo,widgetJson, callback);
        }
        else if(partType.equals("hint")) {
            int which = Integer.parseInt(data);
            new ProblemDesignerEditorHintStep(_solutionInfo,_solutionMeta, which, callback);
        }
        else if(partType.equals("hint-remove")) {
            int which = Integer.parseInt(data);
            removeSolutionHintStep(which);
        }
        else if(partType.equals("hint-moveDown")) {
            int which = Integer.parseInt(data);
            moveSolutionHintStep(which, which+1);
        }
        else if(partType.equals("hint-moveUp")) {
            int which = Integer.parseInt(data);
            moveSolutionHintStep(which, which-1);
        }
        else if(partType.equals("add_hint-step")) {
            addNewHintStep();
        }
        else {
            Window.alert("UNKNOWN EDIT GWT PART: " + partType + " data: " + data);
        }
    }
    
    private void moveSolutionHintStep(int which, int toWhere) {
        
        if(toWhere < 0 || toWhere > _solutionMeta.getSteps().size()-1) {
            CmMessageBox.showAlert("Cannot move hint/step");
        }
        else {
            SolutionMetaStep hintToMove = _solutionMeta.getSteps().get(which);
            if(hintToMove != null) {
                _solutionMeta.getSteps().remove(which);
                _solutionMeta.getSteps().add(toWhere,  hintToMove);
                
                saveSolutionToServer();
            }
        }
    }

    private void removeSolutionHintStep(final int which) {
        SolutionMetaStep hintToRemove = _solutionMeta.getSteps().get(which);
        if(hintToRemove != null) {
            CmMessageBox.confirm("Remove Step",  "Are you sure you want to remove this hint/step?", new ConfirmCallback() {
                @Override
                public void confirmed(boolean yesNo) {
                    if(yesNo) {
                        doRemoveHintStep(which);
                    }
                }
            });
        }
    }

    protected void doRemoveHintStep(final int which) {
        _solutionMeta.getSteps().remove(which);
        saveSolutionToServer();
    }
    
    
    private void saveSolutionToServer() {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                SaveCustomProblemAction action = new SaveCustomProblemAction(_solutionInfo.getPid(), SaveType.HINTSTEP, _solutionMeta);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }
            public void oncapture(RpcData value) {
                Log.info("Hint saved");
                loadProblem(_solutionInfo.getPid());
            }
        }.register();
    }


    native private void jsni_SetupStepEditHooks(boolean showAddStepHintButton) /*-{

       var that = this;
       
       $wnd.gwt_getHintNumber = function(o) {
          return o.parentElement.parentElement.getAttribute("hint_step_num");
       }
       
       $wnd.gwt_getWidgetJson = function(o) {
          return $wnd.getActiveWidget().widgetJson;
       }
       
       // add hooks on double click to bring up appropriate editor
       // and add a hover to indicate editability
       //
       $wnd.$('.cm_whiteboard').prepend("<div class='cp_designer-toolbar'><button onclick='window.gwt_editPart(\"whiteboard\",null)'>Click to Edit Problem Statement</button></div>").dblclick(function(x) {
           //var whiteboardId = x.target.parentNode.parentNode.parentNode.parentNode.getAttribute("wb_id");
           $wnd.gwt_editPart('whiteboard', null);
       });
    
       $wnd.$('#hm_flash_widget').prepend("<div class='cp_designer-toolbar'><button onclick='window.gwt_editPart(\"widget\",null)'>Click to Edit Input</button></div>").dblclick(function() {
          $wnd.gwt_editPart('widget', whiteboardId);
       });
       
       
       var clickDef = "<div class='cp_designer-toolbar'><button onclick='window.gwt_editPart(\"hint\",window.gwt_getHintNumber(this))'>Click to Edit</button>" +
                      "<button onclick='window.gwt_editPart(\"hint-remove\",window.gwt_getHintNumber(this))'>Click to Remove</button>" +
                      "<button onclick='window.gwt_editPart(\"hint-moveUp\",window.gwt_getHintNumber(this))'>^</button>" +
                      "<button onclick='window.gwt_editPart(\"hint-moveDown\",window.gwt_getHintNumber(this))'>v</button></div>";
                      
       $wnd.$('.hint-step').prepend(clickDef).dblclick(function() {
          var stepId = getStepId(this);
          $wnd.gwt_editPart('hint', stepId);
       });
       
       if(true || showAddStepHintButton) {
           $wnd.$('#raw_tutor_steps').append("<div style='margin-top: 15px;' class='cp_designer-toolbar'><button onclick='window.gwt_editPart(\"add_hint-step\",null)'>Click to Add Hint/Step</button></div>").dblclick(function() {
              $wnd.gwt_editPart('add_hint', whiteboardId);
           });
       }
       
       
    }-*/;
    
    protected void loadProblem(final SolutionInfo solution, SolutionMeta solutionMeta) {
        _solutionInfo = solution;
        _solutionMeta = solutionMeta;
        _tutorWrapper = new TutorWrapperPanel(_editMode.getValue(), false, false, false, new TutorCallbackDefault() {
            @Override
            native public void solutionHasBeenInitialized() /*-{
                                                 
            }-*/;
            
            @Override
            public boolean moveFirstHintOnWidgetIncorrect() {
                return false;
            }
        });
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.setScrollMode(ScrollMode.AUTO);
        flow.add(_tutorWrapper);
        
        _problemPanel.setWidget(flow);
        _main.setCenterWidget(_problemPanel);
        _main.forceLayout();

        boolean shouldExpand = !_editMode.getValue();
        _tutorWrapper.externallyLoadedTutor(solution, getWidget(), "", "Solution Title", false, shouldExpand, null);

        if(!_editMode.getValue()) {
            jsni_SetupStepEditHooks(solutionMeta.getSteps().size()==0);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                native public void execute() /*-{
                    if($wnd.TutorSolutionWidgetValues.getActiveWidget().showWidgetCorrectValue) {
                        $wnd.TutorSolutionWidgetValues.getActiveWidget().showWidgetCorrectValue();
                    }
                }-*/;
            });
        }
    }

  

    static public class GwtTestUi implements CmGwtTestUi {
        @Override
        public void startTest() {
            String testPid="test_casey_1_1_1_1";
            testPid="cmextras_dynamic_oops_basic_1_1";
            new ProblemDesigner().loadProblem(testPid);
        }
    }

    public SolutionInfo getSolutionInfo() {
        return _solutionInfo;
   }

}
