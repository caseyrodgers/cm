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

		StringBuilder sb = new StringBuilder();
		sb.append("<div class='training-videos-window'>");
        sb.append("<h2>").append("Videos and FAQ").append("</h2>");
        sb.append("<p>").append("The videos below let you explore the richness of Catchup Math, topic ");
        sb.append("by topic. For specific questions, please click for our ");
        sb.append("<a href='/teacher-faq.html' target='_blank'>Teacher FAQ</a> or contact your ");
        sb.append("<a href='/contact.html' target='_blank'>Account Manager</a>. ");
        sb.append("We hope you and your colleagues will return here often!");
        sb.append("</p>");
		sb.append("</div>");
		HTML html = new HTML();
		html.setHTML(sb.toString());
		lc.add(html);
		

		addVideo("Overview of Teacher Resources",
				 "Brief introduction to the features and resources available to teachers and administrators.  Length: 15 minutes.",
				 "Overview of Teacher Resources/Overview of Teacher Resources_controller.swf",
				 "Watch the Teacher Resources Video",
				 lc, true);

		addVideo("Sample Student Session",
				 "Demonstrates what students experience and the options available to them.  Length:  12 minutes.",
				 "Sample Student Session/Sample Student Session_controller.swf",
				 "Watch the Sample Student Session Video",
				 lc, true);
		
		addVideo("Available Content",
				 "Explains the various programs available, and the content included within each program.  Length: 9 minutes.",
				 "Available Content/Available Content_controller.swf",
				 "Watch the Available Content Video",
				 lc, true);
		
		addVideo("Registering Students",
				 "Demonstrates the various options for registering students into Catchup Math.  Length: 17 minutes.",
				 "Registering Students/Registering Students_controller.swf",
				 "Watch the Registering Students Video",
				 lc, true);
		
		addVideo("Managing Students",
				 "Explores the various resources for managing the placement of students, managing their assignments, reviewing their progress, and applying controls.  Length: 25 minutes.",
				 "Managing Students/Managing Students_controller.swf",
				 "Watch the Managing Students Video",
				 lc, true);
		
		addVideo("Managing Groups",
				 "Explains how to set up and assign students to groups (classes), assign or reassign programs/content to the groups, set controls, and analyze group data.  Length: 30 minutes.",
				 "Managing Groups/Managing Groups_controller.swf",
				 "Watch the Managing Groups Video",
				 lc, false);
				
		add(lc);        

		setVisible(true);
    }

	private void addVideo(final String heading, String description, final String video, String toolTip, LayoutContainer lc,
			boolean addSeparator) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class='training-videos-window'>");
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
        
        if (addSeparator) {
        	html = new HTML();
        	html.setHTML("<hr>");
        	lc.add(html);
        }
	}

    public void onClick(ClickEvent event) {
    }
}

