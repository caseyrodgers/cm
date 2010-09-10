package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.PrescriptionPage;
import hotmath.gwt.cm_mobile_shared.client.page.PrescriptionResourcePage;
import hotmath.gwt.cm_mobile_shared.client.page.QuizPage;
import hotmath.gwt.cm_mobile_shared.client.page.WelcomePage;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

public class Controller {
    private static ObservableStack<IPage> mPageStack;

    private Controller() {
    }

    public static void init(ObservableStack<IPage> pageStack) {
        mPageStack = pageStack;

        TopicListPage p = new TopicListPage();
        mPageStack.push(p);
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
        mPageStack.pop();
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

    public static void navigateToPrescription(IPage currentPage, PrescriptionSessionData sessionData) {
        PrescriptionPage page = new PrescriptionPage(sessionData);
        mPageStack.push(page);
    }

    public static void navigateToPrescriptionResource(IPage currentPage, InmhItemData itemIn, int ordinal) {
        InmhItemData item = null;
        for (PrescriptionSessionDataResource r : CatchupMathMobileShared.getUser().getPrescripion().getCurrSession()
                .getInmhResources()) {
            if (r.getType().equals(itemIn.getType())) {
                item = r.getItems().get(ordinal);
                break;
            }
        }
        PrescriptionResourcePage page = new PrescriptionResourcePage(item);
        mPageStack.push(page);
    }

}
