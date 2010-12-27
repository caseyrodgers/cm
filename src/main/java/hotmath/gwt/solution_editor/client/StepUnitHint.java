package hotmath.gwt.solution_editor.client;

import com.google.gwt.user.client.ui.Widget;


public class StepUnitHint extends EditableStepUnit implements StepUnitItem  {
    
    public StepUnitHint(String text) {
        super();
        addStyleName("step-unit-hint");
        setEditorText(text);
    }
    
    @Override
    public Widget getWidget() {
        // TODO Auto-generated method stub
        return this;
    }
}
