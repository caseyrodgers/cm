package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HTML;

public class TrainingVideosWindow extends CmWindow {

    public  TrainingVideosWindow() {
        setAutoHeight(true);
        setSize(640,480);

        setModal(true);
        setResizable(false);
        setStyleName("training-videos-window");
        setHeading("Catchup Math Training Videos");

        Button closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                TrainingVideosWindow.this.close();
            }
        });
        addButton(closeBtn);

		LayoutContainer lc = new LayoutContainer();
		//TrainingVideosWindow lc = this;
		lc.setAutoWidth(true);
		lc.setBorders(false);
        lc.setStyleName("training-videos-window");
	
		addVideo("Overview of Teacher Resources",
				 "Description of Overview of Teacher Reesources Video...",
				 "Overview of Teacher Resources/Overview of Teacher Resources_controller.swf",
				 "Watch the Teacher Resources Video",
				 lc);

		addVideo("Sample Student Session",
				 "Description of Sample Student Session Video...",
				 "Sample Student Session/Sample Student Session_controller.swf",
				 "Watch the Sample Student Session Video",
				 lc);
		
		addVideo("Available Content",
				 "Description of Available Content Video...",
				 "Available Content/Available Content_controller.swf",
				 "Watch the Available Content Video",
				 lc);
		
		addVideo("Registering Students",
				 "Description of Registering Students Video...",
				 "Registering Students/Registering Students_controller.swf",
				 "Watch the Registering Students Video",
				 lc);
		
		addVideo("Managing Students",
				 "Description of Managing Students Video...",
				 "Managing Students/Managing Students_controller.swf",
				 "Watch the Managing Students Video",
				 lc);
		
		addVideo("Managing Groups",
				 "Description of Managing Groups Video...",
				 "Managing Groups/Managing Groups_controller.swf",
				 "Watch the Managing Groups Video",
				 lc);
				
		add(lc);        

		setVisible(true);
    }

	private void addVideo(final String heading, String description, final String video, String toolTip, LayoutContainer lc) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div>");
        sb.append("<h2>").append(heading).append("</h2>");
        sb.append("<p>").append(description).append("</p>");
		sb.append("</div>");
		
		HTML html = new HTML();
		html.setHTML(sb.toString());
		lc.add(html);
		
        Button button = new Button("Watch");
        button.setStyleAttribute("margin","10px;");
        button.setToolTip(toolTip);
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                new TeacherVideoWindow(video, heading);
            }
        });
        lc.add(button);
	}

    public void onClick(ClickEvent event) {
    }
}

