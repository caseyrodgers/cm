package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;


public class WidgetEditorImplMixedFraction extends WidgetEditorImplRational {

    IntegerTextField _whole = new IntegerTextField("Whole part");
    IntegerTextField _numeratorPart = new IntegerTextField("Numerator of fractional part");
    IntegerTextField _denominatorPart = new IntegerTextField("Demoniator of fractional part");
    public WidgetEditorImplMixedFraction(WidgetDefModel widgetDef) {
        super(widgetDef);
    }

    protected void buildUi() {
        
    	_whole.setAllowBlank(false);
    	_numeratorPart.setAllowBlank(false);
    	_denominatorPart.setAllowBlank(false);
    	
        super.buildUi();
        VerticalLayoutContainer panWhole = new VerticalLayoutContainer();
        panWhole.addStyleName("widget_panel");
        panWhole.add(new MyFieldLabel(_whole, "Whole Part",80, 60));
        panWhole.add(new MyFieldLabel(_numeratorPart, "Numerator",80, 60));
        panWhole.add(new MyFieldLabel(_denominatorPart, "Denominator",80, 60));
        
        _tabPanel.add(panWhole, "Whole");
        
        super.setFormatValue(_widgetDef.getFormat());
    }
    
    @Override
    public void setupValue() {
        String value = getWidgetDef().getValue();
        if(value !=null) {
            String p[] = value.split("\\]");
            String toSplit="";
            if(p.length > 1) {
            	_tabPanel.setActiveWidget(_tabPanel.getWidget(2));
            	
            	_whole.setValue(p[0].substring(1));
            	p = p[1].split("/");
            	_numeratorPart.setValue(p[0]);
        		_denominatorPart.setValue(p[1]);
            	
            }
            else {
            	super.setupValue();
            }
        }
    }
    
    private boolean isWhole() {
		return !super.isFraction() && !super.isDecimal();
	}
    
    @Override
    public String checkValid() {
    	
    	if(!isWhole()) {
    		return super.checkValid();
    	}
    	
    	if(!_whole.validate() || !_numeratorPart.validate() || !_denominatorPart.validate()) {
    		return "Invalid";
    	}
    	
    	int d = Integer.parseInt(_denominatorPart.getCurrentValue());
    	if(d == 0) {
    		return "Denominator must not be zero.";
    	}
    	
    	int n = Integer.parseInt(_numeratorPart.getCurrentValue());
    	if(n == 0) {
    		return "Use the decimal tab for integer values.";
    	}
    			
    	if(n >= d) {
    		return "Numerator must be less than denominator";
    	}
    	
    	if(n < 0) {
    		return "Numerator cannot be negative.";
    	}
    	if(d < 0) {
    		return "Denominator cannot be negative.";
    	}
    	
    	return null;
    }

    @Override
    public String getDescription() {
    	return "Enter an integer, decimal number, fraction (use \"/\"), or mixed number (use \"m\"). " +
               "If a fraction is correct but not in lowest terms, students are prompted to reduce.  " +
    		   "Improper fractions should be written as mixed numbers. Answers must be exact.";
    }
    
    @Override
    protected WidgetDefModel createWidgetDefModel() {
        WidgetDefModel wd = super.createWidgetDefModel();
        
        if(isWhole()) {
        	wd.setValue("[" + (_whole.getCurrentValue()!=null?_whole.getCurrentValue() :0) + "]" + _numeratorPart.getCurrentValue() + "/" + _denominatorPart.getCurrentValue());
        }
        
        wd.setType("number_rational_mixed");
        wd.setAnsFormat("lowest_term");
        wd.setAllowMixed(true);
        return wd;
    }
    
    @Override
    public String getValueLabel() {
    	return "Correct Value (decimal, fraction, or mixed number)";
    }
    
    @Override
    public String getWidgetTypeLabel() {
    	return "Mixed Fraction";
    }
    
}
