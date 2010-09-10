package hotmath.gwt.cm_mobile_shared.client;


import hotmath.gwt.cm_mobile_shared.client.page.MainPage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class MainPagePanel extends AbstractPagePanel {

	private MainPage mMainPage;

	public MainPagePanel(MainPage mainPage) {
		mMainPage = mainPage;
		FlowPanel basePanel = new FlowPanel();
		basePanel.add(new HTML("THE MAIN PAGE"));
		
		Button btn = new Button("Do it");
		btn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				Controller.navigateToWelcome(mMainPage);
			}
		});
		
		basePanel.add(btn);
		initWidget(basePanel);
	}

}
