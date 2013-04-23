package hotmath.gwt.cm_mobile_assignments.client.view;

import java.util.List;

import com.googlecode.mgwt.ui.client.widget.base.HasRefresh;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowWidget;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;

public interface AssignmentView extends BaseView {
    void loadAssignment(StudentAssignment studentAss);
    interface Presenter {}
    void setPresenter(Presenter p);
    PullArrowWidget getPullHeader();
    HasRefresh getPullPanel();
    void render(List<StudentProblemDto> list);
    void refresh();
    void setHeaderPullHandler(PullArrowStandardHandler headerHandler);
}
