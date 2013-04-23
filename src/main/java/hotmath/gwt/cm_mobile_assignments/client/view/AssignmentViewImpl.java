package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.place.AssProblemPlace;
import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.base.HasRefresh;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowHeader;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowWidget;
import com.googlecode.mgwt.ui.client.widget.base.PullPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.BasicCell;
import com.googlecode.mgwt.ui.client.widget.celllist.CellListWithHeader;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

public class AssignmentViewImpl extends BaseComposite implements AssignmentView {

    private List<StudentProblemDto> lastData;
    private Presenter presenter;
    private ClientFactory factory;
    private PullPanel pullToRefresh;
    private PullArrowHeader pullArrowHeader;
    private CellListWithHeader<StudentProblemDto> cellList;
    private StudentAssignment studentAssignment;

    @Override
    public boolean useScrollPanel() {
        return false;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public AssignmentViewImpl(ClientFactory factoryIn) {
        this.factory = factoryIn;
        initWidget(createPullToRefreshPanel());
    }
    

    @Override
    public void loadAssignment(StudentAssignment studentAss) {
        this.studentAssignment = studentAss;
        
        cellList.getHeader().setText(this.studentAssignment.getAssignment().getAssignmentLabel());
    }

    private Widget createPullToRefreshPanel() {
        pullToRefresh = new PullPanel();
        pullArrowHeader = new PullArrowHeader();
        pullToRefresh.setHeader(pullArrowHeader);
        cellList = new CellListWithHeader<StudentProblemDto>(new BasicCell<StudentProblemDto>() {
            @Override
            public String getDisplayString(StudentProblemDto model) {
                String label = model.getProblem().getLabel();
                String status = model.getStatusForStudent();
                
                if(!status.startsWith("View") && !status.startsWith("Unanswered")) {
                    label += " (" + status + ")";
                }
                    
                return label;
            }
        });
        cellList.getCellList().addCellSelectedHandler(new CellSelectedHandler() {
            @Override
            public void onCellSelected(CellSelectedEvent event) {
                StudentProblemDto problem = lastData.get(event.getIndex());
                factory.getPlaceController().goTo(new AssProblemPlace(studentAssignment.getAssignment().getAssignKey(), problem.getPid()));
            }
            
        });
        pullToRefresh.add(cellList);
        return pullToRefresh;
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
    public HasRefresh getPullPanel() {
        return pullToRefresh;
    }

    @Override
    public void render(List<StudentProblemDto> list) {
        this.lastData = list;
        cellList.getCellList().render(list);
    }

    @Override
    public void refresh() {
    }

    @Override
    public void setHeaderPullHandler(PullArrowStandardHandler headerHandler) {
        pullToRefresh.setHeaderPullhandler(headerHandler);
    }

    
    @Override
    public Place getBackPlace() {
        return new HomePlace();
    }

}
