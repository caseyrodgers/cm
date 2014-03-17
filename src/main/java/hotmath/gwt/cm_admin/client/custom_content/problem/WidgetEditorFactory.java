package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;

public class WidgetEditorFactory {

    public static WidgetEditor createEditorFor(WidgetDefModel widgetDef) throws Exception {
        String type=widgetDef.getType();
        if(type == null) {
            throw new Exception("Widget type is null");
        }
        else if(type.equals("number_integer")) {
            return new WidgetEditorImplInteger(widgetDef);
        }
        else if(type.equals("number_decimal")) {
            return new WidgetEditorImplInteger(widgetDef);
        }
        else if(type.equals("inequality")) {
            return new WidgetEditorImplInequality(widgetDef);
        }
        else if(type.equals("number_fraction")) {
            return new WidgetEditorImplFraction(widgetDef);
        }
        else if(type.equals("number_rational")) {
            return new WidgetEditorImplRational(widgetDef);
        }
        else if(type.equals("mChoice")) {
            return new WidgetEditorImplMultiChoice(widgetDef);
        }
        else if(type.equals("")) {
            return new WidgetEditorImplNoWidget(widgetDef);
        }
        
        throw new Exception("Could not create widget editor for: " + widgetDef);
    }

}
