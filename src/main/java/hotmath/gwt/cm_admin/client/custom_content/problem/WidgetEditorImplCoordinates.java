package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.TextField;

public class WidgetEditorImplCoordinates extends SimplePanel implements WidgetEditor {
    
    
    protected WidgetDefModel widgetDef;
    protected TextField xField;
    protected TextField yField;

    public WidgetEditorImplCoordinates(WidgetDefModel widgetDef) {
        this.widgetDef = widgetDef;
    }

    @Override
    public Widget asWidget() {
        buildUi();
        return this;
    }
    
    private void buildUi() {
        VerticalLayoutContainer fieldsPanel = new VerticalLayoutContainer();
        
        String vals = widgetDef.getValue();
        String x="";
        String y="";
        if(vals != null) {
            String p[] = vals.split("\\|");
            if(p.length == 2) {
                x = p[0];
                y = p[1];
            }
        }
        xField = new NumericalTextField();
        xField.setValue(x);
        
        yField = new NumericalTextField();
        yField.setValue(y);
        
        fieldsPanel.add(new MyFieldLabel(xField, "X", 30, 50));
        fieldsPanel.add(new MyFieldLabel(yField, "Y", 30, 50));
        setWidget(fieldsPanel);
    }
    
    @Override
    public String getDescription() {
    	return "Enter an x-coordinate and a y-coordinate. Decimals and fractions are accepted. Correct answers not in lowest terms are still marked correct.";
    }

    @Override
    public String getWidgetJson() {
        widgetDef.setType("coordinates");
        widgetDef.setValue(xField.getCurrentValue() + "|" + yField.getCurrentValue());
        return widgetDef.getJson();
    }

    @Override
    public WidgetDefModel getWidgetDef() {
        return widgetDef;
    }

    @Override
    public String checkValid() {
    	if(!xField.validate() || !yField.validate()) {
    		return "Invalid coordinates";
    	}
    	
        String xv = xField.getCurrentValue();
        String yv = yField.getCurrentValue();
        if(xv == null || yv == null || xv.length() == 0 || yv.length() == 0) {
            return "Both X and Y need to be specified";
        }
        try {
            Integer.parseInt(xv);
            Integer.parseInt(yv);
        }
        catch(Exception e) {
            return "Invalid values";
        }
        return null;
    }

}
