package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.googlecode.mgwt.ui.client.widget.celllist.Cell;

public class AssignmentInfoCell<T> implements Cell<StudentAssignmentInfo> {

    private static Template TEMPLATE = GWT.create(Template.class);
    
    public interface Template extends SafeHtmlTemplates{
        @SafeHtmlTemplates.Template("<table width='100%'>" +
                                    "<col width='60%'/>" +
                                    "<col width='20%'/>" +
                                    "<col width='20%'/>" +
                                    "<tr>" +
                                        "<td>{0}</td>" +
                                        "<td>{1}</td>"+
                                        "<td>{2}</td>"+
                                    "</tr>"+
                                "</table>")
        SafeHtml content(String comments, Date dueDate, String score);
    }

    @Override
    public void render(SafeHtmlBuilder safeHtmlBuilder, final StudentAssignmentInfo model) {
        safeHtmlBuilder.append(TEMPLATE.content(model.getComments(), model.getDueDate(),model.getScore()));
    }

    @Override
    public boolean canBeSelected(StudentAssignmentInfo model) {
        return true;
    }

}
