package hotmath.gwt.cm_tutor.client.view;

import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_mobile_shared.client.util.ViewSettings;
import hotmath.gwt.cm_rpc.client.model.LessonModel;

import java.util.List;

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Create a wrapper around extern jquery calculator
 * 
 * This is external to GXT and is shared amongst tools.
 * 
 * @author casey
 *
 */

public class LessonSelectionPanel extends SimplePanel {

	private Callback callback;

	public LessonSelectionPanel(List<LessonModel> lessons, Callback callback) {
		this.callback = callback;
		addStyleName("page");
		getElement().setAttribute("style", "margin: 20px;min-height: 0;height: 200px");
		drawGui(lessons);
	}
	
	public interface Callback {
		void showLesson(LessonModel lesson);
	}

	private void drawGui(List<LessonModel> lessons) {
		
		TouchClickHandler<String> handler = new TouchClickEvent.TouchClickHandler<String>() {
            @Override
            public void touchClick(TouchClickEvent<String> e) {
                if (!ViewSettings.AnimationRunning) {
                    /** add the current search term to previous entries
                     * 
                     */
                	LessonModel lesson = (LessonModel)e.getTarget().getUserData();
                	callback.showLesson(lesson);
                }
            }
        };
        
		GenericContainerTag ul = new GenericContainerTag("ul");
		ul.addStyleName("touch");
		
		for(LessonModel l: lessons) {		
			GenericTextTag<String> li = new GenericTextTag<String>("li",l.getLessonName());
			li.setUserData(l);
			li.addHandler(handler);

			li.setStyleName("group");
			ul.add(li);
		}

		add(ul);
		
		// lessonsDiv.appendChild(ul.getElement());
	}

}
