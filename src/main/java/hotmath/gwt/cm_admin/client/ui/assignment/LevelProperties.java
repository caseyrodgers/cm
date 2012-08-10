package hotmath.gwt.cm_admin.client.ui.assignment;


import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface LevelProperties extends PropertyAccess<String> {
    @Path("name")
    ModelKeyProvider<NameValueData> key();
    ValueProvider<NameValueData, String> name();
    ValueProvider<NameValueData, String> value();
  }
