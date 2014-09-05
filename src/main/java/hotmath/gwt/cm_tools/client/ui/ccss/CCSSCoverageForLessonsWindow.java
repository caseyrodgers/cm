package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author bob
 * 
 */
public class CCSSCoverageForLessonsWindow extends GWindow {

    private static CCSSCoverageForLessonsWindow __instance;

    BorderLayoutContainer _container;

    private static final String TITLE = "CCSS Coverage";

    BorderLayoutData _centerData = new BorderLayoutData();
    CCSSCoverageListPanel _CCSSCoverageListPanel;
    String _name;
    
    int _adminUid;
    boolean _isGroupReport = false;

    public CCSSCoverageForLessonsWindow(List<LessonModel> lessons, int adminUid, String name) {
        super(false);
        __instance = this;
        _adminUid = adminUid;
        _name = name;

        setHeadingText(TITLE);
        setWidth(385);
        setHeight(400);

        _container = new BorderLayoutContainer();
        _container.setBorders(true);

        _centerData.setSize(230);

        final CCSSCoverageImplLessons impl = new CCSSCoverageImplLessons(lessons, new CallbackOnComplete() {
			@Override
			public void isComplete() {
		        setWidget(_container);
		        forceLayout();
			}
        });
        displayName(name);
        _container.setCenterWidget(impl.getWidget(), _centerData);

/*
        getHeader().addTool(new TextButton("Print Report", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent ce) {
            	printLessonCCSSCoverageReport();
            }
        }));
*/
        super.addCloseButton();

        setVisible(true);
    }

    private void displayName(String name) {
    	FlowLayoutContainer flc = new FlowLayoutContainer();
    	flc.setScrollMode(ScrollMode.AUTO);
    	flc.setBorders(false);

    	BorderLayoutData bld = new BorderLayoutData(20);
    	bld.setMargins(new Margins(5));

        //flc.add(new HTML("<p style='padding: 5px;'>" + name + "</p>"));

        _container.setNorthWidget(flc, bld);
	}

    private void printLessonsCCSSCoverageReport() {
    	/*  TODO
    	new PdfWindowWithNav(_adminUid, "Catchup Math CCSS Report for Lessons",
    			new GeneratePdfAction(PdfType.LESSONS_CCSS,
    			_adminUid, Arrays.asList(_lessonFile), null, null));
        */
    }

    public static void startTest() {
        LessonModel lesson = new LessonModel();
        lesson.setLessonName("Area");
        lesson.setLessonFile("/topics/area.html");
        List<LessonModel> lessons = new ArrayList<LessonModel>();
        lessons.add(lesson);
        lesson = new LessonModel();
        lesson.setLessonName("Area of a Circle");
        lesson.setLessonFile("/topics/area-of-a-circle.html");
        lessons.add(lesson);
        new CCSSCoverageForLessonsWindow(lessons, 6, "test");
    }
}
