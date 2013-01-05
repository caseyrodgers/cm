package hotmath.gwt.cm_tools.client.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;



public interface SubjectModelProperties extends PropertyAccess<String> {
    @Path("subject")
    ModelKeyProvider<SubjectModel> id();

    LabelProvider<SubjectModel> subject();
}