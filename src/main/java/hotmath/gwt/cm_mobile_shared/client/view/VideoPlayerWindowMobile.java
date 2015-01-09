package hotmath.gwt.cm_mobile_shared.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class VideoPlayerWindowMobile {

	static final String HTML_PART_1 =
			"<div id='video-div' class='video-div'><video id='video' src='http://catchupmath.com/";

	static final String HTML_PART_2 =
		    "' controls='controls' autoplay='autoplay'>" +
		    "<p>Your browser does not support HTML5 video.</p></video></div>";


	public VideoPlayerWindowMobile(String title, String videoURI) {
		PopupPanel popup = buildUI(title, videoURI);
		if (popup != null) popup.show();
	}

	private PopupPanel buildUI(String title, String videoURI) {

		title = (title != null) ? title : "Catchup Math";
		FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<div class='video-title'>" + title + "</div>"));
    	panel.setPixelSize(665, 545);

		final PopupPanel popup = new PopupPanel();
        
        Button btn = new Button("Close", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	popup.clear();
                popup.hide();
            }
        });
        btn.getElement().setInnerHTML("<span><span>Close</span></span>");
        btn.getElement().addClassName("sexy_cm_silver sexybutton");
        panel.add(btn);

    	if (videoURI != null) {
    		panel.add(new HTML(HTML_PART_1 + videoURI + HTML_PART_2));
    	}
    	else {
    		Window.alert("Invalid resource for " + title + " Video.");
    		return null;
    	}

        //popup.setAutoHideEnabled(true);
        popup.setWidth("685 px");
        popup.setModal(true);
        //popup.center();
		popup.setStyleName("video-player-mobile");

        popup.setWidget(panel);

        return popup;		
	}
}
