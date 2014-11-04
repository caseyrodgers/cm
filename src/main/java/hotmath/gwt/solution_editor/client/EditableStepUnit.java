package hotmath.gwt.solution_editor.client;


import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

abstract public class EditableStepUnit extends FlowLayoutContainer implements StepUnitItem {
    
    String editorText;
    
    HTML htmlDiv = new HTML();
    public EditableStepUnit() {
        addStyleName("editable-step-unit");
        clear();
        add(htmlDiv);
    }
    
    public void setEditorText(String text) {
        editorText = text;
        htmlDiv.setHTML(text);
    }
    
    public String getEditorText() {
        return editorText;
    }
    
    @Override
    abstract public Widget getWidget();
}
