package hotmath.gwt.cm_admin.client.ui.assignment;


import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentDto;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface AssignStudentsProperties extends PropertyAccess<String> {
    @Path("uid")
    ModelKeyProvider<StudentDto> uid();
    ValueProvider<StudentDto, String> name();
  }
