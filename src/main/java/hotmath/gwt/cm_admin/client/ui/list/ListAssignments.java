package hotmath.gwt.cm_admin.client.ui.list;

import hotmath.gwt.cm_tools.client.model.StudentModelExt;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;

public class ListAssignments extends ListView<StudentModelExt, String> {
    	
    	interface Props extends PropertyAccess<String> {
    		ModelKeyProvider<StudentModelExt> id();
    		ValueProvider<StudentModelExt, String> name();    		
    	}
    	static Props props = GWT.create(Props.class);
    	
        public ListAssignments() {
        	super(new ListStore<StudentModelExt>(props.id()), props.name());
            getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            
            // setDisplayProperty("name");
        }
        public void addStudents(List<StudentModelExt> students) {
            getStore().addAll(students);
        }
    }