package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.TextField;


public class WidgetEditorImpFractionWithAdvanced extends WidgetEditorImplFraction {

    TextField _format = new TextField();
    public WidgetEditorImpFractionWithAdvanced(WidgetDefModel widgetDef) {
        super(widgetDef);
    }
    
    @Override
    protected WidgetDefModel createWidgetDefModel() {
        WidgetDefModel wd = super.createWidgetDefModel();
        wd.setFormat(_format.getCurrentValue()!=null?_format.getCurrentValue():null);
        return wd;
    }

	public Widget createAdvanced() {
        DisclosurePanel advanced = new DisclosurePanel("Advanced Options");
		VerticalLayoutContainer adFields = new VerticalLayoutContainer();
		advanced.setContent(adFields);
		adFields.add(new MyFieldLabel(_format, "Units",80, 80));
		return advanced;
	}
    
}
