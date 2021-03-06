package hotmath.gwt.cm_rpc_core.server.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.ActionType;

import java.util.HashMap;

public class ActionTypeMap {
	static HashMap<String,ActionType> actionTypeMap;
	
	/** TODO: We should use the actual class (*.class) 
	 * to allow for refactoring and referencing.
	 * 
	 * Objects not in list are counted as Other?
	 * 
	 * Perhaps, the right way to do this is to make
	 * it part of the Action interface.
	 * 
	 */
	static {
		actionTypeMap = new HashMap<String,ActionType>();
		
		actionTypeMap.put("CreateTestRunMobileAction",                  ActionType.STUDENT);
		actionTypeMap.put("GetCmMobileLoginAction",                     ActionType.STUDENT);
		actionTypeMap.put("GetMobileSolutionAction",                    ActionType.STUDENT);
		actionTypeMap.put("WhiteboardAction",                           ActionType.STUDENT);
		actionTypeMap.put("AutoAdvanceUserAction",                      ActionType.STUDENT);
		actionTypeMap.put("ClearWhiteboardDataAction",                  ActionType.STUDENT);
		actionTypeMap.put("CreateAutoRegistrationAccountAction",        ActionType.STUDENT);
		actionTypeMap.put("CreateTestRunAction",                        ActionType.STUDENT);
		actionTypeMap.put("GetCmProgramFlowAction",                     ActionType.STUDENT);
		actionTypeMap.put("GetLessonItemsForTestRunAction",             ActionType.STUDENT);
		actionTypeMap.put("GetPrescriptionAction",                      ActionType.STUDENT);
		actionTypeMap.put("GetQuizCurrentResultsAction",                ActionType.STUDENT);
		actionTypeMap.put("GetQuizHtmlCheckedAction",                   ActionType.STUDENT);
		actionTypeMap.put("GetQuizHtmlAction",                          ActionType.STUDENT);
		actionTypeMap.put("GetQuizHtmlSpritedAction",                   ActionType.STUDENT);
		actionTypeMap.put("GetQuizResultsHtmlAction",                   ActionType.STUDENT);
		actionTypeMap.put("GetReviewHtmlAction",                        ActionType.STUDENT);
		actionTypeMap.put("GetSolutionAction",                          ActionType.STUDENT);
		actionTypeMap.put("GetStudentModelAction",                      ActionType.STUDENT);
		actionTypeMap.put("GetStudentShowWorkAction",                   ActionType.STUDENT);
		actionTypeMap.put("GetViewedInmhItemsAction",                   ActionType.STUDENT);
		actionTypeMap.put("GetWhiteboardDataAction",                    ActionType.STUDENT);
		actionTypeMap.put("SetLessonCompletedAction",                   ActionType.STUDENT);
		actionTypeMap.put("ResetUserAction",                            ActionType.STUDENT);
		actionTypeMap.put("SaveAutoRegistrationAction",                 ActionType.STUDENT);
		actionTypeMap.put("SaveQuizCurrentResultAction",                ActionType.STUDENT);
		actionTypeMap.put("SaveWhiteboardDataAction",                   ActionType.STUDENT);
		actionTypeMap.put("SetBackgroundStyleAction",                   ActionType.STUDENT);
		actionTypeMap.put("SetInmhItemAsViewedAction",                  ActionType.STUDENT);

		actionTypeMap.put("AddGroupAction",                             ActionType.ADMIN);
		actionTypeMap.put("AddStudentAction",                           ActionType.ADMIN);
		actionTypeMap.put("AddUserAction",                              ActionType.ADMIN);
		actionTypeMap.put("CreateAutoRegistrationAccountsAction",       ActionType.ADMIN);
		actionTypeMap.put("CreateAutoRegistrationPreviewAction",        ActionType.ADMIN);
		actionTypeMap.put("CreateProgramAction",                        ActionType.ADMIN);
		actionTypeMap.put("CreateProgramDefinitionAction",              ActionType.ADMIN);
		actionTypeMap.put("CreateProgramInfoAction",                    ActionType.ADMIN);
		actionTypeMap.put("CustomProgramAction",                        ActionType.ADMIN);
		actionTypeMap.put("CustomProgramDefinitionAction",              ActionType.ADMIN);
		actionTypeMap.put("CustomProgramInfoAction",                    ActionType.ADMIN);
		actionTypeMap.put("CustomQuizInfoAction",                       ActionType.ADMIN);
		actionTypeMap.put("DeleteCustomQuizAction",                     ActionType.ADMIN);
		actionTypeMap.put("GeneratePdfAssessmentReportAction",          ActionType.ADMIN);
		actionTypeMap.put("GeneratePdfAction",                          ActionType.ADMIN);
		actionTypeMap.put("GeneratePdfHighlightsReportAction",          ActionType.ADMIN);
		actionTypeMap.put("GetAccountInfoForAdminUidAction",            ActionType.ADMIN);
		actionTypeMap.put("GetActiveGroupsAction",                      ActionType.ADMIN);
		actionTypeMap.put("GetAdminTrendingDataAction",                 ActionType.ADMIN);
		actionTypeMap.put("GetAdminTrendingDataDetailAction",           ActionType.ADMIN);
		actionTypeMap.put("GetChaptersForProgramSubjectAction",         ActionType.ADMIN);
		actionTypeMap.put("GetCustomQuizAction",                        ActionType.ADMIN);
		actionTypeMap.put("GetCustProgQuizDefsAction",                  ActionType.ADMIN);
		actionTypeMap.put("GetGroupAggregateInfoAction",                ActionType.ADMIN);
		actionTypeMap.put("GetProgramDefinitionsAction",                ActionType.ADMIN);
		actionTypeMap.put("GetProgramLessonsAction",                    ActionType.ADMIN);
		actionTypeMap.put("GetProgramListingAction",                    ActionType.ADMIN);
		actionTypeMap.put("GetReportDefAction",                         ActionType.ADMIN);
		actionTypeMap.put("GetSolutionAdminAction",                     ActionType.ADMIN);
		actionTypeMap.put("GetSolutionResourcesAdminAction",            ActionType.ADMIN);
		actionTypeMap.put("GetStateStandardsAction",                    ActionType.ADMIN);
		actionTypeMap.put("GetStudentActivityAction",                   ActionType.ADMIN);
		actionTypeMap.put("GetStudentGridPageAction",                   ActionType.ADMIN);
		actionTypeMap.put("GetStudentGridPageExtendedAction",           ActionType.ADMIN);
		actionTypeMap.put("GetSubjectDefinitionsAction",                ActionType.ADMIN);
		actionTypeMap.put("GetSummariesForActiveStudentsAction",        ActionType.ADMIN);
		actionTypeMap.put("GetTemplateForSelfRegGroupAction",           ActionType.ADMIN);
		actionTypeMap.put("GroupManagerAction",                         ActionType.ADMIN);
		actionTypeMap.put("GroupManagerAssignAction",                   ActionType.ADMIN);
		actionTypeMap.put("HighlightsGetReportAction",                  ActionType.ADMIN);
		actionTypeMap.put("SaveCustomQuizAction",                       ActionType.ADMIN);
		actionTypeMap.put("SaveSolutionAdminAction",                    ActionType.ADMIN);
		actionTypeMap.put("UnregisterStudentsAction",                   ActionType.ADMIN);
		actionTypeMap.put("UpdateStudentAction",                        ActionType.ADMIN);

		actionTypeMap.put("CheckForCentralMessagesAction",              ActionType.ANY);
		actionTypeMap.put("CheckUserAccountStatusAction",               ActionType.ANY);
		actionTypeMap.put("GetCatchupMathVersionAction",                ActionType.ANY);
		actionTypeMap.put("GetCmVersionInfoAction",                     ActionType.ANY);
		actionTypeMap.put("GetLessonItemsForCustomProgramTestAction",   ActionType.ANY);
		actionTypeMap.put("GetLessonQuestionsAction",                   ActionType.ANY);
		actionTypeMap.put("GetUserInfoAction",                          ActionType.ANY);
		actionTypeMap.put("LogRetryActionFailedAction",                 ActionType.ANY);
		actionTypeMap.put("LogUserInAction",                            ActionType.ANY);
		actionTypeMap.put("MultiActionRequestAction",                   ActionType.ANY);
		actionTypeMap.put("ProcessLoginRequestAction",                  ActionType.ANY);
		actionTypeMap.put("SaveCmLoggerTextAction",                     ActionType.ANY);
		actionTypeMap.put("SaveFeedbackAction",                         ActionType.ANY);

		actionTypeMap.put("RunNetTestAction",                           ActionType.TEST);
		actionTypeMap.put("SimulateCommunicationsExceptionTestAction",  ActionType.TEST);
		
	}
	
	static public ActionType getActionType(String className) {
		return (actionTypeMap.get(className) != null) ? actionTypeMap.get(className) : ActionType.OTHER;
	}
}
