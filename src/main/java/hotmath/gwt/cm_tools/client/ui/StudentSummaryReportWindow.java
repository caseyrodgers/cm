package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;

import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;

/**
 * Defines window for display of Summary Report PDF, patterned after ReportCardWindow
 * 
 * @author bob
 *
 */
public class StudentSummaryReportWindow extends CmWindow {
    
    public StudentSummaryReportWindow(AccountInfoModel accountInfo, Integer adminUid, List<Integer> studentUids) {

    	createReportRpc(accountInfo, adminUid, studentUids);

        setSize(800, 600);
        setHeading("Catchup Math Summary Report for: " + accountInfo.getSchoolName());

        Button btn = new Button("Close");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });

        addButton(btn);
    }

	private void createReportRpc(final AccountInfoModel account, Integer adminUid, List<Integer> studentUids) {

        CatchupMathTools.setBusy(true);

        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new GeneratePdfAction(PdfType.STUDENT_SUMMARY, adminUid, studentUids), new AsyncCallback<CmWebResource>() {

            public void onSuccess(CmWebResource webResource) {
                Frame frame = new Frame();
                frame.setWidth("100%");
                frame.setHeight("480px");
                frame.setUrl(webResource.getUrl());

                removeAll();
                setLayout(new FitLayout());
                add(frame);

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
