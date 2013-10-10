package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplReview;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Frame;

public class LessonPrescriptionViewer extends GWindow {
    
    public LessonPrescriptionViewer(LessonModel lesson) {
        super(true);
        
        setHeadingHtml("Lesson: " + lesson.getLessonName());
        setPixelSize(600, 800);
        setMinimizable(true);
        
        showLessonPrescription(lesson.getLessonName());
        
        setVisible(true);
    }
    
    
    
    private void showLessonPrescription(String lessonName) {
        ResourceViewerImplReview review = new ResourceViewerImplReview();
        review.setResourceItem(new InmhItemData(CmResourceType.REVIEW,lessonName, lessonName));
        setWidget(review.getResourcePanel());
    }



    private void showLessonPrescription2(String lessonName) {
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
