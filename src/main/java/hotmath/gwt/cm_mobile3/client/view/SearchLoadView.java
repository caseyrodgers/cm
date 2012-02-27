package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile3.client.activity.SearchActivity.CallBack;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;


public interface SearchLoadView extends IPage{
    
    public void setPresenter(Presenter presenter);
    public interface Presenter {
        void loadSearchResult(String topicFile, CallBack callBack);
    }
}
