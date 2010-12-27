package hotmath.gwt.solution_editor.client;

import com.google.gwt.user.client.ui.Widget;


public class StepUnitText extends EditableStepUnit {
    
    public StepUnitText(String text) {
        super();
        addStyleName("step-unit-text");
        setEditorText(text);
    }
    
    @Override
    public Widget getWidget() {
        // TODO Auto-generated method stub
        return this;
    }
}
