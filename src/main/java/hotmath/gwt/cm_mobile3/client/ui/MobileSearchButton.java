package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.activity.SearchActivity;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.ui.TouchAnchor;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class MobileSearchButton extends TouchAnchor {
    
    boolean disallowSearch;
    private ClientFactory cf;
    
    public MobileSearchButton(ClientFactory clientFactory) {
        this.cf = clientFactory;
        addStyleName("mobile-search-button");
        getElement().setInnerHTML("<img src='/gwt-resources/images/search_mobile-enabled.png'/>");
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(disallowSearch) {
                    PopupMessageBox.showMessage("Search is not allowed during quizzes.");;
                }
                else {
                    SearchActivity search = new SearchActivity(cf,cf.getEventBus());
                    cf.getSearchView().setPresenter(search);
                    cf.getEventBus().fireEvent(new LoadNewPageEvent((IPage)cf.getSearchView() ));
                }
            }
        });
    }

    public void setAllowSearch(boolean yesNo) {
        this.disallowSearch = !yesNo;
    }
}
