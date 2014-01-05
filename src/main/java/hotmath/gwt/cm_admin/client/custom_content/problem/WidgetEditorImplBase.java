package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Composite;

public abstract class WidgetEditorImplBase extends Composite implements WidgetEditor {

    private WidgetDefModel widgetDef;

    public WidgetEditorImplBase(WidgetDefModel widgetDef) {
        this.widgetDef = widgetDef;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public WidgetDefModel getWidgetDef() {
        return widgetDef;
    }

}
