package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;

public class WidgetEditorFactory {

    public static WidgetEditor createEditorFor(WidgetDefModel widgetDef) throws Exception {
        String type=widgetDef.getType();
        if(type == null) {
            throw new Exception("Widget type is null");
        }
        else if(type.equals("number_integer")) {
            return new WidgetEditorImplInteger(widgetDef, "Enter an integer. No decimal points or fraction bars.");
        }
        else if(type.equals("number_decimal")) {
            return new WidgetEditorImplDecimal(widgetDef, "Enter an integer or decimal number.");
        }
        else if(type.equals("inequality")) {
            return new WidgetEditorImplInequality(widgetDef);
        }
        else if(type.equals("number_simple_fraction") || type.equals("number_fraction")) {
            return new WidgetEditorImplFraction(widgetDef);
        }
        else if(type.equals("number_rational")) {
        	return new WidgetEditorImplRational(widgetDef);
        }
        else if(type.equals("number_rational_mixed")) {
        	return new WidgetEditorImplMixedFraction(widgetDef);
        }        
        else if(type.equals("mChoice")) {
            return new WidgetEditorImplMultiChoice(widgetDef);
        }
        else if(type.equals("coordinates")) {
            return new WidgetEditorImplCoordinates(widgetDef);
        }
        else if(type.equals("widget_plot")) {
            return new WidgetEditorImplPlot(widgetDef);
        }
        else if(type.equals("whiteboard")) {
            return new WidgetEditorImplWhiteboard(widgetDef);
        }
        else if(type.equals("")) {
            return new WidgetEditorImplNoWidget(widgetDef);
        }
        
        throw new Exception("Could not create widget editor for: " + type);
    }

}
