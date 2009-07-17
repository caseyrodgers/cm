package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewer;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ShowWorkPanel;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Display Student's Lesson Topic Status
 *
 * @author bob
 * 
 */
public class StudentLessonTopicsStatusWindow extends CmWindow {

    StudentModel student;
    Integer runId;
    String programName;
    StudentActivityModel activityModel;
    HTML html;

    public StudentLessonTopicsStatusWindow(StudentModel student, StudentActivityModel activityModel) {
        setStyleName("student-lesson-topic-status-window");
        this.student = student;
        this.activityModel = activityModel;
        runId = activityModel.getRunId();
        setSize(400, 300);
        setResizable(false);
        super.setModal(true);
        
        programName = activityModel.getProgramDescr();

        setLayout(new BorderLayout());
        String title = "Lesson Topics for " + student.getName();
        if(programName != null)
            title += " in program " + programName;
        setHeading(title);
        
        html = new HTML();
        html.setHeight("70px");
        add(html);

        Button closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });

        addButton(closeBtn);
        setVisible(false);
        
        this.getStudentLessonTopicsRPC(new ListStore<LessonItemModel>());

    }

    private void formatLessonTopics(List<LessonItemModel> l){
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='padding-left: 10px; padding-top: 10px; padding-bottom: 10px'>");
        int height = 0;
        int row = 0;
        for (LessonItemModel item : l) {
        	if ((row%2) != 0) {
        		sb.append("<div style='background-color: #FAFAFA;'>");
        	}
        	else {
        		sb.append("<div style='border-color: #FFFFFF #FDFDFD #FDFDFD;'>");
        	}
            sb.append("<div style='width: 20px; float: right;'>").append((item.isCompleted()?"OK":"&nbsp;")).append("</div>");
            sb.append("<div>").append(item.getName()).append("</div>").append("</div>");
            height += 15;
            row++;
        }
        sb.append("</div>");

        setSize(400, height + 60);

        html.setHeight(String.valueOf(height)+"px");
        html.setHTML(sb.toString());
    }
    
    protected void getStudentLessonTopicsRPC(final ListStore <LessonItemModel> store) {
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getLessonItemsForTestRun(runId, new AsyncCallback <List<LessonItemModel>>() {

        public void onSuccess(List<LessonItemModel> list) {
            store.add(list);
            formatLessonTopics(list);
            setVisible(true);
        }

        public void onFailure(Throwable caught) {
            String msg = caught.getMessage();
            CatchupMathTools.showAlert(msg);
        }
        });
    }    
}
