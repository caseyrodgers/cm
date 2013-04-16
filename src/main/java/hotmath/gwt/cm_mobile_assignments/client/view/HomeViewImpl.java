package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowFooter;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowHeader;
import com.googlecode.mgwt.ui.client.widget.base.PullPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.BasicCell;

public class HomeViewImpl extends BaseComposite implements HomeView {
    protected LayoutPanel main;

    ClientFactory factory;

    Presenter presenter;

    private PullPanel pullToRefresh;

    private PullArrowHeader pullArrowHeader;

    private PullArrowFooter pullArrowFooter;

    private CellList<Topic> cellList;

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public HomeViewImpl(ClientFactory factoryIn) {
        this.factory = factoryIn;
        initWidget(createPullToRefresh());
    }

    private Widget createPullToRefresh() {
        pullToRefresh = new PullPanel();
        pullArrowHeader = new PullArrowHeader();
        pullToRefresh.setHeader(pullArrowHeader);
        pullArrowFooter = new PullArrowFooter();
        pullToRefresh.setFooter(pullArrowFooter);
        cellList = new CellList<Topic>(new BasicCell<Topic>() {
            @Override
            public String getDisplayString(Topic model) {
                return model.getName();
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
}
