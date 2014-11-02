package hotmath.gwt.cm_admin.client.ui.list;

import hotmath.gwt.cm_tools.client.model.CustomProgramModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;

public class ListCustomProgram extends ListView<CustomProgramModel, String> {

	interface CustomProgramProps extends PropertyAccess<String> {
		ModelKeyProvider<CustomProgramModel> programId();
		ValueProvider<CustomProgramModel, String> programName();
	}
	static CustomProgramProps props = GWT.create(CustomProgramProps.class);
	
	
	static public interface CallbackOnDoubleClick {
		void doubleClicked(CustomProgramModel modelClicked);
	}
    public ListCustomProgram(final CallbackOnDoubleClick onDoubleClick) {
    	super(new ListStore<CustomProgramModel>(props.programId()), props.programName());
    	getSelectionModel().setSelectionMode(SelectionMode.SINGLE);    	
    	
    	
    	setCell(new SimpleSafeHtmlCell<String>(new AbstractSafeHtmlRenderer<String>() {
            @Override
            public SafeHtml render(String item) {
            	String style="";
            	for(CustomProgramModel rec: getStore().getAll()) {
            		if(rec.getProgramName().equals(item)) {
            			style = rec.getStyleName();
            			break;
            		}
            	}
            	String html = "<span class='" + style + "'> " + item + "</span>";
                return SafeHtmlUtils.fromTrustedString(html);
            }
        }));

    	
    	
    	addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                CustomProgramModel selectedItem = getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                	onDoubleClick.doubleClicked(selectedItem);
                }
            }
        }, DoubleClickEvent.getType());
    	
//        _listViewCp.setDisplayProperty("programName");
//        _listViewCp.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
//            public void handleEvent(BaseEvent be) {
//                editCustomProgram(false);
//            }
//        });
//        _listViewCp.setTemplate(getTemplateHtmlForPrograms());
    	
    }
    
    private String getTemplateHtmlForPrograms() {
        String template = "<tpl for=\".\"><div class='x-view-item'><span class='{styleName}'>{programName}</span></div></tpl>";
        return template;
    }

}
