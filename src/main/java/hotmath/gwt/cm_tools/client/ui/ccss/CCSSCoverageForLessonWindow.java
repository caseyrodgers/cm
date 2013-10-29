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

/**
 * 
 * @author bob
 * 
 */
public class CCSSCoverageForLessonWindow extends GWindow {

    private static CCSSCoverageForLessonWindow __instance;

    BorderLayoutContainer _container;

    private static final String TITLE = "CCSS Coverage";

    BorderLayoutData _centerData = new BorderLayoutData();
    CCSSCoverageListPanel _CCSSCoverageListPanel;
    LessonModel _lesson;
    String _lessonFile;
    
    int _adminUid;
    boolean _isGroupReport = false;

    public CCSSCoverageForLessonWindow(LessonModel lesson, int adminUid) {
        super(false);
        __instance = this;
        _lessonFile = lesson.getLessonFile();
        _lesson = lesson;
        _adminUid = adminUid;

        setHeadingText(TITLE);
        setWidth(120);
        setHeight(300);

        _container = new BorderLayoutContainer();
        _container.setBorders(true);

        _centerData.setSize(110);

        final CCSSCoverageImplLesson impl = new CCSSCoverageImplLesson(_lessonFile, new CallbackOnComplete() {
			@Override
			public void isComplete() {
		        setWidget(_container);
		        forceLayout();
			}
        });
        displayLesson(_lesson.getLessonName());
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

    private void displayLesson(String lessonName) {
    	FlowLayoutContainer flc = new FlowLayoutContainer();
    	flc.setScrollMode(ScrollMode.AUTO);
    	flc.setBorders(false);

    	BorderLayoutData bld = new BorderLayoutData(20);
    	bld.setMargins(new Margins(5));

        flc.add(new HTML("<p style='padding: 5px;'>Lesson: " + lessonName + "</p>"));

        _container.setNorthWidget(flc, bld);
	}

    private void printLessonCCSSCoverageReport() {
    	/*  TODO
    	new PdfWindowWithNav(_adminUid, "Catchup Math CCSS Report for Lesson: " + _lesson.getLessonName(),
    			new GeneratePdfAction(PdfType.LESSON_CCSS,
    			_adminUid, Arrays.asList(_lessonFile), null, null));
        */
    }

    public static void startTest() {
        LessonModel lesson = new LessonModel();
        lesson.setLessonName("Area");
        lesson.setLessonFile("/topics/Area.html");
        new CCSSCoverageForLessonWindow(lesson, 6);
    }
}
