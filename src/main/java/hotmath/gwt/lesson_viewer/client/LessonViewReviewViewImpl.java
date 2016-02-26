package hotmath.gwt.lesson_viewer.client;


import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc.client.CmRpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class LessonViewReviewViewImpl extends Composite {

	    @UiField HTMLPanel lessonText;
	    @UiField HTMLPanel errorText;
	    @UiField HeadingElement lessonTitle;

	    
	    interface MyUiBinder extends UiBinder<Widget, LessonViewReviewViewImpl> {
	    }

	    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


	    
	    public LessonViewReviewViewImpl() {
	        initWidget(uiBinder.createAndBindUi(this));
	    }



	    public void loadLesson(String title, String lesson) {
	        lessonTitle.setInnerHTML(title);
	        lessonText.add(new HTML(lesson));
	        
	        CmRpc.jsni_processMathJax();
	
	    }
	    
	    public void showError(String title, String error) {
	    	errorText.add(new HTML(error));
	    }

	    
	}
