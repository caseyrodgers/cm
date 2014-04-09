package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.form.TextField;


public class WidgetEditorImplRational extends WidgetEditorImpFractionWithAdvanced {
    TextField _decimal = new NumericalTextField();

	private TabPanel _tabPanel;

	private FlowLayoutContainer _panDec;
    public WidgetEditorImplRational(WidgetDefModel widgetDef) {
        super(widgetDef);
    }

    protected void buildUi() {
        super.buildUi();
        
        _decimal.setAllowBlank(false);
        super.setFormatValue(_widgetDef.getFormat());
        
		
		_fields.clear();
		
		 _panDec = new FlowLayoutContainer();
		 _panDec.getElement().setAttribute("style",  "background: #DFE8F6;padding: 5px;");
		 _panDec.add(new HTML("<br/>"));
		 _panDec.add(new MyFieldLabel(_decimal, "Decimal", 80,50));
		 _tabPanel = new TabPanel();
		 _tabPanel.getElement().setAttribute("style",  "background: transparent");
		 _tabPanel.add(_panDec, "Decimal");
		 
		 
		 FlowLayoutContainer panFrac = new FlowLayoutContainer();
		 panFrac.getElement().setAttribute("style",  "background: #DFE8F6;padding: 5px;");
		 panFrac.add(new MyFieldLabel(_numerator, "Numerator", 80, 50));
		 panFrac.add(new MyFieldLabel(_denominator, "Denominator", 80, 50));
		 _tabPanel.add(panFrac, "Fraction");
		 
		 _fields.add(new HTML("<b>Correct value should be either a decimal or a fraction:</b>"));
		 _fields.add(_tabPanel);
		 
		_fields.add(super.createAdvanced());
		
		
		String p[] = (_widgetDef.getValue()!=null?_widgetDef.getValue():"").split("/");
		if(p.length == 2) {
			_tabPanel.setActiveWidget(panFrac);
		}
		else {
			_tabPanel.setActiveWidget(_panDec);
			_decimal.setValue(p[0]);
		}
		
		_tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				if(_tabPanel.getActiveWidget() == _panDec) {
				}
			}
		});
    }
    
    @Override
    public String getWidgetJson() {
    	if(isDecimal()) {
    		WidgetDefModel wd = createWidgetDefModel();
    		wd.setValue(_decimal.getCurrentValue());
    		return wd.getJson();
    	}
    	else {
    		return super.getWidgetJson();
    	}
    }
    
    @Override
    protected WidgetDefModel createWidgetDefModel() {
        WidgetDefModel wd = super.createWidgetDefModel();
        wd.setType("number_rational");
        wd.setAnsFormat("lowest_term");
        wd.setAllowMixed(false);
        
        String format = _format.getCurrentValue()!=null?_format.getCurrentValue():null;
        if(format != null) {
        	if(!format.startsWith("measure_")) {
        		format = "measure_"+format;
        	}
        }
        wd.setFormat(format);
        return wd;
    }

    private boolean isDecimal() {
    	return _tabPanel.getActiveWidget() == _panDec;
    }
    
    @Override
    public String checkValid() {
    	if(isDecimal()) {
    		if(!_decimal.validate()) {
    			return "Not a valid decimal value";
    		}
    		else {
    			return null;
    		}
    	}
    	else {
    		return super.checkValid();
    	}
    }
    
    @Override
    public String getDescription() {
    	return "Enter an integer, decimal number, or fraction (use \"/\"). If a fraction is correct but not in lowest terms, students are prompted to reduce. Answers must be exact.";
    }
    
}
