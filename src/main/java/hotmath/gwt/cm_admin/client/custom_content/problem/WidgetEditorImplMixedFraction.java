package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import com.sencha.gxt.widget.core.client.form.TextField;


public class WidgetEditorImplMixedFraction extends WidgetEditorImplRational {

    TextField _wholeNumber = new TextField();

    public WidgetEditorImplMixedFraction(WidgetDefModel widgetDef) {
        super(widgetDef);
    }

    protected void buildUi() {
        super._fields.add(new MyFieldLabel(_wholeNumber, "Whole",80, 80));
        super.buildUi();
        
        String value = getWidgetDef().getValue();
        if(value !=null) {
            String p[] = value.split("\\]");
            String toSplit="";
            if(p.length > 1) {
            	_wholeNumber.setValue(p[0].substring(1));
            	toSplit = p[1];
            }
            else {
            	_wholeNumber.setValue(p[0]);
            	_numerator.setValue("");
            	
                toSplit = p[0];	
            }
            
        	p = toSplit.split("/");
        	if(p.length > 1) {
            	_numerator.setValue(p[0]);
        		_denominator.setValue(p[1]);
        	}
        }
    }
    
    @Override
    public String checkValid() {
    	try {
    		Integer.parseInt(_wholeNumber.getCurrentValue());
	    	if(_wholeNumber.getCurrentValue() == null) {
	    		return "The whole number needs to be specified";
	    	}
	    	else {
	    		return super.checkValid();
	    	}
    	}
    	catch(Exception e) {
    		return "Invalid mixed fraction";
    	}
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
        wd.setValue("[" + _wholeNumber.getCurrentValue() + "]" + _numerator.getCurrentValue() + "/" + _denominator.getCurrentValue());
        
        // {type:'number_rational',allowMixed:true,value:'$_ans_e', format:'text',ans_format:'lowest_term', width:1, height:1}
        
        wd.setType("number_rational_mixed");
        wd.setAnsFormat("lowest_term");
        wd.setAllowMixed(true);
        return wd;
    }
    
}
