package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;

import com.google.gwt.user.client.ui.HTML;


public class WidgetEditorImplNoWidget extends WidgetEditorImplBase {

    public WidgetEditorImplNoWidget(WidgetDefModel widgetDef) {
        super(widgetDef);
        
        initWidget(new HTML("<h1>No Widget</h1>"));
    }

    @Override
    public String getWidgetJson() {
        return null;
    }


}
