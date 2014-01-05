package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.TextField;


public class WidgetEditorImplInteger extends WidgetEditorImplBase {

    TextField _correctIntegerValue = new TextField();
    private WidgetDefModel widgetDef;
    public WidgetEditorImplInteger(WidgetDefModel widgetDef) {
        super(widgetDef);
        this.widgetDef = widgetDef;
    }

    @Override
    public Widget asWidget() {
        buildUi();
        return this;
    }
    
    private void buildUi() {
        VerticalLayoutContainer fields = new VerticalLayoutContainer();
        
        _correctIntegerValue.setValue(widgetDef.getValue());
        
        fields.add(new MyFieldLabel(_correctIntegerValue, "Correct Value",80, 100));
        
        initWidget(fields);
    }
    
    @Override
    public WidgetDefModel getWidgetDef() {
        return widgetDef;
    }
    
    
    @Override
    public String getWidgetJson() {
        WidgetDefModel widget = new WidgetDefModel();
        widget.setType(widgetDef.getType());
        widget.setValue(_correctIntegerValue.getCurrentValue());
        
        return widget.getJson();
    }
    
}
