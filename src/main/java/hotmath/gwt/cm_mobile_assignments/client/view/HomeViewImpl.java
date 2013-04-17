package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.Item;

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.base.HasRefresh;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowFooter;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowHeader;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowWidget;
import com.googlecode.mgwt.ui.client.widget.base.PullPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.BasicCell;

public class HomeViewImpl extends BaseComposite implements HomeView {
    protected LayoutPanel main;

    ClientFactory factory;

    Presenter presenter;

    private PullPanel pullToRefresh;

    private PullArrowHeader pullArrowHeader;

    private PullArrowFooter pullArrowFooter;

    private CellList<Item> cellList;

    @Override
    public boolean useScrollPanel() {
        return false;
    }
    
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public HomeViewImpl(ClientFactory factoryIn) {
        this.factory = factoryIn;
        initWidget(new HTML("TEST"));// createPullToRefresh());
    }

    private Widget createPullToRefresh() {
        pullToRefresh = new PullPanel();
        pullArrowHeader = new PullArrowHeader();
        pullToRefresh.setHeader(pullArrowHeader);
        pullArrowFooter = new PullArrowFooter();
        pullToRefresh.setFooter(pullArrowFooter);
        cellList = new CellList<Item>(new BasicCell<Item>() {
            @Override
            public String getDisplayString(Item model) {
                return model.getDisplayString();
            }
        });
        pullToRefresh.add(cellList);
        return pullToRefresh;
    }

    @Override
    public void loadUser() {
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public PullArrowWidget getPullHeader() {
        return pullArrowHeader;
    }

    @Override
    public PullArrowWidget getPullFooter() {
        return pullArrowFooter;
    }

    @Override
    public HasRefresh getPullPanel() {
        return pullToRefresh;
    }
    
    @Override
    public void render(List<Item> list) {
        cellList.render(list);
    }
    
    @Override
    public void refresh() {
    }

    @Override
    public void setFooterPullHandler(PullArrowStandardHandler footerHandler) {
        pullToRefresh.setFooterPullHandler(footerHandler);
    }

    @Override
    public void setHeaderPullHandler(PullArrowStandardHandler headerHandler) {
        pullToRefresh.setHeaderPullhandler(headerHandler);
    }
}
