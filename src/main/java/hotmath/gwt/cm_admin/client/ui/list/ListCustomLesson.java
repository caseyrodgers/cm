package hotmath.gwt.cm_admin.client.ui.list;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.model.CustomProgramInfoModel;



import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.info.Info;

public class ListCustomLesson extends ListView<CustomLessonModel, String> {

	interface LessonProps extends PropertyAccess<String> {
		ModelKeyProvider<CustomLessonModel> id();
		ValueProvider<CustomLessonModel, String> label();
	}
	static LessonProps props = GWT.create(LessonProps.class);
	private CustomProgramInfoModel info;
	
	public interface CallbackOnDoubleClick {
	    void doubleClicked(CustomLessonModel lessonModel);
	}
	
    public ListCustomLesson(final CallbackOnDoubleClick callOnDoubleClick) {
    	super(new ListStore<CustomLessonModel>(props.id()), props.label());
    	
    	// _listViewCq.setDisplayProperty("customProgramItem");
    	// _listViewCq.setTemplate(getTemplateHtmlForQuizzes());
    	
    	setCell(new SimpleSafeHtmlCell<String>(new AbstractSafeHtmlRenderer<String>() {
            @Override
            public SafeHtml render(String programItem) {
            	String style = findStyleToUse(programItem);
            	String html = "<span class='" + style + "'> &nbsp;</span>&nbsp;&nbsp;" + programItem + "";
                return SafeHtmlUtils.fromTrustedString(html);
            }
        }));
    	
        addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                CustomLessonModel selectedItem = getSelectionModel().getSelectedItem();
                if (callOnDoubleClick != null && selectedItem != null) {
                    callOnDoubleClick.doubleClicked(selectedItem);
                }
            }
        }, DoubleClickEvent.getType());
    }
    
    protected String findStyleToUse(String programItem) {
    	for(CustomLessonModel p: getStore().getAll()) {
    		if(p.getCustomProgramItem().equals(programItem)) {
    			return p.getSubjectStyleClass();
    		}
    	}
		return "";
	}

	private String getTemplateHtmlForQuizzes() {
        String template = "<tpl for=\".\"><div class='x-view-item'><span class='{styleName}'>{customProgramItem}</span></div></tpl>";
        return template;
    }

	public void setProgramInfo(CustomProgramInfoModel info) {
		this.info = info;
	}

	public CustomProgramInfoModel getInfo() {
		return info;
	}

	public void setInfo(CustomProgramInfoModel info) {
		this.info = info;
	}

    public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                ListCustomLesson lcl = new ListCustomLesson(new CallbackOnDoubleClick() {
                    @Override
                    public void doubleClicked(CustomLessonModel lessonModel) {
                        Info.display("Info",  "Lesson clicked: " + lessonModel);
                    }
                });
                lcl.getStore().add(new CustomLessonModel("LESSON", "FILE","SUBJECT"));    
                GWindow w = new GWindow(true);
                w.setWidget(lcl);
                w.setVisible(true);
            }
        });
    }
}
