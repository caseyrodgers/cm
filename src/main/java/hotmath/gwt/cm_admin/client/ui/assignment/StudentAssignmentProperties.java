package hotmath.gwt.cm_admin.client.ui.assignment;


import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface StudentAssignmentProperties extends PropertyAccess<String> {

	ModelKeyProvider<StudentAssignment> uid();

	ValueProvider<StudentAssignment, String> studentName();

    ValueProvider<StudentAssignment, CmList<StudentProblemDto>> assignmentStatuses();

}
