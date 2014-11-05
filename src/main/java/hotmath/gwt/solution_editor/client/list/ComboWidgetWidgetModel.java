package hotmath.gwt.solution_editor.client.list;

import hotmath.gwt.solution_editor.client.WidgetModel;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class ComboWidgetWidgetModel extends ComboBox<WidgetModel> {
    
    interface Props extends PropertyAccess<WidgetModel> {
        @Path("type")
        ModelKeyProvider<WidgetModel> key();
        LabelProvider<WidgetModel> type();
    }
    static Props props = GWT.create(Props.class);
    
    public ComboWidgetWidgetModel() {
        super(new ListStore<WidgetModel>(props.key()),props.type() );
        setTriggerAction(TriggerAction.ALL);
    }

    public WidgetModel findModelByType(String type) {
        for(WidgetModel w: getStore().getAll()) {
            if(w.getType().equals(type)) {
                return w;
            }
        }
        return null;
    }

}
