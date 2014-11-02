package hotmath.gwt.cm_admin.client.ui.list;

import hotmath.gwt.shared.client.model.CustomQuizDef;

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


public class ListCustomQuiz extends ListView<CustomQuizDef, String> {

	interface Props extends PropertyAccess<String> {

        ModelKeyProvider<CustomQuizDef> quizId();
        ValueProvider<CustomQuizDef, String> quizName();
	}
	static Props props = GWT.create(Props.class);
	
	
	static public interface CallbackOnDoubleClick {
		void doubleClicked(CustomQuizDef modelClicked);
	}
    public ListCustomQuiz(final CallbackOnDoubleClick onDoubleClick) {
    	super(new ListStore<CustomQuizDef>(props.quizId()), props.quizName());
    	getSelectionModel().setSelectionMode(SelectionMode.SINGLE);    	
    	
    	
    	setCell(new SimpleSafeHtmlCell<String>(new AbstractSafeHtmlRenderer<String>() {
            @Override
            public SafeHtml render(String item) {
            	String style="";
            	for(CustomQuizDef rec: getStore().getAll()) {
            		if(rec.getQuizName().equals(item)) {
            			style = rec.isArchived()?"custom-archived":"";
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
            	CustomQuizDef selectedItem = getSelectionModel().getSelectedItem();
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
