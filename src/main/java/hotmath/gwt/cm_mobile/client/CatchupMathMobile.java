package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlSpritedAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * What is the minimum size of CM
 * 
 * @author casey
 * 
 */
public class CatchupMathMobile implements EntryPoint {
    public void onModuleLoad() {
        setupServices();
        LoginForm loginForm = new LoginForm();
        Panel panel = new SimplePanel();
        panel.getElement().appendChild(loginForm.getElement());

        RootPanel.get().add(panel);

        getQuiz();
    }

    private void getQuiz() {
        GetQuizHtmlSpritedAction action = new GetQuizHtmlSpritedAction(23562, 20008, 0);
        getCmService().execute(action, new AsyncCallback<QuizHtmlResult>() {
            @Override
            public void onSuccess(QuizHtmlResult result) {
                Window.alert("Quiz HTML: " + result.getQuizHtml());
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error getting quiz: " + caught);
            }
        });
    }

    private void doLogin() {
        GetCmMobileLoginAction action = new GetCmMobileLoginAction();
        getCmService().execute(action, new AsyncCallback<CmMobileUser>() {
            @Override
            public void onSuccess(CmMobileUser result) {
                Window.alert("Welcome: " + result.getName());
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error logging in: " + caught);
            }
        });
    }

    static CmServiceAsync getCmService() {
        return _cmService;
    }

    static CmServiceAsync _cmService;

    static private void setupServices() {
        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";

        _cmService = (CmServiceAsync) GWT.create(CmService.class);
        ((ServiceDefTarget) _cmService).setServiceEntryPoint(point + "services/cmService");
    }
}
