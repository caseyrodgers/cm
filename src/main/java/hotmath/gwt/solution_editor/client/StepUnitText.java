package hotmath.gwt.solution_editor.client;

import hotmath.gwt.solution_editor.client.rpc.SolutionMetaStep;

import com.google.gwt.user.client.ui.Widget;


public class StepUnitText extends EditableStepUnit {
    
    SolutionMetaStep step;
    public StepUnitText(SolutionMetaStep step) {
        super();
        this.step = step;
        addStyleName("step-unit-text");
        setEditorText(step.getText());
    }
    
    @Override
    public Widget getWidget() {
        return this;
    }
    
    @Override
    public void setEditorText(String text) {
        super.setEditorText(text);
        step.setText(text);
    }
}
