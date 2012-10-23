package hotmath.gwt.cm_admin.client.ui.assignment;


import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface StudentAssignmentProperties extends PropertyAccess<String> {

	ModelKeyProvider<StudentAssignment> uid();

	ValueProvider<StudentAssignment, String> studentName();

	ValueProvider<StudentAssignment, String> homeworkStatus();

	ValueProvider<StudentAssignment, String> homeworkGrade();
	
	ValueProvider<StudentAssignment, String> studentDetailStatus();
}
