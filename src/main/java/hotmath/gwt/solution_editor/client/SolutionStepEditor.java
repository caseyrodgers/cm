
package hotmath.gwt.solution_editor.client;



import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.CmEventListener;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.WidgetListDialog.Callback;
import hotmath.gwt.solution_editor.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.solution_editor.client.rpc.SaveSolutionStepsAdminAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionMeta;
import hotmath.gwt.solution_editor.client.rpc.SolutionMetaStep;

import java.util.ArrayList;
import java.util.List;

import org.jfree.util.Log;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;


public class SolutionStepEditor extends ContentPanel {
    SolutionMeta _meta;
    static SolutionStepEditor __instance;
    StepContainer _selectedContainer;
    SolutionMetaStep _cutStep;
    
    LayoutContainer _mainContainer = new LayoutContainer();
    String _statement;
    CheckBox isActiveCheckBox = new CheckBox();

    public SolutionStepEditor() {
        __instance = this;
        setStyleName("solution-step-editor");
        setScrollMode(Scroll.AUTOY);
        add(new Label("No solution loaded."));


        
        isActiveCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                _meta.setActive(isActiveCheckBox.getValue());
                EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED,_meta));                
            }
        });
        
        
        HorizontalPanel pp = new HorizontalPanel();
        Label l = new Label("Active");
        l.getElement().setAttribute("style", "font-size: .7em;margin: 3px 3px 0 20px;");
        pp.add(l);
        pp.add(isActiveCheckBox);

        getHeader().addTool(pp);
        getHeader().addTool(new Spacer());        
                
        getHeader().addTool(new Button("Define", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                showDefineEditor();
            }
        }));

        getHeader().addTool(new Button("Widget", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                showWidgetEditor();
            }
        }));
        
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
    
    public SolutionMeta getMeta() {
        return _meta;
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
        _meta.getSteps().add(new SolutionMetaStep(_meta,"A New Hint", "A New Step",null));
        buildSolutionEditor(_meta);
        
        fireChanged();
    }
    
    public void showWidgetEditor() {
        String problemStatement = _meta.getProblemStatement();
        String widgetJson = WidgetListDialog.extractWidgetJson(problemStatement);
        WidgetListDialog.showWidgetListDialog(new Callback() {
            @Override
            public void resourceSelected(WidgetDefModel widget) {
                if(widget == null)
                    return;
                
                String ps = WidgetListDialog.stripWidgetFromHtml(_meta.getProblemStatement());
                ps += widget.getWidgetHtml();
                _meta.setProblemStatement(ps);
                buildSolutionEditor(_meta);
                
                fireChanged();
            }
        }, widgetJson);
    }
    
    public void showDefineEditor() {
        new StepEditorDefineDialog(_meta.getTutorDefine());
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
        removeAll();

        
        isActiveCheckBox.setValue(meta.isActive());
        
        setHeading("Solution Step Editor");
        add(new StatementContainer("Problem Statement", new ProblemStatement(meta)));
        
        for(int s=0,t=meta.getSteps().size();s<t;s++) {
            add(new StepContainer(meta.getPid(),(s+1), meta,meta.getSteps().get(s),meta.getSteps().get(s).getFigure()));
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
                steps.add(new SolutionMetaStep(_meta,sc.getHintText(),sc.getStepText(), sc.getFigure()));
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
        final SaveSolutionStepsAdminAction action = new SaveSolutionStepsAdminAction(_meta.getMd5OnRead(), asPid,statement,statementFigure,stepPairs,_meta.getTutorDefine(),_meta.isActive());
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
                    
                    MessageBox.confirm("Force Solution Change", "Solution has been changed since last read.  Do you want to overwrite?",new Listener<MessageBoxEvent>() {
                        
                        @Override
                        public void handleEvent(MessageBoxEvent be) {
                            if(be.getButtonClicked().getText().equals("Yes")) {
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
 
}


class Spacer extends Label {
    public Spacer() {
        setWidth(400);
    }
}