package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.cm_tools.client.ui.GWindow;
import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;

public class GenericVideoPlayer extends GWindow {

	String videoPlayerId = "flowPlayer_" + System.currentTimeMillis();

	public GenericVideoPlayer(String videoToPlay, String title) {

		super(true);

		setHeadingText(title);
		setPixelSize(330, 320);
		setResizable(false);
		setModal(true);

		SWFSettings s = new SWFSettings();
		s.setMinPlayerVersion(new PlayerVersion(9));

		/**
		 * add id to force no cache .. is a bug with flowplayer that if in cache
		 * it only plays once.
		 */
		SWFWidget swfWidget = new SWFWidget(
				"/cm_student/flowplayer-3.1.5.swf?id=" + videoPlayerId, "100%",
				"100%", s);

		String flashVars = "{'key':'$a12fd4b15a588479e9e','clip':{'url':'THE_VIDEO'},'playerId':'PLAYER_ID','playlist':[{'url':'THE_VIDEO'}]}";

		flashVars = flashVars.replaceAll("THE_VIDEO", videoToPlay);
		flashVars = flashVars.replaceAll("PLAYER_ID", videoPlayerId);

		swfWidget.addFlashVar("config", flashVars);
		swfWidget.addParam("scale", "scale");
		swfWidget.addParam("allowfullscreen", "true");
		swfWidget.addParam("allowscriptaccess", "always");
		swfWidget.addParam("quality", "high");
		swfWidget.addParam("cachebusting", "true");
		swfWidget.addParam("bgcolor", "000000");
		add(swfWidget);

	}
}