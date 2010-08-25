package hotmath.gwt.cm_tools.client.ui;

// import hotmath.gwt.cm.client.history.CmHistoryManager;
// import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.shared.client.util.UserInfo;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.TableRowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;

/**
 * Display current user's location in CM program/Level/Prescription
 * 
 * @author casey
 * 
 */
public class LocationBar extends LayoutContainer {
	public LocationBar() {
		setStyleName("location-bar");
		setScrollMode(Scroll.NONE);
	}

	private int margins = 30;

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);

		TableRowLayout layout = new TableRowLayout();
		layout.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		layout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		// layout.setCellSpacing();
		//layout.setCellPadding(0);
		layout.setWidth("95%");
		layout.setHeight("20px");

		setLayout(layout);

		int activeLesson = UserInfo.getInstance().getSessionNumber();
		int numLessons = UserInfo.getInstance().getSessionCount();
		
		for (int i = 0; i < numLessons; i++) {
			Anchor a = new Anchor(Integer.toString(i + 1));
			final int lessNum=i;
			a.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					  String locationStr = "p:" + lessNum;
				      //CmHistoryManager.getInstance().addHistoryLocation(new CmLocation(locationStr));
				}
			});
			
			String linkStyle = "normal";
			if(i == activeLesson) {
				linkStyle = "normal";
			}
			else if(i < activeLesson) {
				linkStyle = "completed";
			}
			else {
				linkStyle = "todo";
			}

			a.setStyleName(linkStyle);
			add(a);
		}
		layout();
	}
}