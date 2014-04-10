package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;

import com.google.gwt.user.client.ui.IsWidget;


public interface WidgetEditor extends IsWidget {
    String getWidgetJson();
    WidgetDefModel getWidgetDef();
    String checkValid();
	String getDescription();
	

	/** return string used as value
	 *  entry form label, or null to 
	 *  not show it.
	 *  
	 * @return
	 */
	String getValueLabel();
}
