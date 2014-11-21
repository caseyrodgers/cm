package hotmath.gwt.cm_mobile_shared.client.ui;

import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.activity.AssignmentProblemActivity;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_tutor.client.view.LessonSelectionPanel;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class LessonSelectionDialog extends DialogBox {
    
    public LessonSelectionDialog(List<LessonModel> lessons) {
        super(true);
        addStyleName("AboutDialog");
        setSize("360px", "280px");
        setText("Available Lessons");
        setGlassEnabled(true);
        setAnimationEnabled(true);
        setAutoHideEnabled(true);
        setModal(true);

        DockPanel dock = new DockPanel();
        HorizontalPanel buttonBar = new HorizontalPanel();
        

        FlowPanel mainPanel = new FlowPanel();

        Anchor closeAnchor = new Anchor("x");
        closeAnchor.addStyleName("closeAnchor");
        closeAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        mainPanel.add(closeAnchor);
        
        
        SexyButton close = new SexyButton("Close", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        buttonBar.getElement().setAttribute("style", "margin: 10px");
        buttonBar.add(close);
        dock.add(buttonBar, DockPanel.SOUTH);
        dock.add(new LessonSelectionPanel(lessons, new LessonSelectionPanel.Callback() {
			@Override
			public void showLesson(LessonModel lesson) {
            	new AssignmentProblemActivity(0,null).showLesson(lesson);
            	hide();
			}
		}), DockPanel.CENTER);
        setWidget(dock);
    }

}
