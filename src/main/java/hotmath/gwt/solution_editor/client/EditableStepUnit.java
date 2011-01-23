package hotmath.gwt.solution_editor.client;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.Widget;

abstract public class EditableStepUnit extends LayoutContainer implements StepUnitItem {
    
    String editorText;
    
    Html htmlDiv = new Html();
    public EditableStepUnit() {
        addStyleName("editable-step-unit");
        removeAll();
        add(htmlDiv);
    }
    
    public void setEditorText(String text) {
        editorText = text;
        htmlDiv.setHtml(text);
        layout();
    }
    
    public String getEditorText() {
        return editorText;
    }
    
    @Override
    abstract public Widget getWidget();
}
