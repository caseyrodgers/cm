package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Frame;

public class LessonPrescriptionViewer extends GWindow {
    
    public LessonPrescriptionViewer(String lessonName) {
        super(true);
        
        setHeadingHtml("Lesson: " + lessonName);
        setPixelSize(600, 800);
        
        showLessonPrescription(lessonName);
        
        setVisible(true);
    }
    
    private void showLessonPrescription(String lessonName) {
        try {
            String url = "/cm_search/CatchupMathSearch.html#TopicPlace:" + URL.encode(lessonName);
            Frame frame = new Frame();
            frame.setWidth("100%");
            frame.setHeight("480px");
            frame.setUrl(url);
            setWidget(frame);
        }
        catch(Exception e) {
            Log.error("Error show lesson prescription: " + e.getMessage(), e);
        }
    }

}
