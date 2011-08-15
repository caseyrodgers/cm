package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile3.client.view.LoginView;
import hotmath.gwt.cm_mobile3.client.view.LoginViewImpl;
import hotmath.gwt.cm_mobile3.client.view.QuizView;
import hotmath.gwt.cm_mobile3.client.view.QuizViewImpl;
import hotmath.gwt.cm_mobile3.client.view.ShowWorkView;
import hotmath.gwt.cm_mobile3.client.view.ShowWorkViewImpl;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile3.client.view.WelcomeViewImpl;
import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class ClientFactoryImplBase implements ClientFactory {

    EventBus eventBus = new SimpleEventBus();

    PagesContainerPanel pagesContainer = new PagesContainerPanelImplIPad();
    WelcomeView welcomeView = new WelcomeViewImpl();
    LoginView loginView = new LoginViewImpl();
    QuizView quizView = new QuizViewImpl();
    ShowWorkView showWorkView = new ShowWorkViewImpl();
    
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PagesContainerPanel getPagesContainer() {
        return pagesContainer;
    }
    
    @Override
    public WelcomeView getWelcomeView() {
        return welcomeView;
    }

    @Override
    public LoginView getLoginView() {
        return loginView;
    }

    @Override
    public QuizView getQuizView() {
        return quizView;
    }

    @Override
    public ShowWorkView getShowWorkView() {
        return showWorkView;
    }
}
