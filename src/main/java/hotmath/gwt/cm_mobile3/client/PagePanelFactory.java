package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

public class PagePanelFactory {

	private PagePanelFactory() {

	}

	public static IsWidget createPagePanel(IPage page) {
	    try {
	        
	        if(page instanceof IsWidget) {
	            return (IsWidget)page;
	        }
//    		if (page instanceof MainPage) {
//    			return new MainPagePanel((MainPage) page);
//    		} else if (page instanceof WelcomePage) {
//    			return new WelcomePagePanel((WelcomePage) page);
//    		}
//    		else if(page instanceof LoginPage) {
//    			return new LoginForm((LoginPage)page);
//    		}
//    		else if(page instanceof QuizPage) {
//    			return new QuizPanel((QuizPage)page);
//    		}
//    		else if(page instanceof PrescriptionPage) {
//    			return new PrescriptionPanel((PrescriptionPage)page);
//    		}
//    		else if(page instanceof PrescriptionResourcePage) {
//    			return new PrescriptionResourcePagePanel((PrescriptionResourcePage)page);
//    		}
//    		else if(page instanceof TopicListPage) {
//    		    return new TopicListPagePanel((TopicListPage)page);
//    		}
//            else if(page instanceof TopicViewPage) {
//                return new TopicViewPagePanel((TopicViewPage)page);
//            }
//            else if(page instanceof TestPage) {
//                return new TestPagePanel((TestPage)page);
//            }
    		
	        Window.alert("Requested page '" + page.getClass().getName() + " either does not exist or does not extend IsWidget.");
    		throw new IllegalArgumentException("The page is unknown:" + page.getClass().getName());
    		
	    }
	    finally {
	        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_PAGE_ACTIVATED, page));
	    }
	}
}