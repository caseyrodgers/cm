package hotmath.gwt.hm_mobile.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.WelcomePage;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;

public class Controller {
    private static ObservableStack<IPage> mPageStack;

    private Controller() {
    }

    public static void init(ObservableStack<IPage> pageStack) {
        mPageStack = pageStack;
        IPage viewPage = (IPage)HmMobile.__clientFactory.getHomeView();
        mPageStack.push(viewPage);
    }

    public static void navigateBack() {
        mPageStack.pop();
    }

    public static void navigateToWelcome(IPage currentPage) {
        WelcomePage page = new WelcomePage();
        mPageStack.push(page);
    }

    public static void navigateToQuiz(IPage currentPage) {
//        QuizPage quizPage = new QuizPage();
//        mPageStack.push(quizPage);
    }
    
    public static void navigateToLogin() {
//        LoginPage loginPage = new LoginPage();
//        mPageStack.push(loginPage);
    }

    public static void navigateToPrescription(IPage currentPage, PrescriptionSessionData sessionData) {
//        PrescriptionPage page = new PrescriptionPage(sessionData);
//        mPageStack.push(page);
    }

    public static void navigateToPrescriptionResource(IPage currentPage, InmhItemData itemIn, int ordinal) {
//        InmhItemData item = null;
//        for (PrescriptionSessionDataResource r : CatchupMathMobile.getUser().getPrescripion().getCurrSession()
//                .getInmhResources()) {
//            if (r.getType().equals(itemIn.getType())) {
//                item = r.getItems().get(ordinal);
//                break;
//            }
//        }
//        PrescriptionResourcePage page = new PrescriptionResourcePage(item);
//        mPageStack.push(page);
    }

}
