package hotmath.gwt.cm_tools.client.util;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

import hotmath.gwt.cm_core.client.util.CmAlertify;
import hotmath.gwt.cm_tools.client.ui.GWindow;

public class VideoPlayerWindow extends GWindow {
	
	static final String HTML_PART_1 =
		"<div id='video-div'><video id='video' src='http://catchupmath.com/";

	static final String HTML_PART_2 =
	    "' controls='controls' autoplay='autoplay' height='420' width='626'>" +
	    "<p>Your browser does not support HTML5 video.</p></video></div>";

	public VideoPlayerWindow(String title, String videoURI) {

		super(true);

		title = (title != null) ? title : "Catchup Math";
		setHeadingText(title);
		setPixelSize(650, 500);
		setResizable(false);
		setModal(true);

		if (videoURI != null) {
	        FlowLayoutContainer flow = new FlowLayoutContainer();
	        flow.setScrollMode(ScrollMode.NONE);
	        flow.add(new HTML(HTML_PART_1 + videoURI + HTML_PART_2));

	        FramedPanel fp = new FramedPanel();
	        fp.setHeaderVisible(false);
	        fp.setWidget(flow);

	        setWidget(fp);
	        setVisible(true);
		}
		else {
			new CmAlertify().alert("Invalid resource for " + title + " Video.");
		}
	}
}