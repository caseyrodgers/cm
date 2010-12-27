package hotmath.gwt.solution_editor.client;

import com.google.gwt.user.client.ui.Widget;


public class ProblemStatement extends EditableStepUnit implements StepUnitItem {
    
    public ProblemStatement(String text) {
        super();
        addStyleName("problem-statement");
        setEditorText(text);
    }
    
    public Widget getWidget() {
        return this;
    }
}
