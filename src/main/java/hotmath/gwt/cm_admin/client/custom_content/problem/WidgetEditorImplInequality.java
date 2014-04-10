package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.form.TextField;

public class WidgetEditorImplInequality extends SimplePanel implements WidgetEditor {

    TextField _inequalityDef;
    private WidgetDefModel widgetDef;

    public WidgetEditorImplInequality(WidgetDefModel widgetDef) {
        this.widgetDef = widgetDef;
    }
    
    
    @Override
    public String checkValid() {
        String val = _inequalityDef.getCurrentValue();
        if(val == null || val.length() == 0) {
            return "Must be specified";
        }
        return null;
    }

    @Override
    public Widget asWidget() {
        _inequalityDef = new TextField();
        _inequalityDef.setToolTip("Set an inequality, for example x > -3");
        _inequalityDef.setValue(widgetDef.getValue()!=null?URL.decode(widgetDef.getValue()):null);
        setWidget(new MyFieldLabel(_inequalityDef, "Inequality", 80,  100));
        return this;
    }

    @Override
    public String getWidgetJson() {
        String val = _inequalityDef.getCurrentValue() != null?URL.encode(_inequalityDef.getCurrentValue()):null;
        widgetDef.setType("inequality");
        widgetDef.setValue(val);
        return widgetDef.getJson();
    }

    @Override
    public WidgetDefModel getWidgetDef() {
        return widgetDef;
    }
    
    @Override
    public String getDescription() {
    	return "Enter an inequality";
    }
    
    @Override
    public String getValueLabel() {
    	return "Correct Value";
    }
}
