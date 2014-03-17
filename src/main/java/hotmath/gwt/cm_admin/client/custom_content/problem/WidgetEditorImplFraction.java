package hotmath.gwt.cm_admin.client.custom_content.problem;

import com.sencha.gxt.widget.core.client.form.TextField;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;

public class WidgetEditorImplFraction extends WidgetEditorImplBase {

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
        String p[] = value.split("/");
        
        _numerator.setValue(p[0]);
        _denominator.setValue(p.length > 1?p[1]:"");
        _fields.add(new MyFieldLabel(_numerator, "Numerator", 70));
        _fields.add(new MyFieldLabel(_denominator, "Denominator", 70));
    }

    @Override
    protected WidgetDefModel createWidgetDefModel() {
        WidgetDefModel wd = super.createWidgetDefModel();
        
        wd.setValue(_numerator.getCurrentValue() + "/" + _denominator.getCurrentValue());
        return wd;
    }
    
    @Override
    protected String getWidgetType() {
        return "number_fraction";
    }

    @Override
    public String checkValid() {
        return null;
    }
}
