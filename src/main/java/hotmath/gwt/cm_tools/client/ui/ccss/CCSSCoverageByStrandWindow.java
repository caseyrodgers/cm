package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentModelBase;
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
public class CCSSCoverageByStrandWindow extends GWindow {

    private static CCSSCoverageByStrandWindow __instance;

    DateRangeWidget _dateRange = DateRangeWidget.getInstance();

    BorderLayoutContainer _container;

    private static final String TITLE = "CCSS Strand Coverage for ";

    BorderLayoutData _westData;
    BorderLayoutData _centerData;
    CCSSCoverageByStrandListPanel _CCSSCoverageByStrandListPanel;
    GroupDto _groupDto;
    StudentModelI _stuModel;
    int _uid;
    int _adminId;
    boolean _isGroupReport = false;

    public CCSSCoverageByStrandWindow(StudentModelI stuModel, GroupDto groupDto) {
        super(false);
        __instance = this;
        _stuModel = stuModel;
        _groupDto = groupDto;
        if (_groupDto != null) {
        	_uid = _groupDto.getGroupId();
        	_adminId = _groupDto.getAdminId();
        	_isGroupReport = true;
        }
        else {
        	_uid = _stuModel.getUid();
        	_adminId = _stuModel.getAdminUid();
        }

        setHeadingText(TITLE + ((_isGroupReport == false) ? stuModel.getName() : _groupDto.getName()));
        setWidth(580);
        setHeight(500);

        _container = new BorderLayoutContainer();
        _container.setBorders(true);

        _westData = new BorderLayoutData();
        _westData.setSize(253);
        _westData.setFloatable(true);

        _centerData = new BorderLayoutData();
        _centerData.setSize(400);

        _CCSSCoverageByStrandListPanel = new CCSSCoverageByStrandListPanel(_container, _centerData, _uid, _adminId, _isGroupReport);
        _container.setWestWidget(_CCSSCoverageByStrandListPanel, _westData);

        _dateRange.refresh();

        getHeader().addTool(new TextButton("Refresh", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                reloadData();
            }
        }));
/*
        getHeader().addTool(new TextButton("Print Report", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent ce) {
            	if (_isGroupReport == false) {
            		printStudentReport();
            	}
            	else {
            		printGroupCCSSCoverageReport();
            	}
            }
        }));
*/

        HTML html = new HTML(_dateRange.getText());
        html.getElement().setAttribute("style",  "position: absolute;left: 0");
        getButtonBar().add(html);
        super.addCloseButton();

        showDefaultMsg();

        /**
         * turn on after data retrieved
         * 
         */
        setWidget(_container);

        setVisible(true);
    }

    private void showDefaultMsg() {
        CenterLayoutContainer clc = new CenterLayoutContainer();
        HTML defaultMsg = new HTML("<h1>Select a strand</h1>");
        clc.add(defaultMsg);
        _container.setCenterWidget(clc);
        _container.forceLayout();
    }

    int _currentSelection;

    private void reloadData() {
        _container.getCenterWidget().removeFromParent();
        _container.getWestWidget().removeFromParent();

        _CCSSCoverageByStrandListPanel = new CCSSCoverageByStrandListPanel(_container, _centerData, _uid, _adminId, _isGroupReport);
        _container.setWestWidget(_CCSSCoverageByStrandListPanel, _westData);

        _dateRange.refresh();

        showDefaultMsg();
        _container.forceLayout();
    }

    private void printStudentReport() {
    	Date fromDate = _dateRange.getFromDate();
    	Date toDate   = _dateRange.getToDate();

    	new PdfWindowWithNav(_stuModel.getAdminUid(), "Catchup Math CCSS Report for: " + _stuModel.getName(), new GeneratePdfAction(PdfType.STUDENT_CCSS,
    			_adminId, Arrays.asList(_stuModel.getUid()), fromDate, toDate));
    }

    private void printGroupCCSSCoverageReport() {
    	DateRangePanel dateRange = DateRangePanel.getInstance();
    	Date fromDate=null, toDate=null;
    	if (dateRange != null) {
    		fromDate = dateRange.getFromDate();
    		toDate = dateRange.getToDate();
    	}
    	// TODO
    	new PdfWindowWithNav(0, "Catchup Math CCSS Report for: " + _groupDto.getName(), new GeneratePdfAction(PdfType.GROUP_CCSS,
    			_adminId, Arrays.asList(_groupDto.getGroupId()), fromDate, toDate));
    }

    static {
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if (event.getEventType() == EventType.EVENT_TYPE_STUDENT_GRID_FILTERED) {
                    if (__instance != null && __instance.isVisible()) {
                        __instance._dateRange.refresh();
                        __instance.reloadData();
                    }
                }
            }
        });
    }

    public static void startTest() {
        int groupId = 2298;
        int uid=390;
        StudentModelBase stuModel = new StudentModelBase();
        stuModel.setUid(uid);
        stuModel.setAdminUid(2);
        new CCSSCoverageByStrandWindow(stuModel, null);
    }
}
