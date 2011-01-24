
package hotmath.gwt.solution_editor.client;



import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.CmEventListener;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.solution_editor.client.rpc.SaveSolutionStepsAdminAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionMeta;
import hotmath.gwt.solution_editor.client.rpc.SolutionMetaStep;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

public class SolutionStepEditor extends ContentPanel {
    SolutionMeta _meta;
    static SolutionStepEditor __instance;
    StepContainer _selectedContainer;
    SolutionMetaStep _cutStep;
    
    LayoutContainer _mainContainer = new LayoutContainer();
    String _statement;

    public SolutionStepEditor() {
        __instance = this;
        setStyleName("solution-step-editor");
        setScrollMode(Scroll.AUTOY);
        add(new Label("No solution loaded."));


        getHeader().addTool(new Button("Cut Step", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                cutSelectedStep();
            }
        }));
        
        getHeader().addTool(new Button("Paste Step", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                pasteCutStep();
            }
        }));

        
        getHeader().addTool(new Button("Add Step", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                addNewStep();
            }
        }));
        
        getHeader().addTool(new Button("Info", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                showInfo();
            }
        }));
    }
    
    public String getStatement() {
        return _statement;
    }
    
    private void showInfo() {
       new ShowValueWindow("Loaded Problem",_meta.getPid());
    }
    
    public List<StepUnitPair> getSteps() {
        return null;
    }
    
    private void addNewStep() {
        flushChanges();
        _meta.getSteps().add(new SolutionMetaStep("A New Hint", "A New Step"));
        buildSolutionEditor(_meta);
        
        fireChanged();
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
        this._meta = meta;
        removeAll();

        setHeading("Solution Step Editor");
        add(new StepUnitWrapper("Problem Statement", new ProblemStatement(meta)));
        
        for(int s=0,t=meta.getSteps().size();s<t;s++) {
            add(new StepContainer((s+1), meta.getSteps().get(s)));
        }
        layout();
        
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.POST_SOLUTION_LOAD));
    }
    
    private native void initializeTutorInEditor() /*-{
        $wnd.initializeSolutionEditor();
    }-*/;
    
    private void setStepContainerSelected(StepContainer container) {
        _selectedContainer = container;
        List<Component> items = getItems();
        for(int i=0,t=items.size();i<t;i++) {
            Component comp = items.get(i);
            if(comp instanceof StepContainer) {
                comp.el().removeStyleName("selected");
            }
        }
        container.el().addStyleName("selected");
    }
    
    private void flushChanges() {
        
        if(true)
            return;
        
        List<SolutionMetaStep> steps = new ArrayList<SolutionMetaStep>();
        List<Component> items = getItems();
        for(int i=0,t=items.size();i<t;i++) {
            Component comp = items.get(i);
            if(comp instanceof StepUnitWrapper) {
                String text = ((StepUnitWrapper)comp).getItem().getEditorText();
                _meta.setProblemStatement(text);
            }
            else if(comp instanceof StepContainer) {
                StepContainer sc = (StepContainer)comp;
                steps.add(new SolutionMetaStep(sc.getHintText(),sc.getStepText()));
            }
        }
        _meta.setSteps(steps );
    }
    
    public void saveStepChanges(String asPid) {

        List<StepUnitPair> stepPairs = new ArrayList<StepUnitPair>();

        String statement="";
        List<Component> items = getItems();
        for(int i=0,t=items.size();i<t;i++) {
            Component comp = items.get(i);
            if(comp instanceof StepUnitWrapper) {
                String text = ((StepUnitWrapper)comp).getItem().getEditorText();
                statement = text;            
            }
            else if(comp instanceof StepContainer) {
                StepContainer sc = (StepContainer)comp;
                StepUnitPair  su = new StepUnitPair(sc.getHintText(), sc.getStepText());
                stepPairs.add(su);
            }
        }
        
        SaveSolutionStepsAdminAction action = new SaveSolutionStepsAdminAction(asPid,statement,stepPairs);
        SolutionEditor.__status.setBusy("Saving solution ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData solutionResponse) {
                SolutionEditor.__status.clearStatus("");
                EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_SAVED, solutionResponse));
            }
            @Override
            public void onFailure(Throwable arg0) {
                SolutionEditor.__status.clearStatus("");
                arg0.printStackTrace();
                Window.alert(arg0.getLocalizedMessage());
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
}
