package hotmath.gwt.cm_tools.client;



import hotmath.gwt.cm_tools.client.search.LessonSearchWindow;
import hotmath.gwt.cm_tools.client.teacher.AddFolderDialog;
import hotmath.gwt.cm_tools.client.ui.DateRangePickerDialog;
import hotmath.gwt.cm_tools.client.ui.GradeLevelChooser;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.SearchComboBoxPanel;
import hotmath.gwt.cm_tools.client.ui.SearchPanel;
import hotmath.gwt.cm_tools.client.ui.TopicExplorerManager;
import hotmath.gwt.cm_tools.client.ui.UserActivityLogDialog;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageWindow;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplActivity;
import hotmath.gwt.cm_tools.client.util.CreateTemplateFromClipboardImage;
import hotmath.gwt.cm_tools.client.util.WhiteboardTemplatesManager;
import hotmath.gwt.shared.client.CmShared;


/** Run test identified by test URL param 
 * 
 * @return
 */
public class CatchupMathSharedTests {

	/** Check for test identified by test param, if
	 * exist start test and return true.  Return
	 * false if test is not requested.
	 * 
	 * @return
	 */
    public static boolean runTest() {
        String test = CmShared.getQueryParameterValue("test");
        
        
        if(test.equals("GradeLevelChooser")) {
            GradeLevelChooser.startTest();
        }
        else if(test.equals("ResourceViewerImplActivity")) {
            ResourceViewerImplActivity.startTest();
        }
        else if(test.equals("SearchPanel")) {
            SearchPanel.startTest();
        }
        else if(test.equals("TopicExplorerManager")) {
            TopicExplorerManager.startTest();
        }
        else if(test.equals("SearchPanel")) {
            SearchComboBoxPanel.startTest();
        }
        else if(test.equals("ccss")) {
            CCSSCoverageWindow.startTest();
        }
        else if(test.equals("register")) {
            RegisterStudent.startTest();
        }
        else if(test.equals("UserActivityLogDialog")) {
            UserActivityLogDialog.startTest();
        }
        else if(test.equals("search")) {
            LessonSearchWindow.startTest();
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

        else if(test.equals("custom_problem_add_folder")) {
        	AddFolderDialog.doTest();
        }
        else {
        	return false;
        }
        
        return true;
    }

}
