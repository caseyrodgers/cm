package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_tools.client.model.StudentModelBase;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.ui.DateRangeWidget;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.PdfWindowWithNav;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;

import java.util.Arrays;
import java.util.Date;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
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

    private static final String TITLE = "CCSS Coverage for ";

    BorderLayoutData _centerData;
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

        setHeadingText(TITLE + assignment.getAssignmentName());
        setWidth(310);
        setHeight(500);

        _container = new BorderLayoutContainer();
        _container.setBorders(true);

        _centerData = new BorderLayoutData();
        _centerData.setSize(300);

        final CCSSCoverageImplAssignment impl = new CCSSCoverageImplAssignment(_assignKey, new CallbackOnComplete() {
			@Override
			public void isComplete() {
		        _container.forceLayout();
			}
        });
        _container.setCenterWidget(impl.getWidget(), _centerData);

/*
        getHeader().addTool(new TextButton("Refresh", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                reloadData();
            }
        }));
*/
        getHeader().addTool(new TextButton("Print Report", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent ce) {
            	printAssignmentCCSSCoverageReport();
            }
        }));

        super.addCloseButton();

        /**
         * turn on after data retrieved
         * 
         */
        setWidget(_container);

        setVisible(true);
        
        forceLayout();
    }

    private void reloadData() {
        _container.getCenterWidget().removeFromParent();

        _container.forceLayout();
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
