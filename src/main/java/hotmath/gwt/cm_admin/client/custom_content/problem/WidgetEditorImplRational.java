package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import com.sencha.gxt.widget.core.client.form.TextField;


public class WidgetEditorImplRational extends WidgetEditorImplFraction {

    TextField _format = new TextField();

    public WidgetEditorImplRational(WidgetDefModel widgetDef) {
        super(widgetDef);
    }

    protected void buildUi() {
        super.buildUi();
        
        String format = getWidgetDef().getFormat();
        if(format !=null) {
            String p[] = format.split("\\|");
            format = p.length > 0?p[0]:null;
        }
        _format.setValue(format);
        super._fields.add(new MyFieldLabel(_format, "Format",80, 80));
    }
    @Override
    protected WidgetDefModel createWidgetDefModel() {
        WidgetDefModel wd = super.createWidgetDefModel();
        wd.setType("number_rational");
        wd.setFormat(_format.getCurrentValue()!=null?_format.getCurrentValue():null);
        return wd;
    }
    
}
