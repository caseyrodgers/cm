package hotmath.gwt.cm_core.client.award;

import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCorrectEvent;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCorrectHandler;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class CmAwardPanel extends Composite {
	
	static {
		CmRpcCore.EVENT_BUS.addHandler(TutorWidgetInputCorrectEvent.TYPE, new TutorWidgetInputCorrectHandler() {
			@Override
			public void widgetWasCorrect(boolean firstTime) {
				__instance.addStar();
			}
		});
	}
	
	private FlowPanel _starPanel;
	private int _stars;

	private static CmAwardPanel __instance;
	public CmAwardPanel() {
		__instance = this;
		_starPanel = new FlowPanel();
		_starPanel.addStyleName("cm-awards");
		_starPanel.getElement().setAttribute("style", "position: absolute");
		initWidget(_starPanel);

		// startTest();
	}

	/**
	 * Increment the stars and make the new star stand out by showing a larger
	 * one ... then showing the standard one.
	 */
	public void addStar() {
		final HTML bigImage = new HTML("<img class='animated " + getRandomAnimationIn() + "' src='/gwt-resources/images/awards/star_big.png'/>");
		_starPanel.add(bigImage);
		new Timer() {
			@Override
			public void run() {
				bigImage.getElement().setAttribute("class",  "animated " + getRandomAnimationOut());
				new Timer() {
					public void run() {
						setStars(_stars+1,true);
					}
				}.schedule(1000);
			}
		}.schedule(2000);
	}

	private String getRandomAnimationOut() {
		String animations[] = {"lightSpeedOut","flipOutX", "flipOutY", "bounceOut", "fadeOut"};
		return animations[ Random.nextInt(animations.length-1)];
	}

	public void setStars(int stars, boolean animateLate) {
		_stars = stars;
		_starPanel.clear();
		
		_starPanel.add(new HTML(createStars(stars, animateLate)));
	}

	private String createStars(int stars, boolean animateLast) {
		String html = "";
		
		int star10s = stars / 6;
		int starSingles = stars % 6;
		
		for(int i=0;i<star10s;i++) {
			html += "<img src='/gwt-resources/images/awards/star_10.png'/>";
		}
		
		for (int i = 0; i < starSingles; i++) {
			boolean isLast = i==starSingles-1;
			html += "<img " + (animateLast && isLast?" class='animated " + getRandomAnimationIn() + "' ":"") + 
					" src='/gwt-resources/images/awards/star.png'/>";
		}
		return html;
	}

	private String getRandomAnimationIn() {
		String animations[] = {"lightSpeedIn","flipInX", "flipInY", "bounceIn", "fadeIn"};
		return animations[ Random.nextInt(animations.length-1)];
	}

	private void startTest() {
		new Timer() {
			@Override
			public void run() {
				addStar();
			}
		}.scheduleRepeating(500);
	}

}
