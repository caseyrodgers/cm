package hotmath.gwt.solution_editor.client.list;

import hotmath.gwt.solution_editor.client.WidgetModel;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;

public class ListWidgetModel extends ListView<WidgetModel, String>  {

    interface Props extends PropertyAccess<String> {
        ValueProvider<WidgetModel, String> name();
        @Path("name")
        ModelKeyProvider<WidgetModel> key();
    }
    final static Props props = GWT.create(Props.class);
    public ListWidgetModel() {
        super(new ListStore<WidgetModel>(props.key()), props.name());
    }
 }
