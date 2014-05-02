package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;

import com.google.gwt.user.client.ui.HTML;


public class WidgetEditorImplNoWidget extends WidgetEditorImplDefault {

    public WidgetEditorImplNoWidget(WidgetDefModel widgetDef) {
        super(widgetDef);
    }
    
    @Override
    protected void buildUi() {
    	_fields.add(new HTML("<h1>Students will enter their answer using the Whiteboard.</h1>"));
    };
    
    @Override
    protected String getWidgetType() {
        return "";
    }
    
    @Override
    public String checkValid() {
        return null;
    }
    
    @Override
    public String getWidgetJson() {
    	return null;  // return null to signify no widget
    }
    
    @Override
    public String getDescription() {
    	return "Write and/or draw your answer on the whiteboard.";
    }
    
    @Override
    public String getValueLabel() {
    	return null;
    }
    
    @Override
    public void setupValue() {
    	/** empty */
    }
    
    @Override
    public String getWidgetTypeLabel() {
    	return "Whiteboard";
    }
    
    @Override
    public String getWidgetValueLabel() {
    	return null;
    }
}
