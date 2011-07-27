package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEvent;
import hotmath.gwt.cm_mobile_shared.client.event.BackPageLoadedEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.PrescriptionResourcePage;
import hotmath.gwt.cm_mobile_shared.client.page.QuizPage;
import hotmath.gwt.cm_mobile_shared.client.page.WelcomePage;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;

public class Controller {
    private static ObservableStack<IPage> mPageStack;
    private static EventBus __eventBus;

    private Controller() {
    }

    public static void installEventBus(EventBus eventBus) {
    	__eventBus = eventBus;
    }
    /** Always go to topic list on init
     *
     * @param pageStack
     */
    public static void init(ObservableStack<IPage> pageStack) {
        mPageStack = pageStack;
    }

    public static void navigateToTopicList() {
        TopicListPage topicPage = new TopicListPage();
        mPageStack.push(topicPage);
    }

    public static void navigateToTopicView(String file) {
        TopicViewPage topicViewPage = new TopicViewPage(file);
        mPageStack.push(topicViewPage);
    }

    public static void navigateBack() {
    	
    	/** allow current page an override to normal back
    	 * 
    	 */
    	IPage pageCurrent = mPageStack.peek();
    	if(pageCurrent != null && pageCurrent.getBackAction() != null) {
    		Log.info("Back override for '" + pageCurrent + "'");
    		pageCurrent.getBackAction().goBack();
    	}
    	else {
	    	if(mPageStack.getCount() > 1) {
	    		mPageStack.pop();
	    		IPage currPage = mPageStack.peek();
	    		__eventBus.fireEvent(new BackPageLoadedEvent(currPage));
	    	}
	    	else {
	    		if(__eventBus == null) {
	    			Window.alert("No back history and no eventBus installed");
	    		}
	    		else {
	    			__eventBus.fireEvent(new BackDiscoveryEvent(mPageStack.getCount()==1?mPageStack.peek():null));
	    		}
	    	}
    	}
    }

    public static void navigateToWelcome(IPage currentPage) {
        WelcomePage page = new WelcomePage();
        mPageStack.push(page);
    }

    public static void navigateToQuiz(IPage currentPage) {
        QuizPage quizPage = new QuizPage();
        mPageStack.push(quizPage);
    }

    public static void navigateToLogin() {
        LoginPage loginPage = new LoginPage();
        mPageStack.push(loginPage);
    }

    public static void navigateToPrescriptionResource(IPage currentPage, InmhItemData itemIn, int ordinal) {
        InmhItemData item = null;
        if(ordinal == -1) {
            item = itemIn;
        }
        else {
            for (PrescriptionSessionDataResource r : CatchupMathMobileShared.getUser().getPrescripion().getCurrSession()
                    .getInmhResources()) {
                if (r.getType().equals(itemIn.getType())) {
                    item = r.getItems().get(ordinal);
                    break;
                }
            }
        }
        String lesson = CatchupMathMobileShared.getUser().getPrescripion().getCurrSession().getInmhResources().get(1).getItems().get(0).getFile();


        PrescriptionResourcePage page = new PrescriptionResourcePage(lesson, item);
        mPageStack.push(page);
    }

}
