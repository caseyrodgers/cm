package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile3.client.activity.LoginActivity;
import hotmath.gwt.cm_mobile3.client.activity.QuizActivity;
import hotmath.gwt.cm_mobile3.client.activity.ShowWorkActivity;
import hotmath.gwt.cm_mobile3.client.activity.WelcomeActivity;
import hotmath.gwt.cm_mobile3.client.view.LoginView;
import hotmath.gwt.cm_mobile3.client.view.QuizView;
import hotmath.gwt.cm_mobile3.client.view.ShowWorkView;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;

public class CatchupMathMobileHistoryListener implements ValueChangeHandler<String> {
    public void onValueChange(ValueChangeEvent<String> event) {
        
        String historyToken = event.getValue();

        final TokenParser token = new TokenParser(historyToken);

        try {
            final String type = token.getType();
            
            if(type == null || type.equals("login")) {
                LoginActivity activity = new LoginActivity(CatchupMathMobile3.__clientFactory.getEventBus());
                LoginView view = CatchupMathMobile3.__clientFactory.getLoginView();
                view.setPresenter(activity);
                activity.prepareLogin(view);
                CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));            
            }
            else if (type.equals("welcome") ) {
                WelcomeActivity activity = new WelcomeActivity(CatchupMathMobile3.__clientFactory.getEventBus());
                WelcomeView view = CatchupMathMobile3.__clientFactory.getWelcomeView();
                view.setPresenter(activity);
                activity.prepareView(view);
                CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));            
            }
            else if(type.equals("quiz")) {
                QuizActivity activity = new QuizActivity(CatchupMathMobile3.__clientFactory.getEventBus());
                QuizView view = CatchupMathMobile3.__clientFactory.getQuizView();
                view.setPresenter(activity);
                CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));
            }
            else if(type.equals("show_work")) {
                ShowWorkActivity activity = new ShowWorkActivity(CatchupMathMobile3.__clientFactory.getEventBus());
                ShowWorkView view = CatchupMathMobile3.__clientFactory.getShowWorkView();
                view.setPresenter(activity);
                CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));
            }
            else {
                Window.alert("NOT IMPLEMENTED: " + token.getHistoryTag());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    }
}