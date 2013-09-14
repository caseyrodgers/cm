package hotmath.gwt.cm_tools.client.ui.ccss;

import java.util.Arrays;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CCSSDetail;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSDetailAction;

import com.google.gwt.user.client.ui.HTML;

import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class StandardWindowForCCSS extends GWindow {

    public StandardWindowForCCSS(String stdCode, String desc) {
        super(true);

        setModal(true);
        setWidth(400);
        setHeight(200);
        setMaximizable(true);
        setResizable(true);
        setHeadingHtml("CCSS Definition: " + stdCode);

        if (desc == null) {
        	getSummary(stdCode);
        }
        else {
        	displaySummary(desc);
        }

    }

	private void displaySummary(String desc) {
        FramedPanel fp = new FramedPanel();
        fp.setHeaderVisible(false);

        String html = "<p style='padding: 10px;'>" + desc + "</p>";
        fp.add(new HTML(html));

        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.setScrollMode(ScrollMode.AUTO);
        flow.add(fp);

        setWidget(flow);
        setVisible(true);
	}

	private void getSummary(final String stdCode) {
        new RetryAction<CmList<CCSSDetail>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CCSSDetailAction action = new CCSSDetailAction(Arrays.asList(stdCode));
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CCSSDetail> ccssDetail) {
                CmBusyManager.setBusy(false);
                CmLogger.debug("getDesc(): got details for " + stdCode);
                displaySummary(ccssDetail.get(0).getSummary());
            }
            
            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                CmBusyManager.setBusy(false);
            }
        }.register();
	}

}
