package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.PrescriptionResourcePage;
import hotmath.gwt.cm_mobile_shared.client.page.QuizPage;
import hotmath.gwt.cm_mobile_shared.client.page.WelcomePage;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

public class Controller {
    private static ObservableStack<IPage> mPageStack;

    private Controller() {
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
