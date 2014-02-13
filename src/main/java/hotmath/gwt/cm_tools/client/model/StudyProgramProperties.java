package hotmath.gwt.cm_tools.client.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface StudyProgramProperties extends PropertyAccess<String> {
    @Path("uiId")
    ModelKeyProvider<StudyProgramExt> id();

    LabelProvider<StudyProgramExt> title();

    LabelProvider<StudyProgramExt> label();
}

