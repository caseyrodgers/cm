package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.form.TextField;


public class WidgetEditorImplInteger extends WidgetEditorImplBase {

    TextField _correctIntegerValue = new TextField();
    public WidgetEditorImplInteger(WidgetDefModel widgetDef) {
        super(widgetDef);
    }

    @Override
    public Widget asWidget() {
        buildUi();
        return this;
    }

    protected void buildUi() {
        _correctIntegerValue.setValue(_widgetDef.getValue());
        _fields.add(new MyFieldLabel(_correctIntegerValue, "Correct Value",80, 100));
    }
    
    protected WidgetDefModel createWidgetDefModel() {
        WidgetDefModel widget = super.createWidgetDefModel();
        widget.setValue(_correctIntegerValue.getCurrentValue());
        return widget;
    }

    @Override
    protected String getWidgetType() {
        return "number_integer";
    }
    
}
