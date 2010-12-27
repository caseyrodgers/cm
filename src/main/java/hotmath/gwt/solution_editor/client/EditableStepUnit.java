package hotmath.gwt.solution_editor.client;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.Widget;

abstract public class EditableStepUnit extends LayoutContainer implements StepUnitItem {
    String html;
    
    public EditableStepUnit() {
        addStyleName("editable-step-unit");

    }
    
    public void setEditorText(String text) {
       this.html = text;
       removeAll();
       add(new Html(text));
       layout();
    }
    
    public String getEditorText() {
        return this.html; 
    }
    
    @Override
    abstract public Widget getWidget();
}
