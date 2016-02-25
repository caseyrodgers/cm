package hotmath.gwt.lesson_viewer.client;


import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class LessonViewerPanelSimple extends Composite {
    private LessonViewReviewViewImpl _lessonPanel;

	public LessonViewerPanelSimple(String file) {
        FlowPanel fp = new FlowPanel();
        ScrollPanel sp = new ScrollPanel();
        _lessonPanel = new LessonViewReviewViewImpl();
        sp.add(_lessonPanel.asWidget());
        fp.add(sp);
        initWidget(fp);
        
        loadLessonTextFromServer(file);
    }

    private void loadLessonTextFromServer(final String file) {
    	try {
    		GetReviewHtmlAction action = new GetReviewHtmlAction("topics/" + file);
    		LessonViewer.getCmService().execute(action, new AsyncCallback<LessonResult>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					_lessonPanel.showError(file, "Could not load: " + caught.getMessage());
				}

				@Override
				public void onSuccess(LessonResult result) {
		    		_lessonPanel.loadLesson(file, result.getLesson());				
				}
    			
			});
 
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		Window.alert("Error loading lesson text: " + e);
    	}
    }
}
