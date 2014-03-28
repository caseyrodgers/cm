package hotmath.gwt.cm_admin.client.custom_content.problem;

import com.sencha.gxt.widget.core.client.form.TextField;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;

public class WidgetEditorImplFraction extends WidgetEditorImplDefault {

    TextField _numerator;
    TextField _denominator;

    public WidgetEditorImplFraction(WidgetDefModel widgetDef) {
        super(widgetDef);
    }

    @Override
    protected void buildUi() {
        _numerator = new TextField();
        _denominator = new TextField();
        
        
        String value = getWidgetDef().getValue();
        String p[] = value!=null?value.split("/"):new String[1];
        
        _numerator.setValue( getIntValueAsString(p[0]));
        _denominator.setValue(p.length > 1?getIntValueAsString(p[1]):"");
        
        _fields.add(new MyFieldLabel(_numerator, "Numerator", 80, 80));
        _fields.add(new MyFieldLabel(_denominator, "Denominator", 80,80));
    }

    @Override
    protected WidgetDefModel createWidgetDefModel() {
        WidgetDefModel wd = super.createWidgetDefModel();
        
        wd.setValue(_numerator.getCurrentValue() + "/" + _denominator.getCurrentValue());
        return wd;
    }
    
    @Override
    public String getDescription() {
    	return "Enter a fraction";
    }
    
    @Override
    protected String getWidgetType() {
        return "number_fraction";
    }

    @Override
    public String checkValid() {
        try {
            if(_numerator.getCurrentValue() != null) {
                Integer.parseInt(_numerator.getCurrentValue());
            }
            if(_denominator.getCurrentValue() != null) {
                Integer.parseInt(_denominator.getCurrentValue());
            }
        }
        catch(Exception e) {
            return "Invalid fraction values.  Make sure both are integers";
        }
        return null;
    }
}
