package hotmath.gwt.cm_tools.client.search;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.FlowPanel;

public class LessonSearchWindow extends GWindow {
    
    public LessonSearchWindow() {
        super(true);
        
        setPixelSize(800,  600);
        setHeadingText("Lesson Search");
        
        FlowPanel fp = new FlowPanel();
        LessonSearchField lsf = new LessonSearchField();
        fp.add(lsf);
        setWidget(fp);
        setVisible(true);
    }

    public static void startTest() {
        new LessonSearchWindow();
    }
}
