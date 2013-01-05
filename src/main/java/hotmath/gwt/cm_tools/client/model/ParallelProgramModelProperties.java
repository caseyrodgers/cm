package hotmath.gwt.cm_tools.client.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;


public interface ParallelProgramModelProperties extends PropertyAccess<String> {
    @Path("id")
    ModelKeyProvider<ParallelProgramModel> id();

    ValueProvider<ParallelProgramModel, Integer> studentCount();

    ValueProvider<ParallelProgramModel, String> programName();

    ValueProvider<ParallelProgramModel, String> name();
}