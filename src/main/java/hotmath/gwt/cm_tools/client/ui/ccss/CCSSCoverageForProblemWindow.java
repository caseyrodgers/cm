package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
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
public class CCSSCoverageForProblemWindow extends GWindow {

    private static CCSSCoverageForProblemWindow __instance;

    BorderLayoutContainer _container;

    private static final String TITLE = "CCSS Coverage";

    BorderLayoutData _centerData = new BorderLayoutData();
    CCSSCoverageListPanel _CCSSCoverageListPanel;
    ProblemDto _problem;
    String _pid;
    
    int _adminUid;
    boolean _isGroupReport = false;

    public CCSSCoverageForProblemWindow(ProblemDto problem, int adminUid) {
        super(false);
        __instance = this;
        _problem = problem;
        _pid = _problem.getPid();
        _adminUid = adminUid;

        setHeadingText(TITLE);
        setWidth(120);
        setHeight(300);

        _container = new BorderLayoutContainer();
        _container.setBorders(true);

        _centerData.setSize(110);

        final CCSSCoverageImplProblem impl = new CCSSCoverageImplProblem(_pid, new CallbackOnComplete() {
			@Override
			public void isComplete() {
		        setWidget(_container);
		        forceLayout();
			}
        });
        displayLesson(_problem.getLesson().getLessonName());
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
    	new PdfWindowWithNav(_adminUid, "Catchup Math CCSS Report for Problem: " + _lesson.getLessonName(),
    			new GeneratePdfAction(PdfType.LESSON_CCSS,
    			_adminUid, Arrays.asList(_lessonFile), null, null));
        */
    }

}
