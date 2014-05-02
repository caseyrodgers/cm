package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyValidators;

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
    
    
    @Override
    public void setupValue() {
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
        xField.setValue(x);
        yField.setValue(y);
    }
    
    
    private void buildUi() {
        VerticalLayoutContainer fieldsPanel = new VerticalLayoutContainer();

        xField = new DecimalTextField();
        yField = new DecimalTextField();
        
        fieldsPanel.add(new MyFieldLabel(xField, "X", 30, 60));
        fieldsPanel.add(new MyFieldLabel(yField, "Y", 30, 60));
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
            Double.parseDouble(xv);
            Double.parseDouble(yv);
        }
        catch(Exception e) {
            return "Invalid values";
        }
        return null;
    }
    
    @Override
    public String getValueLabel() {
    	return null;
    }

    @Override
    public String getWidgetTypeLabel() {
    	return "Ordered Pair";
    }
    
    @Override
    public String getWidgetValueLabel() {
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
        return x + ", " + y;
    }
}
