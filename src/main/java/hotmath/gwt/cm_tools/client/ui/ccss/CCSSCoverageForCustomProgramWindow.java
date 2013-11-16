package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.PdfWindowWithNav;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;

import java.util.Arrays;

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
public class CCSSCoverageForCustomProgramWindow extends GWindow {

    private static CCSSCoverageForCustomProgramWindow __instance;

    BorderLayoutContainer _container;

    private static final String TITLE = "Custom Program CCSS Coverage";

    BorderLayoutData _centerData = new BorderLayoutData();
    CCSSCoverageListPanel _CCSSCoverageListPanel;
    CustomProgramModel _cpModel;
    int _cpId;
    int _adminId;
    boolean _isGroupReport = false;
    CallbackOnComplete _callback;

    public CCSSCoverageForCustomProgramWindow(CustomProgramModel cpModel, CallbackOnComplete callback) {
        super(false);
        __instance = this;
        _cpModel = cpModel;
        _cpId = cpModel.getProgramId();
        _callback = callback;

        setHeadingText(TITLE);
        setWidth(310);
        setHeight(470);

        _container = new BorderLayoutContainer();
        _container.setBorders(true);

        _centerData.setSize(300);

        final CCSSCoverageImplCustomProgram impl = new CCSSCoverageImplCustomProgram(_cpId, new CallbackOnComplete() {
			@Override
			public void isComplete() {
		        setWidget(_container);
		        forceLayout();
			}
        });
        displaySummary(cpModel.getProgramName());
        _container.setCenterWidget(impl.getWidget(), _centerData);
/*
        getHeader().addTool(new TextButton("Print Report", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent ce) {
            	printCustomProgramCCSSCoverageReport();
            }
        }));
*/
        super.addCloseButton();
        setVisible(true);
    }

    @Override
    public void hide() {
    	super.hide();
    	if (_callback != null) _callback.isComplete();
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

    private void printCustomProgramCCSSCoverageReport() {
    	new PdfWindowWithNav(_adminId, "Catchup Math CCSS Report for: " + _cpModel.getProgramName(),
    			new GeneratePdfAction(PdfType.CUSTOM_PROG_CCSS,
    			_adminId, Arrays.asList(_cpId), null, null));
    }

    public static void startTest() {
        CustomProgramModel cpm = new CustomProgramModel();
        cpm.setProgramId(3);
        new CCSSCoverageForCustomProgramWindow(cpm, null);
    }
}
