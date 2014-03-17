package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import com.sencha.gxt.widget.core.client.form.TextField;


public class WidgetEditorImplInteger extends WidgetEditorImplBase {

    TextField _correctIntegerValue;
    public WidgetEditorImplInteger(WidgetDefModel widgetDef) {
        super(widgetDef);
    }
    
    protected void buildUi() {
        _correctIntegerValue = new TextField();
        _correctIntegerValue.setValue(_widgetDef.getValue()!=null?_widgetDef.getValue():"");
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
    
    @Override
    public String checkValid() {
        
        String currVal = _correctIntegerValue.getCurrentValue();
        if(currVal == null || currVal.length() == 0) {
            return "Must be specified";
        }
        
        try {
            int val = Integer.parseInt(currVal);
        }
        catch(Exception e) {
            return "Invalid value";
        }
        
        return null;
    }
    
}
