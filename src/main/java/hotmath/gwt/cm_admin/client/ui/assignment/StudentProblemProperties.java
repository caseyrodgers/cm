package hotmath.gwt.cm_admin.client.ui.assignment;


import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface StudentProblemProperties extends PropertyAccess<String> {

	ModelKeyProvider<StudentProblemDto> pid();

	ValueProvider<StudentProblemDto, String> pidLabel();

	ValueProvider<StudentProblemDto, String> status();

}
