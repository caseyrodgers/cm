package hotmath.gwt.solution_editor.client.list;

import hotmath.gwt.solution_editor.client.model.SolutionResourceModel;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;

public class ListSolutionResource extends ListView<SolutionResourceModel, String> {
    
    interface Props extends PropertyAccess<String> {
        ModelKeyProvider<SolutionResourceModel> file();        
        ValueProvider<SolutionResourceModel, String> url();
    }
    final static Props props = GWT.create(Props.class);
    
    public ListSolutionResource() {
        super(new ListStore<SolutionResourceModel>(props.file()), props.url());
    }
}
