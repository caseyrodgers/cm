package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_mobile_assignments.client.Item;

import java.util.List;

import com.googlecode.mgwt.ui.client.widget.base.HasRefresh;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowWidget;


public interface HomeView extends BaseView {
    void loadUser();
    void setPresenter(Presenter listener);
    public interface Presenter {}     
    
    
    public PullArrowWidget getPullHeader();

    public PullArrowWidget getPullFooter();

    public HasRefresh getPullPanel();
    
    void render(List<Item> list);
    void refresh();
    
    void setFooterPullHandler(PullArrowStandardHandler footerHandler);
    void setHeaderPullHandler(PullArrowStandardHandler headerHandler);
}
