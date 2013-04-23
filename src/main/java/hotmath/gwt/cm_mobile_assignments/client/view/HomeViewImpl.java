package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.place.AssignmentPlace;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.base.HasRefresh;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowHeader;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowWidget;
import com.googlecode.mgwt.ui.client.widget.base.PullPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.CellListWithHeader;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;
//import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;

public class HomeViewImpl extends BaseComposite implements HomeView {
    
    protected LayoutPanel main;
    ClientFactory factory;
    Presenter presenter;
    private PullPanel pullToRefresh;
    private PullArrowHeader pullArrowHeader;
//    private CellListWithHeader<StudentAssignmentInfo> cellList;
//    private List<StudentAssignmentInfo> lastData;
    private CellListWithHeader<StudentAssignmentInfo> cellList;
    private List<StudentAssignmentInfo> lastData;

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
        initWidget(createPullToRefreshPanel());
    }

    private Widget createPullToRefreshPanel() {
        pullToRefresh = new PullPanel();
        pullArrowHeader = new PullArrowHeader();
        pullToRefresh.setHeader(pullArrowHeader);
        cellList =new CellListWithHeader<StudentAssignmentInfo>(new AssignmentInfoCell<StudentAssignmentInfo>());
        cellList.getHeader().setText("Your Assignments");

        cellList.getCellList().addCellSelectedHandler(new CellSelectedHandler() {
            @Override
            public void onCellSelected(CellSelectedEvent event) {
                StudentAssignmentInfo ass = lastData.get(event.getIndex());
                factory.getPlaceController().goTo(new AssignmentPlace(ass.getAssignKey()));
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
        return null;
    }

    @Override
    public HasRefresh getPullPanel() {
        return pullToRefresh;
    }

    @Override
    public void render(List<StudentAssignmentInfo> list) {
        this.lastData = list;
        cellList.getCellList().render(list);
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
