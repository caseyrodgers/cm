package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

public abstract class WidgetEditorImplDefault extends Composite implements WidgetEditor {

    protected WidgetDefModel _widgetDef;
    protected VerticalLayoutContainer _fields = new VerticalLayoutContainer();

    public WidgetEditorImplDefault(WidgetDefModel widgetDef) {
        this._widgetDef = widgetDef;
        initWidget(_fields);
    }
    
    
    abstract protected String getWidgetType();
    abstract protected void buildUi();
    
    @Override
    public Widget asWidget() {
        buildUi();
        return this;
    }

    @Override
    public WidgetDefModel getWidgetDef() {
        return _widgetDef;
    }
    
    @Override
    public String getWidgetJson() {
        WidgetDefModel widget = createWidgetDefModel();
        return widget.getJson();
    }

    protected WidgetDefModel createWidgetDefModel() {
        WidgetDefModel wd = new WidgetDefModel();
        wd.setType(getWidgetType());
        
        return wd;
    }
    
    
    
    /** silently coerese into an int as string
     * 
     * @param intVal
     * @return
     */
    protected String getIntValueAsString(String intVal) {
        try {
            return Integer.parseInt(intVal) + ""; 
        }
        catch(Exception e) {
            // silent ..?
        }
        return "0";
    }
    
    @Override
    public String getValueLabel() {
    	return "Correct Value";
    }


    /** return true if the current widget is the one
     *  used to edit the widget defined
     *  
     * @return
     */
	public boolean isDefinedWidget() {
		return getWidgetType().equals(_widgetDef.getType());
	}
	
	@Override
	public void setupValue() {
		Window.alert("Default Setup Value");
		/** default setup value ... no value set */
	}
	
	@Override
	public String getWidgetTypeLabel() {
		return "Integer";
	}
	
	@Override
	public String getWidgetValueLabel() {
		return getWidgetDef().getValue(); 
	}
}
