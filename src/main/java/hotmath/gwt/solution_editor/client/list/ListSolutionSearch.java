package hotmath.gwt.solution_editor.client.list;

import hotmath.gwt.solution_editor.client.SolutionSearchModel;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;


public class ListSolutionSearch extends ListView<SolutionSearchModel, String> {
	
	interface Props extends PropertyAccess<String> {
		@Path("pid")
		ModelKeyProvider<SolutionSearchModel> key();
		ValueProvider<SolutionSearchModel, String> pid();
        ValueProvider<SolutionSearchModel, String> label();
	}
	
	static Props props = GWT.create(Props.class);
	public ListSolutionSearch() {
		super(new ListStore<SolutionSearchModel>(props.key()),props.label());
	}
	
//    static public native String getTemplate() /*-{ 
//    return [ 
//    '<tpl for="."><div class="x-view-item">', 
//    '<h3><span>{pid}</span></h3>', 
//    '</div></tpl>' 
//    ].join(""); 
//    }-*/;

}
