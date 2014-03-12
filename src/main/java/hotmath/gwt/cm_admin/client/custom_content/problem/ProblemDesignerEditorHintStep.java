package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc.client.model.SolutionMetaStep;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction.SaveType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class ProblemDesignerEditorHintStep extends GWindow {

    private SolutionInfo _solution;
    private CallbackOnComplete callback;
    private SolutionMeta _solutionMeta;
    private int whichStepHint;
    private SolutionMetaStep _hintStep;

    static final int NEW_HINTSTEP=-1;

    public ProblemDesignerEditorHintStep(SolutionInfo solutionInfo, SolutionMeta solutionMeta, int whichStepHint, CallbackOnComplete callback) {
        super(false);
        this._solution = solutionInfo;
        this._solutionMeta = solutionMeta;
        this.whichStepHint = whichStepHint;
        this.callback = callback;
        
        setHeadingText("Step Hint Editor");
        
        if(whichStepHint == NEW_HINTSTEP) {
            _hintStep = new SolutionMetaStep();
        }
        else {
            _hintStep = _solutionMeta.getSteps().get(whichStepHint);
        }
        
        buildUi();
        
        addButton(new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doSave();
            }
        }));
        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        
        setVisible(true);
    }
    
    

    private void doSave() {
        if(_hintField.getValue() == null || _stepField.getValue() == null || _hintField.getValue().length() == 0 || _stepField.getValue().length() == 0) {
            CmMessageBox.showAlert("Both hint and step must be specified");
            return;
        }
        
        
        _hintStep.setHint(_hintField.getCurrentValue());
        _hintStep.setText(_stepField.getCurrentValue());
        
        
        if(whichStepHint == NEW_HINTSTEP) {
            _solutionMeta.getSteps().add(_hintStep);
        }
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                SaveCustomProblemAction action = new SaveCustomProblemAction(_solution.getPid(), SaveType.HINTSTEP, _solutionMeta);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }
            public void oncapture(RpcData value) {
                CmBusyManager.setBusy(false);
                Log.info("Hint saved");
                callback.isComplete();
                hide();
            }
        }.register();

    }
    

    private TextArea _hintField = new TextArea();
    private TextArea _stepField = new TextArea();
    private void buildUi2() {
       VerticalLayoutContainer vert = new VerticalLayoutContainer();
       _hintField.setHeight(100);
       _stepField.setHeight(100);
       
       _hintField.setValue(this._hintStep.getHint());
       _stepField.setValue(this._hintStep.getText());
       
       vert.add(new FieldLabel(_hintField, "Hint Text"));
       vert.add(new FieldLabel(_stepField, "Step Text"));
       
       FramedPanel fp = new FramedPanel();
       fp.setHeaderVisible(false);
       
       fp.setWidget(vert);
       
       setWidget(fp);
    }
    
    
    private void buildUi() {
        
        BorderLayoutContainer bC = new BorderLayoutContainer();
        _hintField.setValue(this._hintStep.getHint());
        _stepField.setValue(this._hintStep.getText());
        
        
        addTool(new TextButton("Edit", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                new CpEditingArea("hint");
            }
        }));
        
        BorderLayoutData bD = new BorderLayoutData(.30);
        bD.setSplit(true);
        bC.setNorthWidget(new MyFieldLabel(_hintField, "Hint Text", 100), bD);
        bC.setCenterWidget(new MyFieldLabel(_stepField, "Step Text", 100));
        
        FramedPanel fp = new FramedPanel();
        fp.setHeaderVisible(false);
        
        fp.setWidget(bC);
        
        setWidget(fp);
     }





    static public void doTest() {
        SolutionMeta meta = new SolutionMeta();
        meta.getSteps().add(new SolutionMetaStep(new SolutionMeta(), "Hint", "Step",null));
        new ProblemDesignerEditorHintStep(new SolutionInfo(), meta,0, new CallbackOnComplete() {
            @Override
            public void isComplete() {
            }
        });
    }

}
