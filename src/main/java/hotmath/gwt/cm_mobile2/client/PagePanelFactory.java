package hotmath.gwt.cm_mobile2.client;

import hotmath.gwt.cm_mobile2.client.TopicViewPagePanel.Callback;
import hotmath.gwt.cm_mobile2.client.TopicViewPagePanel.LessonLoader;
import hotmath.gwt.cm_mobile2.client.page.PrescriptionPage;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.LoginForm;
import hotmath.gwt.cm_mobile_shared.client.LoginPage;
import hotmath.gwt.cm_mobile_shared.client.QuizPanel;
import hotmath.gwt.cm_mobile_shared.client.TopicListPage;
import hotmath.gwt.cm_mobile_shared.client.TopicListPagePanel;
import hotmath.gwt.cm_mobile_shared.client.TopicViewPage;
import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.MainPage;
import hotmath.gwt.cm_mobile_shared.client.page.PrescriptionResourcePage;
import hotmath.gwt.cm_mobile_shared.client.page.QuizPage;
import hotmath.gwt.cm_mobile_shared.client.page.TestPage;
import hotmath.gwt.cm_mobile_shared.client.page.TestPagePanel;
import hotmath.gwt.cm_mobile_shared.client.page.WelcomePage;
import hotmath.gwt.cm_mobile_shared.client.page.WelcomePagePanel;

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
    		else if(page instanceof TopicListPage) {
    		    return new TopicListPagePanel((TopicListPage)page);
    		}
            else if(page instanceof TopicViewPage) {
                return new TopicViewPagePanel((TopicViewPage)page);
            }
            else if(page instanceof TestPage) {
                return new TestPagePanel((TestPage)page);
            }
    		
    		throw new IllegalArgumentException("The page is unknown:" + page.getClass().getName());
    		
	    }
	    finally {
	        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_PAGE_ACTIVATED, page));
	    }
	}
}