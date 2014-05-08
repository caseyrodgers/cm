package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.custom_content.problem.ProblemDesignerEditor.EditorCallback;
import hotmath.gwt.cm_core.client.CmGwtTestUi;
import hotmath.gwt.cm_core.client.JSOModel;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc.client.model.SolutionMetaStep;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction.SaveType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
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

/** Main custom problem editor
 * 
 * @author casey
 *
 */
public class ProblemDesigner extends Composite {
    
    private static final int SCROLL_TO_END = -1;

    static private ProblemDesigner __lastInstance;
    
    BorderLayoutContainer _main;
    SolutionInfo _solutionInfo;
    private TutorWrapperPanel _tutorWrapper;
    private SolutionMeta _solutionMeta;
    ContentPanel _problemPanel = new ContentPanel();

    private CustomProblemModel _customProblem;

    static private ToggleButton _editMode;
    CallbackOnComplete callback;
    
    public ProblemDesigner(CallbackOnComplete callbackIn) {
        __lastInstance = this;
        this.callback = callbackIn;
        
        if(_editMode == null) {
            _editMode = new ToggleButton("Preview Mode");
            _editMode.setValue(false);
            _editMode.addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    __lastInstance.loadProblem(__lastInstance._customProblem, 0);
                }
            });
        }
        setupJsniHooks();
        
        _main = new BorderLayoutContainer();
        _main.setCenterWidget(new DefaultGxtLoadingPanel());
        

        _problemPanel.addTool(new TextButton("Close", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(callback != null) {
					callback.isComplete();
				}
			}
		}));
        _problemPanel.addTool(_editMode);
        
        initWidget(_main);
    }
    
    public SolutionMeta getSolutionMeta() {
        return _solutionMeta;
    }

    protected void addNewHintStep() {
        SolutionMetaStep hintStep = new SolutionMetaStep();
        _solutionMeta.getSteps().add(hintStep);
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                SaveCustomProblemAction action = new SaveCustomProblemAction(_solutionMeta.getPid(), SaveType.HINTSTEP, _solutionMeta);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }
            public void oncapture(RpcData value) {
                CmBusyManager.setBusy(false);
                Log.info("Hint saved");
                
                loadProblem(_customProblem, SCROLL_TO_END);
            }
        }.register();        
    }

    native private void setupJsniHooks() /*-{
        var that = this;
        $wnd.gwt_editPart = function(partType, data) {
            that.@hotmath.gwt.cm_admin.client.custom_content.problem.ProblemDesigner::gwt_editPart(Ljava/lang/String;Ljava/lang/String;)(partType, data);
        }
    }-*/;

    public void loadProblem(CustomProblemModel customProblem, final int scrollPosition) {
        _customProblem = customProblem;
        final String pid = customProblem.getPid();
        String label = customProblem.getLabel();
        
        if(label != null) {
           _problemPanel.setHeadingText("Problem: " + label);
        }
        
        CmBusyManager.setBusy(true);
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
                CmBusyManager.setBusy(false);        
                SolutionInfo si = (SolutionInfo)responses.get(0);
                SolutionMeta sm = (SolutionMeta)responses.get(1);
                
                // Window.alert("Solution: " + responses);
                loadProblem(si, sm, scrollPosition);
            }
        }.register();
    }
    
    
    
    CallbackOnComplete reloadCallback = new CallbackOnComplete() {
        @Override
        public void isComplete() {
            int scrollPosition = _tutorFlow.getScrollSupport().getVerticalScrollPosition();
            loadProblem(_customProblem,scrollPosition);
        }
    };
    
    
    native private String jsni_getProblemStatementHtml() /*-{
        var m = $wnd.$('.cm_problem_text');
        if(m.length == 0) {
            return '';
        }
        return m.html();
    }-*/;
    
    
    private void gwt_editPart(String partType, String data) {

        // Window.alert("Part: " + partType + ", " + data);
        if(partType.equals("whiteboard")) {
            
            ProblemDesignerEditor.getSharedWindow().show(_solutionMeta.getProblemStatement(), "wb_ps", new EditorCallback() {
                
                @Override
                public void editingComplete(String pidEdit, String textPluswhiteboardJson) {
                    saveProblemStatement(pidEdit,textPluswhiteboardJson);
                }
            });
            
            
        }
        else if(partType.equals("widget")) {
            String widgetJson = TutorWrapperPanel.jsni_getWidgetJson();
            if(widgetJson == null) {
                widgetJson = "{type:''}";
            }
            Log.debug("Widget json: " + widgetJson);
            new ProblemDesignerEditorWidget(_solutionInfo,widgetJson, reloadCallback);
        }
        else if(partType.equals("hint")) {
            int which = Integer.parseInt(data);
            final SolutionMetaStep step = _solutionMeta.getSteps().get(which);
            
            ProblemDesignerEditor.getSharedWindow().show(step.getHint(), "wb_hint", new EditorCallback() {
                @Override
                public void editingComplete(String pidEdit, String textPartPlusWhiteboardJson) {
                    step.setHint(textPartPlusWhiteboardJson);
                    saveSolutionToServer();
                }
            });
        }
        else if(partType.equals("step")) {
            int which = Integer.parseInt(data);
            final SolutionMetaStep step = _solutionMeta.getSteps().get(which);
            ProblemDesignerEditor.getSharedWindow().show(step.getText(), "wb_step", new EditorCallback() {
                @Override
                public void editingComplete(String pidEdit, String textPartPlusWhiteboardJson) {
                    step.setText(textPartPlusWhiteboardJson);
                    saveSolutionToServer();
                }
            });        }        
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
    
    protected void saveProblemStatement(final String pidEdit, final String textAndwhiteboardJson) {
            CmBusyManager.setBusy(true);
            
            new RetryAction<RpcData>() {
                @Override
                public void attempt() {
                    SaveCustomProblemAction action = new SaveCustomProblemAction(_solutionInfo.getPid(), SaveType.PROBLEM_STATEMENT_TEXT, textAndwhiteboardJson); 
                    setAction(action);
                    CmShared.getCmService().execute(action, this);
                }

                @Override
                public void oncapture(RpcData result) {
                    CmBusyManager.setBusy(false);
                    Log.info("Whiteboard changes commited: " + result);
                    loadProblem(_customProblem, 0);
                }

            }.register();   
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
        
        final int scrollPosition = _tutorFlow.getScrollSupport().getVerticalScrollPosition();
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                SaveCustomProblemAction action = new SaveCustomProblemAction(_solutionInfo.getPid(), SaveType.HINTSTEP, _solutionMeta);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }
            public void oncapture(RpcData value) {
                Log.info("Hint saved");
                loadProblem(_customProblem, scrollPosition);
            }
        }.register();
    }


    native private void jsni_SetupStepEditHooks(boolean showAddStepHintButton) /*-{

       var that = this;
       
       $wnd.gwt_getHintNumber = function(o) {
          var stepNum = $wnd.$(o).closest('.hint-step').attr('hint_step_num');
          return stepNum;
       }
       
       $wnd.gwt_getWidgetJson = function(o) {
          return $wnd.getActiveWidget().widgetJson;
       }
       
       // add hooks on double click to bring up appropriate editor
       //
       $wnd.$('#problem_statement').prepend("<div class='cp_designer-toolbar'><button onclick='window.gwt_editPart(\"whiteboard\",null)'>Edit Problem</button></div>");
    
       // $wnd.$('#hm_flash_widget').prepend("<div class='cp_designer-toolbar'><button onclick='window.gwt_editPart(\"widget\",null)'>Edit Input</button></div>");
       
       $wnd.$('#hm_flash_widget').html("<div class='cp_designer-toolbar'><button onclick='window.gwt_editPart(\"widget\",null)'>Edit Input</button></div><div id='widget_info'></div>");
       
       $wnd.$('.widget-not-used').replaceWith('<p>Input Type: whiteboard</p>');
       
       var clickDef = "<div class='cp_designer-toolbar'>" +
                      "<button onclick='window.gwt_editPart(\"hint-remove\",window.gwt_getHintNumber(this))'>Remove Hint/Step</button>" +
                      "<button onclick='window.gwt_editPart(\"hint-moveUp\",window.gwt_getHintNumber(this))'>^</button>" +
                      "<button onclick='window.gwt_editPart(\"hint-moveDown\",window.gwt_getHintNumber(this))'>v</button></div>";
                      
       $wnd.$('.hint-step').addClass('hint-step-box').prepend(clickDef);
       
       
       var hintClickDef = "<div class='cp_designer-toolbar'>" +
                      "<button onclick='window.gwt_editPart(\"hint\",window.gwt_getHintNumber(this))'>Edit Hint</button>";
                      
                      
       $wnd.$('.hint').prepend(hintClickDef).dblclick(function() {
          var stepId = $wnd.gwt_getHintNumber(this.parentElement.parentElement);
          $wnd.gwt_editPart('hint', stepId);
       });
       
       var stepClickDef = "<div class='cp_designer-toolbar'>" +
                  "<button onclick='window.gwt_editPart(\"step\",window.gwt_getHintNumber(this))'>Edit Step</button>";
                      
                      
       $wnd.$('.step').prepend(stepClickDef).dblclick(function() {
          var stepId = getStepId(this.parentElement.parentElement);
          $wnd.gwt_editPart('step', stepId);
       });

       
       if(true || showAddStepHintButton) {
           $wnd.$('#raw_tutor_steps').append("<div style='margin-top: 15px;' class='cp_designer-toolbar'><button onclick='window.gwt_editPart(\"add_hint-step\",null)'>Click to Add Hint/Step</button></div>").dblclick(function() {
              $wnd.gwt_editPart('add_hint', whiteboardId);
           });
       }
       
       
    }-*/;
    
    FlowLayoutContainer _tutorFlow;
    protected void loadProblem(final SolutionInfo solution, SolutionMeta solutionMeta, int scrollPosition) {
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
        _tutorFlow = new FlowLayoutContainer();
        _tutorFlow.setScrollMode(ScrollMode.AUTO);
        _tutorFlow.add(_tutorWrapper);
        
        _problemPanel.setWidget(_tutorFlow);
        _main.setCenterWidget(_problemPanel);



        boolean shouldExpand = true; // !_editMode.getValue();
        _tutorWrapper.externallyLoadedTutor(solution, getWidget(), "", "Solution Title", false, shouldExpand, null);

        if(!_editMode.getValue()) {
            jsni_SetupStepEditHooks(solutionMeta.getSteps().size()==0);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                	String widgetJson = TutorWrapperPanel.jsni_getWidgetJson();
                    JSOModel model = JSOModel.fromJson(widgetJson);
                    WidgetDefModel widgetDef=null;
                    if(model != null) {
                    	widgetDef = new WidgetDefModel(model);
                    }
                    else {
                    	widgetDef = new WidgetDefModel();
                    	widgetDef.setType("");
                    }
                    try {
                    	WidgetEditor wid = WidgetEditorFactory.createEditorFor(widgetDef);
                    	
                    	Element widgetInfo = DOM.getElementById("widget_info");
                    	if(widgetInfo == null) {
                    		CmMessageBox.showAlert("Widget Info div not found");
                    		return;
                    	}

                    	/** Create human reading label identifing input type and value */
                    	String html = "<b>Input Type</b>: " + wid.getWidgetTypeLabel();
                    	
                    	html += wid.getWidgetValueLabel() != null?"<br/><b>Correct Value</b>: " + wid.getWidgetValueLabel():"";
                    	widgetInfo.setInnerHTML(html);
                    }
                    catch(Exception e) {
                    	e.printStackTrace();
                    }
                }
                
                
//                native public void execute() /*-{
//                    if($wnd.TutorSolutionWidgetValues.getActiveWidget().showWidgetCorrectValue) {
//                        // $wnd.TutorSolutionWidgetValues.getActiveWidget().showWidgetCorrectValue();
//                    }
//                }-*/;
            });
        }

        
        _main.forceLayout();

        final int thisScrollPosition;
        if(scrollPosition == SCROLL_TO_END) {
            thisScrollPosition = _tutorFlow.getScrollSupport().getMaximumVerticalScrollPosition();
        }
        else {
            thisScrollPosition = scrollPosition;
        }
        new Timer() {
            @Override
            public void run() {
                _tutorFlow.getScrollSupport().setVerticalScrollPosition(thisScrollPosition);
            }
        }.schedule(0);
    }

  

    static public class GwtTestUi implements CmGwtTestUi {
        @Override
        public void startTest() {
            String testPid="test_casey_1_1_1_1";
            testPid="cmextras_dynamic_oops_basic_1_1";
            new ProblemDesigner(null).loadProblem(new CustomProblemModel(testPid, 0, null, null, ProblemType.UNKNOWN),0);
        }
    }

    public SolutionInfo getSolutionInfo() {
        return _solutionInfo;
   }

}
