package hotmath.gwt.cm_tools.client.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface ParallelProgramUsageModelProperties extends PropertyAccess<String> {
    @Path("userId")
    ModelKeyProvider<ParallelProgramUsageModel> id();

    ValueProvider<ParallelProgramUsageModel, Integer> studentCount();

    ValueProvider<ParallelProgramUsageModel, String> programName();

    ValueProvider<ParallelProgramUsageModel, String> name();

    ValueProvider<ParallelProgramUsageModel, String> studentName();

    ValueProvider<ParallelProgramUsageModel, String> activity();

    ValueProvider<ParallelProgramUsageModel, String> result();

    ValueProvider<ParallelProgramUsageModel, String> useDate();
}