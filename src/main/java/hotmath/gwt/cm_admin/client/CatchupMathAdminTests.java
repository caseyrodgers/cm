package hotmath.gwt.cm_admin.client;

import hotmath.gwt.cm_admin.client.custom_content.problem.AddTeacherDialog;
import hotmath.gwt.cm_admin.client.custom_content.problem.CustomProblemManager;
import hotmath.gwt.cm_admin.client.custom_content.problem.CustomProblemTreeTable;
import hotmath.gwt.cm_admin.client.custom_content.problem.ProblemDesignerEditor;
import hotmath.gwt.cm_admin.client.custom_content.problem.ProblemDesignerEditorHintStep;
import hotmath.gwt.cm_admin.client.custom_content.problem.ProblemDesignerEditorWidget;
import hotmath.gwt.cm_admin.client.ui.AssignmentManagerDialog2;
import hotmath.gwt.cm_admin.client.ui.AssignmentStatusDialog;
import hotmath.gwt.cm_admin.client.ui.CustomProgramAddQuizDialog;
import hotmath.gwt.cm_admin.client.ui.CustomProgramDialog;
import hotmath.gwt.cm_admin.client.ui.ProgramDetailsPanel;
import hotmath.gwt.cm_admin.client.ui.WebLinkEditorDialog;
import hotmath.gwt.cm_admin.client.ui.WebLinksManager;
import hotmath.gwt.cm_admin.client.ui.assignment.AddProblemDialog;
import hotmath.gwt.cm_admin.client.ui.assignment.EditAssignmentDialog;
import hotmath.gwt.cm_admin.client.ui.assignment.FinalExamCreationManager;
import hotmath.gwt.cm_admin.client.ui.highlights.HighlightsDataWindow;
import hotmath.gwt.cm_admin.client.ui.list.ListCustomLesson;
import hotmath.gwt.cm_tools.client.search.LessonSearchWindow;
import hotmath.gwt.cm_tools.client.teacher.AddFolderDialog;
import hotmath.gwt.cm_tools.client.ui.DateRangePickerDialog;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.UserActivityLogDialog;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageWindow;
import hotmath.gwt.cm_tools.client.util.CreateTemplateFromClipboardImage;
import hotmath.gwt.cm_tools.client.util.WhiteboardTemplatesManager;
import hotmath.gwt.shared.client.CmShared;


/** Run test identified by test URL param 
 * 
 * @return
 */
public class CatchupMathAdminTests {

	/** Check for test identified by test param, if
	 * exist start test and return true.  Return
	 * false if test is not requested.
	 * 
	 * @return
	 */
    public static boolean runTest() {
        String test = CmShared.getQueryParameterValue("test");
        
        if(test.equals("CustomProgramDialog")) {
        	CustomProgramDialog.startTest();
        }
        else if(test.equals("ProgramDetailsPanel")) {
            ProgramDetailsPanel.startTest();
        }
        else if(test.equals("CustomProgramAddQuizDialog")) {
            CustomProgramAddQuizDialog.startTest();
        }
        else if(test.equals("ListCustomLesson")) {
            ListCustomLesson.startTest();
        }
        
        else if(test.equals("CollectEmailFromUserDialog")) {
        	CollectEmailFromUserDialog.startTest();
        }
        else if(test.equals("final")) {
            FinalExamCreationManager.startTest();
        }
        else if(test.equals("assignments")) {
            AssignmentManagerDialog2.startTest();
        }
        else if(test.equals("assignment_status")) {
            AssignmentStatusDialog.startTest();
        }
        else if(test.equals("assignment_edit")) {
            EditAssignmentDialog.startTest();
        }
        else if(test.equals("ccss")) {
            CCSSCoverageWindow.startTest();
        }
        else if(test.equals("highlights")) {
            HighlightsDataWindow.getSharedInstance(2).setVisible(true);
        }
        else if(test.equals("register")) {
            RegisterStudent.startTest();
        }
        else if(test.equals("activity")) {
            UserActivityLogDialog.startTest();
        }
        else if(test.equals("weblinks")) {
            WebLinksManager.startTest();
        }
        else if(test.equals("weblinkeditor")) {
            WebLinkEditorDialog.startTest();
        }        
        else if(test.equals("search")) {
            LessonSearchWindow.startTest();
        }
        else if(test.equals("custom_manager")) {
            CustomProblemManager.startTest();
        }
        else if(test.equals("assignment_add_problem")) {
            AddProblemDialog.startTest();
        }
        else if(test.equals("widget_editor")) {
            ProblemDesignerEditorWidget.doTest();
        }
        else if(test.equals("hint_editor")) {
            ProblemDesignerEditorHintStep.doTest();
        }
        else if(test.equals("whiteboard_templates")) {
            WhiteboardTemplatesManager.doTest();
        }
        else if(test.equals("date_chooser")) {
            DateRangePickerDialog.doTest();
        }
        else if(test.equals("clipboard_image")) {
            CreateTemplateFromClipboardImage.doTest();
        }
        else if(test.equals("custom_problem_designer")) {
            ProblemDesignerEditor.doTest();
        }
        else if(test.equals("tree_table")) {
        	CustomProblemTreeTable.doTest();
        }
        else if(test.equals("custom_problem_add_teacher")) {
        	AddTeacherDialog.doTest();
        }
        else if(test.equals("custom_problem_add_folder")) {
        	AddFolderDialog.doTest();
        }
        else {
        	return false;
        }
        
        return true;
    }

}
