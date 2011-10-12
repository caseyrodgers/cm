package hotmath.gwt.solution_editor.client;

import hotmath.gwt.solution_editor.client.rpc.SolutionMeta;

import com.google.gwt.user.client.ui.Widget;


public class ProblemStatement extends EditableStepUnit implements StepUnitItem {
    
    SolutionMeta meta;
    public ProblemStatement(SolutionMeta meta) {
        this.meta = meta;
        addStyleName("problem-statement");
        setEditorText(meta.getProblemStatement());
    }
    
    public SolutionMeta getMeta() {
        return meta;
    }
    
    public Widget getWidget() {
        return this;
    }

    
    @Override
    public void setEditorText(String text) {
        super.setEditorText(text);
        meta.setProblemStatement(text);
    }

}
