package hotmath.gwt.cm_admin.client.custom_content.problem;

import com.google.gwt.user.client.ui.HTML;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;


public class WidgetEditorImplMixedFraction extends WidgetEditorImpFractionWithAdvanced {

    NumericalTextField _decimal = new NumericalTextField();

    public WidgetEditorImplMixedFraction(WidgetDefModel widgetDef) {
        super(widgetDef);
    }

    protected void buildUi() {
        super._fields.add(new MyFieldLabel(_decimal, "Decimal",80, 80));
        super.buildUi();
        
        String value = getWidgetDef().getValue();
        if(value !=null) {
            String p[] = value.split("\\]");
            String toSplit="";
            if(p.length > 1) {
            	_decimal.setValue(p[0].substring(1));
            	toSplit = p[1];
            }
            else {
                toSplit = p[0];	
            }
            
        	p = toSplit.split("/");
        	if(p.length > 1) {
            	_numerator.setValue(p[0]);
        		_denominator.setValue(p[1]);
        	}
        }
        
        super.setFormatValue(_widgetDef.getFormat());
        
        _fields.add(super.createAdvanced());
    }
    
    @Override
    public String checkValid() {
    	if(!_decimal.validate()) {
    		return "Decimal number needs to be numeric";
    	}
    	else {
    		return super.checkValid();
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
        wd.setValue("[" + (_decimal.getCurrentValue()!=null?_decimal.getCurrentValue() :0) + "]" + _numerator.getCurrentValue() + "/" + _denominator.getCurrentValue());
        
        // {type:'number_rational',allowMixed:true,value:'$_ans_e', format:'text',ans_format:'lowest_term', width:1, height:1}
        
        wd.setType("number_rational_mixed");
        wd.setAnsFormat("lowest_term");
        wd.setAllowMixed(true);
        return wd;
    }
    
}
