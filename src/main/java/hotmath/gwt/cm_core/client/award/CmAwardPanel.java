package hotmath.gwt.cm_core.client.award;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCorrectEvent;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCorrectHandler;
import hotmath.gwt.shared.client.CmShared;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;

public class CmAwardPanel extends Composite {

	static {
		CmRpcCore.EVENT_BUS.addHandler(TutorWidgetInputCorrectEvent.TYPE,
				new TutorWidgetInputCorrectHandler() {
					@Override
					public void widgetWasCorrect(boolean firstTime) {
						__instance.addStar();
					}
				});
		
		
	}
	
	
	static public interface AwardCallback {
		void awardPosted(int totalAwards);
	}

	private FlowPanel _starPanel;
	private int _stars;
	private AwardCallback awardCallback;
	private static final int NUM_STARS = 5;

	private static CmAwardPanel __instance;

	public CmAwardPanel() {
		__instance = this;
		_starPanel = new FlowPanel();
		_starPanel.addStyleName("cm-awards");
		_starPanel.getElement().setAttribute("style", "position: absolute");
		initWidget(_starPanel);
		
		if(CmCore.getQueryParameterValue("test").equals("awards")) {
			startTest();
		}
	}

	public CmAwardPanel(AwardCallback awardCallback) {
		this();
		this.awardCallback = awardCallback;
	}

	/**
	 * Increment the stars and make the new star stand out by showing a larger
	 * one ... then showing the standard one.
	 */
	public void addStar() {

		
		String html = "<img id='award-big-button' src='/gwt-resources/images/awards/star_big.png' class='animated " + getRandomAnimationIn() + "'/>";
		_starPanel.add(new HTML(html));
		new Timer() {
			@Override
			public void run() {
				final Element bigImage = DOM.getElementById("award-big-button");
				if(bigImage != null) {
					bigImage.addClassName("animated slideOutUp");
					new Timer() {
						public void run() {
							bigImage.removeFromParent();
						}
					}.schedule(1500);
				}
			}
		}.schedule(2000);
		
		setStars(_stars + 1, true);
	}

	public int getStars() {
		return _stars;
	}

	private String getRandomAnimationOut() {
		String animations[] = { "lightSpeedOut", "flipOutX", "flipOutY",
				"bounceOut", "fadeOut" };
		return animations[Random.nextInt(animations.length - 1)];
	}

	public void setStars(final int stars, final boolean animateLate) {
		_stars = stars;
		
		if(stars > 0 && stars % NUM_STARS == 0) {
			// animate change to single
			for(int i=0;i<_starPanel.getWidgetCount();i++) {
				_starPanel.getWidget(i).getElement().setAttribute("class",  "animated " + getRandomAnimationOut());
			}
		}
		
		new Timer() {
			@Override
			public void run() {
				_starPanel.clear();
				_starPanel.add(new HTML(createStars(stars, animateLate)));
				
				if(awardCallback != null) {
					awardCallback.awardPosted(stars);
				}
			}
		}.schedule(1000);
	}

	private String createStars(int stars, boolean animateLast) {
		String html = "";

		int star10s = stars / NUM_STARS;
		int starSingles = stars % NUM_STARS;

		for (int i = 0; i < star10s; i++) {
			boolean isLast = (starSingles == 0 && i == star10s - 1);
			html += "<img src='/gwt-resources/images/awards/star_10.png' "
		    +  (animateLast && isLast ? " class='animated "	+ getRandomAnimationIn() + "' " : "") + "/>";
		}

		for (int i = 0; i < starSingles; i++) {
			boolean isLast = i == starSingles - 1;
			html += "<img "
					+ (animateLast && isLast ? " class='animated "
							+ getRandomAnimationIn() + "' " : "")
					+ " src='/gwt-resources/images/awards/star.png'/>";
		}
		return html;
	}

	private String getRandomAnimationIn() {
		String animations[] = { "lightSpeedIn", "flipInX", "flipInY",
				"bounceIn", "fadeIn" };
		return animations[Random.nextInt(animations.length - 1)];
	}

	public void startTest() {
		new Timer() {
			@Override
			public void run() {
				addStar();
			}
		}.scheduleRepeating(10000);
	}
}
