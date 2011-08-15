package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile3.client.view.LoginView;
import hotmath.gwt.cm_mobile3.client.view.QuizView;
import hotmath.gwt.cm_mobile3.client.view.ShowWorkView;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;

import com.google.gwt.event.shared.EventBus;

public interface ClientFactory {
    EventBus getEventBus();
    PagesContainerPanel getPagesContainer();
    LoginView getLoginView();
    QuizView getQuizView();
    WelcomeView getWelcomeView();
    ShowWorkView getShowWorkView();
}