package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.event.CmEvent;
import hotmath.gwt.cm_mobile.client.event.EventBus;
import hotmath.gwt.cm_mobile.client.event.EventTypes;
import hotmath.gwt.cm_mobile.client.page.IPage;
import hotmath.gwt.cm_mobile.client.page.MainPage;
import hotmath.gwt.cm_mobile.client.page.PrescriptionPage;
import hotmath.gwt.cm_mobile.client.page.PrescriptionResourcePage;
import hotmath.gwt.cm_mobile.client.page.PrescriptionResourcePagePanel;
import hotmath.gwt.cm_mobile.client.page.QuizPage;
import hotmath.gwt.cm_mobile.client.page.WelcomePage;
import hotmath.gwt.cm_mobile.client.page.WelcomePagePanel;

public class PagePanelFactory {

	private PagePanelFactory() {

	}

	public static AbstractPagePanel createPagePanel(IPage page) {
	    try {
    		if (page instanceof MainPage) {
    			return new MainPagePanel((MainPage) page);
    		} else if (page instanceof WelcomePage) {
    			return new WelcomePagePanel((WelcomePage) page);
    		}
    		else if(page instanceof LoginPage) {
    			return new LoginForm((LoginPage)page);
    		}
    		else if(page instanceof QuizPage) {
    			return new QuizPanel((QuizPage)page);
    		}
    		else if(page instanceof PrescriptionPage) {
    			return new PrescriptionPanel((PrescriptionPage)page);
    		}
    		else if(page instanceof PrescriptionResourcePage) {
    			return new PrescriptionResourcePagePanel((PrescriptionResourcePage)page);
    		}
    		throw new IllegalArgumentException("The page is unknown:" + page.getClass().getName());
	    }
	    finally {
	        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_PAGE_ACTIVATED, page));
	    }
	}
}