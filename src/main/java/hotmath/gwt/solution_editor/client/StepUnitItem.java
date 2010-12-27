package hotmath.gwt.solution_editor.client;

import com.google.gwt.user.client.ui.Widget;

public interface StepUnitItem {
    
    String getEditorText();
    
    Widget getWidget();
    
    void setEditorText(String text);

}
