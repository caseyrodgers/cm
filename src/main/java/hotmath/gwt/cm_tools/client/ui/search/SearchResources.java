package hotmath.gwt.cm_tools.client.ui.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface SearchResources extends ClientBundle {

    public SearchResources INSTANCE = GWT.create(SearchResources.class);

    @Source("resource_lesson.png")
    ImageResource resourceLessonImage();
    
    @Source("resource_video.png")
    ImageResource resourceVideoImage();

    @Source("resource_activity.png")
    ImageResource resourceActivityImage();

    @Source("resource_practice.png")
    ImageResource resourcePracticeImage();

}