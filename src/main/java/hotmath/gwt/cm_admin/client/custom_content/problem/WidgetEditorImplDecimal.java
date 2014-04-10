package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.ui.MyValidators;

import com.sencha.gxt.widget.core.client.form.TextField;


public class WidgetEditorImplDecimal extends WidgetEditorImplDefault {

    TextField _correctDecimalValue;
	private String description;
    public WidgetEditorImplDecimal(WidgetDefModel widgetDef, String description) {
        super(widgetDef);
        this.description = description;
    }
    
    protected void buildUi() {
    	_correctDecimalValue = new NumericalTextField(MyValidators.DECIMAL);
    	_correctDecimalValue.setValue(getIntValueAsString(_widgetDef.getValue()!=null?_widgetDef.getValue():""));
        _fields.add(new MyFieldLabel(_correctDecimalValue, "Decimal: ",80, 100));
    }

    protected WidgetDefModel createWidgetDefModel() {
        WidgetDefModel widget = super.createWidgetDefModel();
        widget.setValue(_correctDecimalValue.getCurrentValue());
        return widget;
    }

    @Override
    protected String getWidgetType() {
        return "number_decimal";
    }
    
    @Override
    public String checkValid() {
    	if(!_correctDecimalValue.validate()) {
    		return "Is invalid";
    	}
        return null;
    }
    
    @Override
    public String getDescription() {
    	return description;
    }
    
}
