package hotmath.cm;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm_mobile.server.rpc.GetCmMobileLoginCommand;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.server.service.SetInmhItemAsViewedCommand;
import hotmath.gwt.shared.server.service.command.AddGroupCommand;
import hotmath.gwt.shared.server.service.command.AddStudentCommand;
import hotmath.gwt.shared.server.service.command.AutoAdvanceUserCommand;
import hotmath.gwt.shared.server.service.command.CheckForCentralMessagesCommand;
import hotmath.gwt.shared.server.service.command.CheckUserAccountStatusCommand;
import hotmath.gwt.shared.server.service.command.ClearWhiteboardDataCommand;
import hotmath.gwt.shared.server.service.command.CreateAutoRegistrationAccountCommand;
import hotmath.gwt.shared.server.service.command.CreateAutoRegistrationAccountsCommand;
import hotmath.gwt.shared.server.service.command.CreateAutoRegistrationPreviewCommand;
import hotmath.gwt.shared.server.service.command.CreateTestRunCommand;
import hotmath.gwt.shared.server.service.command.CustomProgramCommand;
import hotmath.gwt.shared.server.service.command.CustomProgramDefinitionCommand;
import hotmath.gwt.shared.server.service.command.CustomProgramInfoCommand;
import hotmath.gwt.shared.server.service.command.GeneratePdfAssessmentReportCommand;
import hotmath.gwt.shared.server.service.command.GeneratePdfCommand;
import hotmath.gwt.shared.server.service.command.GetAccountInfoForAdminUidCommand;
import hotmath.gwt.shared.server.service.command.GetActiveGroupsCommand;
import hotmath.gwt.shared.server.service.command.GetAdminTrendingDataCommand;
import hotmath.gwt.shared.server.service.command.GetAdminTrendingDataDetailCommand;
import hotmath.gwt.shared.server.service.command.GetCatchupMathVersionCommand;
import hotmath.gwt.shared.server.service.command.GetChaptersForProgramSubjectCommand;
import hotmath.gwt.shared.server.service.command.GetCmVersionInfoCommand;
import hotmath.gwt.shared.server.service.command.GetGroupAggregateInfoCommand;
import hotmath.gwt.shared.server.service.command.GetLessonItemsForTestRunCommand;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.gwt.shared.server.service.command.GetProgramDefinitionsCommand;
import hotmath.gwt.shared.server.service.command.GetQuizCurrentResultsCommand;
import hotmath.gwt.shared.server.service.command.GetQuizHtmlCheckedCommand;
import hotmath.gwt.shared.server.service.command.GetQuizHtmlCommand;
import hotmath.gwt.shared.server.service.command.GetQuizResultsHtmlCommand;
import hotmath.gwt.shared.server.service.command.GetReportDefCommand;
import hotmath.gwt.shared.server.service.command.GetReviewHtmlCommand;
import hotmath.gwt.shared.server.service.command.GetSolutionCommand;
import hotmath.gwt.shared.server.service.command.GetStateStandardsCommand;
import hotmath.gwt.shared.server.service.command.GetStudentActivityCommand;
import hotmath.gwt.shared.server.service.command.GetStudentGridPageCommand;
import hotmath.gwt.shared.server.service.command.GetStudentModelCommand;
import hotmath.gwt.shared.server.service.command.GetStudentShowWorkCommand;
import hotmath.gwt.shared.server.service.command.GetSubjectDefinitionsCommand;
import hotmath.gwt.shared.server.service.command.GetSummariesForActiveStudentsCommand;
import hotmath.gwt.shared.server.service.command.GetTemplateForSelfRegGroupCommand;
import hotmath.gwt.shared.server.service.command.GetUserInfoCommand;
import hotmath.gwt.shared.server.service.command.GetViewedInmhItemsCommand;
import hotmath.gwt.shared.server.service.command.GetWhiteboardDataCommand;
import hotmath.gwt.shared.server.service.command.GroupManagerCommand;
import hotmath.gwt.shared.server.service.command.LogRetryActionFailedCommand;
import hotmath.gwt.shared.server.service.command.LogUserInCommand;
import hotmath.gwt.shared.server.service.command.MarkPrescriptionLessonAsViewedCommand;
import hotmath.gwt.shared.server.service.command.ProcessLoginRequestCommand;
import hotmath.gwt.shared.server.service.command.ResetUserCommand;
import hotmath.gwt.shared.server.service.command.RunNetTestCommand;
import hotmath.gwt.shared.server.service.command.SaveAutoRegistrationCommand;
import hotmath.gwt.shared.server.service.command.SaveCmLoggerTextCommand;
import hotmath.gwt.shared.server.service.command.SaveFeedbackCommand;
import hotmath.gwt.shared.server.service.command.SaveQuizCurrentResultCommand;
import hotmath.gwt.shared.server.service.command.SaveWhiteboardDataCommand;
import hotmath.gwt.shared.server.service.command.SetBackgroundStyleCommand;
import hotmath.gwt.shared.server.service.command.UnregisterStudentsCommand;
import hotmath.gwt.shared.server.service.command.UpdateStudentCommand;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/** Provides hooks to initialize the Catchup Math 
 *  system when pluggined into a servlet environment.
 *  
 * @author casey
 *
 */
public class CmInitialzation extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -771897979715468574L;

    public void init() {
        String prefix = getServletContext().getRealPath("/");
        String file = getInitParameter("log4j-init-file");
        // if the log4j-init-file is not set, then no point in trying
        if (file != null) {
            PropertyConfigurator.configure(prefix + file);
            Logger.getLogger(this.getClass()) .info("Catchup Math Log4J intialized");
        }
        
        CmWebResourceManager.setFileBase(getServletContext().getRealPath("cm_temp"));

        ActionDispatcher.getInstance().registerCommands(commands);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        /** silent */
    }
    
    
    
    @SuppressWarnings("unchecked")
    Class[] commands = 
    {
            GetPrescriptionCommand.class,
            GetViewedInmhItemsCommand.class,
            GetSolutionCommand.class,
            SetInmhItemAsViewedCommand.class,
            GetUserInfoCommand.class,
            CreateTestRunCommand.class,
            GetQuizHtmlCommand.class,
            GetQuizHtmlCheckedCommand.class,
            SaveQuizCurrentResultCommand.class,
            GetQuizResultsHtmlCommand.class,
            SaveFeedbackCommand.class,
            AutoAdvanceUserCommand.class,
            GetProgramDefinitionsCommand.class,
            AddStudentCommand.class,
            UpdateStudentCommand.class,
            SaveWhiteboardDataCommand.class,
            ClearWhiteboardDataCommand.class,
            CreateAutoRegistrationPreviewCommand.class,
            CreateAutoRegistrationAccountsCommand.class,
            AddGroupCommand.class,
            SaveAutoRegistrationCommand.class,
            GetReportDefCommand.class,
            CreateAutoRegistrationAccountCommand.class,
            GetStateStandardsCommand.class,
            GetLessonItemsForTestRunCommand.class,
            UnregisterStudentsCommand.class,
            ProcessLoginRequestCommand.class,
            GetSummariesForActiveStudentsCommand.class,
            GetReviewHtmlCommand.class,
            MarkPrescriptionLessonAsViewedCommand.class,
            CheckUserAccountStatusCommand.class,
            LogUserInCommand.class,
            GetGroupAggregateInfoCommand.class,
            GroupManagerCommand.class,
            GeneratePdfCommand.class,
            SetBackgroundStyleCommand.class,
            GetCmVersionInfoCommand.class,
            GetWhiteboardDataCommand.class,
            GetStudentShowWorkCommand.class,
            GetStudentActivityCommand.class,
            GetAccountInfoForAdminUidCommand.class,
            GetActiveGroupsCommand.class,
            GetQuizCurrentResultsCommand.class,
            GetStudentModelCommand.class,
            ResetUserCommand.class,
            GetSubjectDefinitionsCommand.class,
            GetChaptersForProgramSubjectCommand.class,
            GetStudentGridPageCommand.class,
            GetTemplateForSelfRegGroupCommand.class,
            GetAdminTrendingDataCommand.class,
            GeneratePdfAssessmentReportCommand.class,
            GetAdminTrendingDataDetailCommand.class,
            RunNetTestCommand.class,
            LogRetryActionFailedCommand.class,
            CheckForCentralMessagesCommand.class,
            GetCatchupMathVersionCommand.class,
            SaveCmLoggerTextCommand.class,
            CustomProgramCommand.class,
            CustomProgramDefinitionCommand.class,
            CustomProgramInfoCommand.class,
            GetCmMobileLoginCommand.class,
            };
}
