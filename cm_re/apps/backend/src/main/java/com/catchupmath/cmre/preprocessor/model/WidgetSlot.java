package com.catchupmath.cmre.preprocessor.model;

/** Mirrors shared-types' WidgetSlot. See LegacySolutionParser.extractWidgetSlot and cm/TUTOR_WIDGET.org. */
public class WidgetSlot {
    public String type;

    public WidgetSlot(String type) {
        this.type = type;
    }
}
