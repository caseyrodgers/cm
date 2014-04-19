package hotmath.gwt.cm.client.ui.awards;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class CmAwards extends Composite {
	private FlowLayoutContainer _starPanel;
	private int _stars;

	public CmAwards() {
		_starPanel = new FlowLayoutContainer();
		_starPanel.setToolTip("The number of graded practice problems you got right on the first try during this session.");
		_starPanel.getElement().setAttribute("style",  "position: absolute; left: 200px;top: 10px;width: 200px;background: transparent");
		
		initWidget(_starPanel);
	}

	/** Increment the stars and make the new star 
	 *  stand out by showing a larger one ... then 
	 *  showing the standard one.
	 */
	public void addStar() {
		final HTML bigImage = new HTML("<img src='/gwt-resources/images/awards/star_big.png'/>");
		_starPanel.add(bigImage);
		new Timer() {
			@Override
			public void run() {
				setStars(_stars+1);
			}
		}.schedule(5000);;
	}
	
	public void setStars(int stars) {
		_stars = stars;
		_starPanel.clear();
		_starPanel.add(new HTML(createStars(stars)));
	}

	private String createStars(int stars) {
		String html = "";
		for(int i=0;i<stars;i++) {
			html += "<img src='/gwt-resources/images/awards/star.png'/>";
		}
		return html;
	}
}
