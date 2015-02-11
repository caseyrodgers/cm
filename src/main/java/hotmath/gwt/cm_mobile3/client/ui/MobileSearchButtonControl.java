package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.activity.SearchActivity;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;

public class MobileSearchButtonControl {
    
    boolean disallowSearch;
    private ClientFactory cf;
    private SearchActivity _searchActivity;
    
    public MobileSearchButtonControl(ClientFactory clientFactory) {
        this.cf = clientFactory;
        _searchActivity = new SearchActivity(cf,cf.getEventBus());
    }

    public void showSearchPanel() {
        cf.getSearchView().setPresenter(_searchActivity);
        cf.getEventBus().fireEvent(new LoadNewPageEvent((IPage)cf.getSearchView() ));
    }
    
    public boolean isAllowed() {
        return !disallowSearch;
    }
    public void setAllowSearch(boolean yesNo) {
        this.disallowSearch = !yesNo;
    }
}
