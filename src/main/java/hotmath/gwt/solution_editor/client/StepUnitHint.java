package hotmath.gwt.solution_editor.client;

import hotmath.gwt.solution_editor.client.rpc.SolutionMetaStep;

import com.google.gwt.user.client.ui.Widget;


public class StepUnitHint extends EditableStepUnit implements StepUnitItem  {
    
    SolutionMetaStep step;
    public StepUnitHint(SolutionMetaStep step) {
        super();
        this.step = step;
        addStyleName("step-unit-hint");
        setEditorText(step.getHint());
    }
    
    @Override
    public Widget getWidget() {
        return this;
    }
    
    @Override
    public void setEditorText(String text) {
        super.setEditorText(text);
        step.setHint(text);
    }

}
