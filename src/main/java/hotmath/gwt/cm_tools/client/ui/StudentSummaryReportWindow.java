package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;

/**
 * Defines window for display of Summary Report PDF, patterned after ReportCardWindow
 * 
 * @author bob
 *
 */
public class StudentSummaryReportWindow extends GWindow {
    
    public StudentSummaryReportWindow(AccountInfoModel accountInfo, Integer adminUid, List<Integer> studentUids) {
        super(true);
        
    	createReportRpc(accountInfo, adminUid, studentUids);

        setPixelSize(800, 600);
        setHeadingText("Catchup Math Summary Report for: " + accountInfo.getSchoolName());
    }

	private void createReportRpc(final AccountInfoModel account, Integer adminUid, List<Integer> studentUids) {

        CatchupMathTools.setBusy(true);

        CmServiceAsync s = CmShared.getCmService();
        s.execute(new GeneratePdfAction(PdfType.STUDENT_SUMMARY, adminUid, studentUids), new AsyncCallback<CmWebResource>() {

            public void onSuccess(CmWebResource webResource) {
                Frame frame = new Frame();
                frame.setWidth("100%");
                frame.setHeight("480px");
                frame.setUrl(webResource.getUrl());

                clear();
                setWidget(frame);

                setVisible(true);

                CatchupMathTools.setBusy(false);
            }

            public void onFailure(Throwable caught) {
                CatchupMathTools.setBusy(false);
                String msg = caught.getMessage();
                CatchupMathTools.showAlert(msg);
            }
        });
    }

}
