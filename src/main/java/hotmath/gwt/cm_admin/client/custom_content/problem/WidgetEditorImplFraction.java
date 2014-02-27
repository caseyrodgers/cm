package hotmath.gwt.cm_admin.client.custom_content.problem;

import com.sencha.gxt.widget.core.client.form.TextField;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;

public class WidgetEditorImplFraction extends WidgetEditorImplBase {

    TextField _numerator = new TextField();
    TextField _denominator = new TextField();

    public WidgetEditorImplFraction(WidgetDefModel widgetDef) {
        super(widgetDef);
    }

    @Override
    protected void buildUi() {
        
        _fields.add(new MyFieldLabel(_numerator, "Numerator", 100, 200));
        _fields.add(new MyFieldLabel(_denominator, "Denominator", 100, 200));
    }

    @Override
    protected WidgetDefModel createWidgetDefModel() {
        WidgetDefModel wd = super.createWidgetDefModel();
        
        wd.setValue(_denominator.getCurrentValue() + "/" + _numerator.getCurrentValue());
        return wd;
    }
    
    @Override
    protected String getWidgetType() {
        return "number_fraction";
    }
}
