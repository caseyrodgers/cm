package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.PdfWindowWithNav;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;

import java.util.Arrays;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author bob
 * 
 */
public class CCSSCoverageForAssignmentWindow extends GWindow {

    private static CCSSCoverageForAssignmentWindow __instance;

    BorderLayoutContainer _container;

    private static final String TITLE = "Assignment CCSS Coverage";

    BorderLayoutData _centerData = new BorderLayoutData();
    CCSSCoverageListPanel _CCSSCoverageListPanel;
    Assignment _assignment;
    int _assignKey;
    int _adminId;
    boolean _isGroupReport = false;

    public CCSSCoverageForAssignmentWindow(Assignment assignment) {
        super(false);
        __instance = this;
        _assignment = assignment;
        _assignKey = assignment.getAssignKey();
        _adminId = assignment.getAdminId();

        setHeadingText(TITLE);
        setWidth(310);
        setHeight(500);

        _container = new BorderLayoutContainer();
        _container.setBorders(true);

        _centerData.setSize(300);

        final CCSSCoverageImplAssignment impl = new CCSSCoverageImplAssignment(_assignKey, new CallbackOnComplete() {
			@Override
			public void isComplete() {
		        setWidget(_container);
		        forceLayout();
			}
        });
        displaySummary(assignment.getAssignmentName());
        _container.setCenterWidget(impl.getWidget(), _centerData);

        getHeader().addTool(new TextButton("Print Report", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent ce) {
            	printAssignmentCCSSCoverageReport();
            }
        }));

        super.addCloseButton();

        setVisible(true);
    }

    private void displaySummary(String summary) {
    	FlowLayoutContainer flc = new FlowLayoutContainer();
    	flc.setScrollMode(ScrollMode.AUTO);
    	flc.setBorders(false);

    	BorderLayoutData bld = new BorderLayoutData(50);
    	bld.setMargins(new Margins(5));

        flc.add(new HTML("<p style='padding: 5px;'>" + summary + "</p>"));

        _container.setNorthWidget(flc, bld);
	}

    private void printAssignmentCCSSCoverageReport() {
    	new PdfWindowWithNav(_adminId, "Catchup Math CCSS Report for: " + _assignment.getAssignmentName(),
    			new GeneratePdfAction(PdfType.ASSIGNMENT_CCSS,
    			_adminId, Arrays.asList(_assignKey), null, null));
    }

    public static void startTest() {
        Assignment assignment = new Assignment();
        assignment.setAssignKey(3);
        assignment.setAdminId(2);
        new CCSSCoverageForAssignmentWindow(assignment);
    }
}
