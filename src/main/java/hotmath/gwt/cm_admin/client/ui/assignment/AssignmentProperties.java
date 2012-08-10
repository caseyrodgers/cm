package hotmath.gwt.cm_admin.client.ui.assignment;


import hotmath.gwt.cm_rpc.client.model.Assignment;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface AssignmentProperties extends PropertyAccess<String> {
    @Path("assignKey")
    ModelKeyProvider<Assignment> key();
    ValueProvider<Assignment, String> assignmentName();
  }
