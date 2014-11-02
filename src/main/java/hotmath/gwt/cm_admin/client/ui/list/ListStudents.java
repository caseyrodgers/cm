package hotmath.gwt.cm_admin.client.ui.list;

import hotmath.gwt.cm_tools.client.model.StudentModelExt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;

public class ListStudents extends ListView<StudentModelExt, String> {

	interface Props extends PropertyAccess<String> {
		ModelKeyProvider<StudentModelExt> uid();
		ValueProvider<StudentModelExt, String> name();
	}
	static Props props = GWT.create(Props.class);
	
	public interface CallbackOnDoubleClick {
	    void doubleClicked(StudentModelExt student);
	}
	
    public ListStudents(final CallbackOnDoubleClick callOnDoubleClick) {
    	super(new ListStore<StudentModelExt>(props.uid()), props.name());

//    	setCell(new SimpleSafeHtmlCell<String>(new AbstractSafeHtmlRenderer<String>() {
//            @Override
//            public SafeHtml render(String programItem) {
//            	String style = findStyleToUse(programItem);
//            	String html = "<span class='" + style + "'> &nbsp;</span>&nbsp;&nbsp;" + programItem + "";
//                return SafeHtmlUtils.fromTrustedString(html);
//            }
//        }));
    	
        addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                StudentModelExt selectedItem = getSelectionModel().getSelectedItem();
                if (callOnDoubleClick != null && selectedItem != null) {
                    callOnDoubleClick.doubleClicked(selectedItem);
                }
            }
        }, DoubleClickEvent.getType());
    }
}
