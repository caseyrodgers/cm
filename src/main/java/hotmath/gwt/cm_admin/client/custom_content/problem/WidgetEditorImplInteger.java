package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import com.sencha.gxt.widget.core.client.form.TextField;


public class WidgetEditorImplInteger extends WidgetEditorImplDefault {

    TextField _correctIntegerValue;
	private String description;
    public WidgetEditorImplInteger(WidgetDefModel widgetDef, String description) {
        super(widgetDef);
        this.description = description;
    }
    
    protected void buildUi() {
        _correctIntegerValue = new NumericalTextField();
        _correctIntegerValue.setValue(getIntValueAsString(_widgetDef.getValue()!=null?_widgetDef.getValue():""));
        _fields.add(new MyFieldLabel(_correctIntegerValue, "Integer",80, 60));
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
    
    @Override
    public String checkValid() {
    	if(!_correctIntegerValue.validate()) {
    		return "Is invalid";
    	}
        return null;
    }
    
    @Override
    public String getDescription() {
    	return description;
    }
    
}
