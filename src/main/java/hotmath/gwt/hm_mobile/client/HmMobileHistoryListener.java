package hotmath.gwt.hm_mobile.client;

import hotmath.gwt.cm_mobile_shared.client.TokenParserGeneric;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.hm_mobile.client.activity.BookListActivity;
import hotmath.gwt.hm_mobile.client.activity.BookSearchActivity;
import hotmath.gwt.hm_mobile.client.activity.BookViewActivity;
import hotmath.gwt.hm_mobile.client.activity.CategoryListActivity;
import hotmath.gwt.hm_mobile.client.activity.TutorViewActivity;
import hotmath.gwt.hm_mobile.client.event.LoadNewPageEvent;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.place.BookListPlace;
import hotmath.gwt.hm_mobile.client.place.BookSearchPlace;
import hotmath.gwt.hm_mobile.client.place.BookViewPlace;
import hotmath.gwt.hm_mobile.client.place.CategoryListPlace;
import hotmath.gwt.hm_mobile.client.place.TutorViewPlace;
import hotmath.gwt.hm_mobile.client.view.BookListView;
import hotmath.gwt.hm_mobile.client.view.BookSearchView;
import hotmath.gwt.hm_mobile.client.view.BookView;
import hotmath.gwt.hm_mobile.client.view.CategoryListView;
import hotmath.gwt.hm_mobile.client.view.HelpView;
import hotmath.gwt.hm_mobile.client.view.HomeView;
import hotmath.gwt.hm_mobile.client.view.TutorView;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;

public class HmMobileHistoryListener implements ValueChangeHandler<String> {
    
        public void onValueChange(ValueChangeEvent<String> event) {
            
            try {
                String historyToken = event.getValue();
    
                final TokenParserGeneric token = new TokenParserGeneric(historyToken);
    
                final String type = token.getToken(0);
                
                if(type == null || type.length() == 0 || type.equals("home")) {
                    HomeView view = HmMobile.__clientFactory.getHomeView();
                    HmMobile.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else if(type.equals("CategoryListPlace")) {
                    CategoryListActivity a = new CategoryListActivity(new CategoryListPlace(historyToken), HmMobile.__clientFactory);
                    CategoryListView view = HmMobile.__clientFactory.getCategoryListView();
                    view.setPresenter(a);
                    HmMobile.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else if(type.equals("BookListPlace")) {
                    BookListActivity activity = new BookListActivity(new BookListPlace(token.getToken(1)),HmMobile.__clientFactory);
                    BookListView view = HmMobile.__clientFactory.getBookListView();
                    view.setPresenter(activity);
                    activity.doLoadBookForSubject(token.getToken(1));
                    
                    HmMobile.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else if(type.equals("BookViewPlace")) {
                    String textCode = token.getToken(1);
                    BookViewActivity act = new BookViewActivity(new BookViewPlace(textCode), HmMobile.__clientFactory);
    
                    BookView view = HmMobile.__clientFactory.getBookView();
                    view.setPresenter(act);
                    act.loadBookInfo(new BookModel(textCode));
                    
                    HmMobile.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else if(type.equals("TutorViewPlace")) {
                    String pid = token.getToken(1);
                    TutorViewActivity act = new TutorViewActivity(new TutorViewPlace(pid),HmMobile.__clientFactory);
                    TutorView view = HmMobile.__clientFactory.getTutorView();
                    view.setPresenter(act);
                    act.getTutor(pid);
                    
                    HmMobile.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else if(type.equals("HomeViewPlace")) {
                    HomeView view = HmMobile.__clientFactory.getHomeView();
                    HmMobile.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else if(type.equals("BookSearchPlace")) {
                	BookSearchView view = HmMobile.__clientFactory.getBookSearchView();
                	BookSearchActivity act = new BookSearchActivity(new BookSearchPlace(""), HmMobile.__clientFactory);
                	view.setPresenter(act);
                	
                	HmMobile.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else if(type.equals("HelpPlace")) {
                	HelpView view = HmMobile.__clientFactory.getHelpView();
                	
                	
                	HmMobile.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));
                }
                
                else {
                    Window.alert("No history handler: " + token);
                }
            }
            catch(Exception ex) {
                String causes=ex.getMessage();
                if(ex instanceof UmbrellaException) {
                    for(Throwable th: ((UmbrellaException)ex).getCauses()) {
                        causes += "\n" + th.getMessage() + "\n";
                    }
                }
                Window.alert("Error processing history: [" + causes + "]" + ", " + event.getValue());
            }
        }
}
