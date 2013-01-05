package hotmath.gwt.cm_tools.client.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface LessonItemModelProperties extends PropertyAccess<String>{

    @Path("file")
    ModelKeyProvider<LessonItemModel> id();

    ValueProvider<LessonItemModel, String> name();
    ValueProvider<LessonItemModel, String> prescribed();
}
