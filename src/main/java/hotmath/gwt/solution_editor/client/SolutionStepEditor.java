
package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.CmEventListener;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc.client.model.SolutionMetaStep;
import hotmath.gwt.cm_rpc.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.solution_editor.client.StepEditorPlainTextDialog.EditCallback;
import hotmath.gwt.solution_editor.client.WidgetListDialog.Callback;
import hotmath.gwt.solution_editor.client.rpc.SaveSolutionStepsAdminAction;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class SolutionStepEditor extends ContentPanel {
    SolutionMeta _meta;
    static SolutionStepEditor __instance;
    StepContainer _selectedContainer;
    SolutionMetaStep _cutStep;
    
    String _statement;
    CheckBox isActiveCheckBox = new CheckBox();
    private FlowLayoutContainer _mainFlow;

    public SolutionStepEditor() {
        __instance = this;
        
        _mainFlow = new FlowLayoutContainer();
        _mainFlow.setScrollMode(ScrollMode.AUTOY);
        _mainFlow.setStyleName("solution-step-editor");
        
        CenterLayoutContainer clc = new CenterLayoutContainer();
        clc.setWidget(new Label("No solution loaded."));
        _mainFlow.add(clc);
        setWidget(_mainFlow);
        
        isActiveCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                _meta.setActive(isActiveCheckBox.getValue());
                EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED,_meta));                
            }
        });
        
        getHeader().addTool(new MyFieldLabel(isActiveCheckBox, "Active", 30, 10));
        getHeader().addTool(new Spacer());        
                
        getHeader().addTool(new TextButton("Define", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
                showDefineEditor();
            }
        }));

        getHeader().addTool(new TextButton("Widget", new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
                showWidgetEditor();
            }
        }));
        
        getHeader().addTool(new TextButton("Cut Step", new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
                cutSelectedStep();
            }
        }));
        
        getHeader().addTool(new TextButton("Paste Step", new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
                pasteCutStep();
            }
        }));

        
        getHeader().addTool(new TextButton("Add Step", new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
                addNewStep();
            }
        }));
        
        getHeader().addTool(new TextButton("Info", new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
                showInfo();
            }
        }));
        
    }
    
    public SolutionMeta getMeta() {
        return _meta;
    }
    
    public String getStatement() {
        return _statement;
    }
    
    private void showInfo() {
       new ShowValueWindow("Loaded Problem",_meta.getPid());
    }
    
    public List<SolutionMetaStep> getSteps() {
        return _meta.getSteps();
    }
    
    private void addNewStep() {
        flushChanges();
        _meta.getSteps().add(new SolutionMetaStep(_meta,"A New Hint", "A New Step",null));
        buildSolutionEditor(_meta);
        
        fireChanged();
    }
    
    public void showWidgetEditor() {
        String problemStatement = _meta.getProblemStatement();
        String widgetJson = TutorWrapperPanel.extractWidgetJson(problemStatement);
        WidgetListDialog.showWidgetListDialog(new Callback() {
            @Override
            public void resourceSelected(WidgetDefModel widget) {
                if(widget == null)
                    return;
                
                String ps = TutorWrapperPanel.stripWidgetFromHtml(_meta.getProblemStatement());
                ps += widget.getWidgetHtml();
                _meta.setProblemStatement(ps);
                buildSolutionEditor(_meta);
                
                fireChanged();
            }
        }, widgetJson);
    }
    
    public void showDefineEditor() {
    	new StepEditorPlainTextDialog(new EditCallback() {
			@Override
			public String getTextToEdit() {
				return _meta.getTutorDefine();
			}

			@Override
			public void saveTextToEdit(String editedText) {
				_meta.setTutorDefine(editedText);
                EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED));
			}
		});
    }
    
    public void fireChanged() {
        EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED,_meta));
    }
    
    
    private void cutSelectedStep() {
        flushChanges();
        SolutionMetaStep step = _selectedContainer.getStep();
        for(int s=0,t=_meta.getSteps().size();s<t;s++) {
            if(_meta.getSteps().get(s).equals(step)) {
                _cutStep = step;
                _meta.getSteps().remove(s);
                break;
            }
        }
        buildSolutionEditor(_meta);   
        fireChanged();
    }
    
    private void pasteCutStep() {
        flushChanges();
        SolutionMetaStep step = _selectedContainer.getStep();
        for(int s=0,t=_meta.getSteps().size();s<t;s++) {
            if(_meta.getSteps().get(s).equals(step)) {
                _meta.getSteps().add(s,_cutStep);
                break;
            }
        }
        buildSolutionEditor(_meta);   
        fireChanged();
    }
    
    private void buildSolutionEditor(SolutionMeta meta) {
        
        Log.debug("Loading SolutionMeta: steps: " + meta.getNumSteps() + ", define len: " + (meta.getTutorDefine() != null?meta.getTutorDefine().length():-1));
        
        this._meta = meta;
        _mainFlow.clear();

        
        isActiveCheckBox.setValue(meta.isActive());
        
        setHeadingText("Solution Step Editor");
        _mainFlow.add(new StatementContainer("Problem Statement", new ProblemStatement(meta)));
        
        for(int s=0,t=meta.getSteps().size();s<t;s++) {
            _mainFlow.add(new StepContainer(meta.getPid(),(s+1), meta,meta.getSteps().get(s),meta.getSteps().get(s).getFigure()));
        }
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.POST_SOLUTION_LOAD));
    }
    
    private native void initializeTutorInEditor() /*-{
        $wnd.initializeSolutionEditor();
    }-*/;
    
    private void setStepContainerSelected(StepContainer container) {
    	
        _selectedContainer = container;
        
        for(int i=0,t=getWidgetCount();i<t;i++) {
            Widget comp = getWidget(i);
            if(comp instanceof StepContainer) {
                comp.removeStyleName("selected");
            }
        }
        container.addStyleName("selected");
    }
    
    private void flushChanges() {
        
        if(true)
            return;
        
        List<SolutionMetaStep> steps = new ArrayList<SolutionMetaStep>();
        for(int i=0,t=getWidgetCount();i<t;i++) {
            Widget comp = getWidget(i);
            if(comp instanceof StepUnitWrapper) {
                String text = ((StepUnitWrapper)comp).getItem().getEditorText();
                _meta.setProblemStatement(text);
            }
            else if(comp instanceof StepContainer) {
                StepContainer sc = (StepContainer)comp;
                steps.add(new SolutionMetaStep(_meta,sc.getHintText(),sc.getStepText(), sc.getFigure()));
            }
        }
        _meta.setSteps(steps );
    }
    
    public void saveStepChanges(String asPid) {

        List<StepUnitPair> stepPairs = new ArrayList<StepUnitPair>();

        String statement="";
        for(int i=0,t=_mainFlow.getWidgetCount();i<t;i++) {
            Widget comp = _mainFlow.getWidget(i);
            if(comp instanceof StatementContainer) {
                String text = ((StatementContainer)comp).getStepUnitWrapper().getItem().getEditorText();
                statement = text;            
            }
            else if(comp instanceof StepContainer) {
                StepContainer sc = (StepContainer)comp;
                StepUnitPair  su = new StepUnitPair(sc.getHintText(), sc.getStepText(), sc.getFigure());
                stepPairs.add(su);
            }
        }
        
        String statementFigure = _meta.getFigure();
        final SaveSolutionStepsAdminAction action = new SaveSolutionStepsAdminAction(Login.getInstance().getUser().getUserName(), _meta.getMd5OnRead(), asPid,statement,statementFigure,stepPairs,_meta.getTutorDefine(),_meta.isActive());
        action.setFromPid(_meta.getPid());
        saveSolution(action, asPid);
    }
    
    public void saveSolution(final SaveSolutionStepsAdminAction action, final String asPid) {
        SolutionEditor.__status.setBusy("Saving solution ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData solutionResponse) {
                SolutionEditor.__status.clearStatus("");
                EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_SAVED, solutionResponse));
                
                _meta.setMd5OnRead(solutionResponse.getDataAsString("md5"));
                
                if(!SolutionEditor.__pidToLoad.equals(asPid)) {
                    loadSolution(asPid);
                }
                
            }
            @Override
            public void onFailure(Throwable arg0) {
                SolutionEditor.__status.clearStatus("");
                arg0.printStackTrace();
                
                if(arg0.getLocalizedMessage().contains("has been modified")) {
                    CmMessageBox.confirm("Force Solution Change", "Solution has been changed since last read.  Do you want to overwrite?",new ConfirmCallback() {
						@Override
						public void confirmed(boolean yesNo) {
							if(yesNo) {
                                action.setForceWrite(true);
                                saveSolution(action, asPid);
                            }
                        }
                    });                    
                }
                else {
                    Window.alert(arg0.getLocalizedMessage());
                }
            }
        });        
    }
    
    public void loadSolution(final String pid) {
        SolutionEditor.__status.setText("Loading solution: " + pid);
        LoadSolutionMetaAction action = new LoadSolutionMetaAction(pid);
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionMeta>() {
            @Override
            public void onSuccess(SolutionMeta meta) {
                SolutionEditor.__status.setText("");
                buildSolutionEditor(meta);
            }
            @Override
            public void onFailure(Throwable arg0) {
                SolutionEditor.__status.setText("");
                arg0.printStackTrace();
                Window.alert(arg0.getMessage());
            }
        });
        
        
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.SOLUTION_LOAD_COMPLETE, pid));
    }
    


    final static boolean shouldProcessMathJax = true;
    static {
        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType().equals(EventTypes.POST_SOLUTION_LOAD)) {
                    if(shouldProcessMathJax)
                        mathJaxProcess();
                    __instance.initializeTutorInEditor();
                }
                else if(event.getEventType().equals(hotmath.gwt.solution_editor.client.EventTypes.STEP_CONTAINER_SELECTED)) {
                    __instance.setStepContainerSelected((StepContainer)event.getEventData());
                }
            }
        });
    }
    
    
    static private native void mathJaxProcess() /*-{
         $wnd.processMathJax();
    }-*/;
    
    
    /** Create spacer for toolbar
     * 
     * @author casey
     *
     */
    class Spacer extends HTML {
        public Spacer() {
            super("<div style='width: 40px;'>&nbsp;</div>");
        }
    }


    /** Return just text portion of all steps
     * 
     * @return
     */
    public String getSolutionStepsText() {
        String text="";
        for(int i=0,t=_mainFlow.getWidgetCount();i<t;i++) {
            Widget comp = _mainFlow.getWidget(i);
            if(comp instanceof StatementContainer) {
                text += ((StatementContainer)comp).getStepUnitWrapper().getItem().getEditorText();            
            }
            else if(comp instanceof StepContainer) {
                StepContainer sc = (StepContainer)comp;
                StepUnitPair  su = new StepUnitPair(sc.getHintText(), sc.getStepText(), sc.getFigure());
                text += su.toString();
            }
        }
        return text;
    }
 
}


