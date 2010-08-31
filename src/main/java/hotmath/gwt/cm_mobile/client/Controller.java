package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.page.IPage;
import hotmath.gwt.cm_mobile.client.page.PrescriptionPage;
import hotmath.gwt.cm_mobile.client.page.PrescriptionResourcePage;
import hotmath.gwt.cm_mobile.client.page.QuizPage;
import hotmath.gwt.cm_mobile.client.page.WelcomePage;
import hotmath.gwt.cm_mobile.client.util.ObservableStack;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;

public class Controller {
	private static ObservableStack<IPage> mPageStack;
	private Controller() {
	}

	public static void init(ObservableStack<IPage> pageStack) {
		mPageStack = pageStack;
		LoginPage p = new LoginPage();
		mPageStack.push(p);
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
	public static void navigateToPrescription(IPage currentPage,PrescriptionSessionData sessionData) {
		PrescriptionPage page = new PrescriptionPage(sessionData);
		mPageStack.push(page);
	}
	
	public static void navigateToPrescriptionResource(IPage currentPage,InmhItemData item, int ordinal) {
		PrescriptionResourcePage page = new PrescriptionResourcePage(item);
		mPageStack.push(page);
	}
}
