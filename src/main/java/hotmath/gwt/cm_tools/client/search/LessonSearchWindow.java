package hotmath.gwt.cm_tools.client.search;

import hotmath.gwt.cm_tools.client.ui.GWindow;

public class LessonSearchWindow extends GWindow {
    
    public LessonSearchWindow() {
        super(true);
        
        setPixelSize(800,  600);
        setHeadingText("Lesson Search");
        setWidget(new LessonSearchPanel());
        setVisible(true);
    }

    public static void startTest() {
        new LessonSearchWindow();
    }
}