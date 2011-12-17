package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile3.client.event.HandleNextFlowEvent;
import hotmath.gwt.cm_mobile3.client.event.LoadActiveProgramFlowEvent;
import hotmath.gwt.cm_mobile3.client.event.LoadActiveProgramFlowEventHandler;
import hotmath.gwt.cm_mobile3.client.event.MoveToNextSegmentEvent;
import hotmath.gwt.cm_mobile3.client.event.MoveToNextSegmentEventHandler;
import hotmath.gwt.cm_mobile3.client.event.ShowEndOfProgramEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowEndOfProgramEventHandler;
import hotmath.gwt.cm_mobile3.client.event.ShowLoginViewEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowLoginViewHandler;
import hotmath.gwt.cm_mobile3.client.event.ShowPrescriptionLessonViewEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowPrescriptionLessonViewHandler;
import hotmath.gwt.cm_mobile3.client.event.ShowPrescriptionResourceEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowPrescriptionResourceHandler;
import hotmath.gwt.cm_mobile3.client.event.ShowQuizViewEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowQuizViewHandler;
import hotmath.gwt.cm_mobile3.client.event.ShowWelcomeViewEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowWelcomeViewHandler;
import hotmath.gwt.cm_mobile3.client.event.ShowWorkViewEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowWorkViewHandler;
import hotmath.gwt.cm_mobile3.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.ShowFlashRequiredEvent;
import hotmath.gwt.cm_mobile_shared.client.event.ShowFlashRequiredEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * handle the form routing by using the history tags
 * 
 * events to handle mapping to GWT history listener
 * 
 * Define listeners that will load the appropriate view into the history stack.
 * 
 * @author casey
 */
public class FormLoaderListenersImplHistory implements FormLoaderListeners {

    @Override
    public void setupListeners(final EventBus eb) {

        eb.addHandler(ShowWelcomeViewEvent.TYPE, new ShowWelcomeViewHandler() {
            @Override
            public void showWelcomeView() {
                History.newItem("welcome:" + System.currentTimeMillis());
            }
        });
        eb.addHandler(ShowLoginViewEvent.TYPE, new ShowLoginViewHandler() {
            @Override
            public void showLoginView() {
                History.newItem("login:" + System.currentTimeMillis());
            }
        });
        eb.addHandler(ShowQuizViewEvent.TYPE, new ShowQuizViewHandler() {
            @Override
            public void showQuizView(QuizHtmlResult quizResult) {
                String initial = quizResult != null ? "initial" : "";
                String token = "quiz:" + initial + ":" + System.currentTimeMillis();
                History.newItem(token);
            }
        });
        eb.addHandler(ShowWorkViewEvent.TYPE, new ShowWorkViewHandler() {
            @Override
            public void showWorkView(String pid, String title) {
                if (pid == null) {
                    pid = "";
                }
                History.newItem("show_work:" + pid + ":" + title + ":" + System.currentTimeMillis());
            }
        });
        eb.addHandler(ShowPrescriptionLessonViewEvent.TYPE, new ShowPrescriptionLessonViewHandler() {
            @Override
            public void showPrescriptionLesson() {
                History.newItem("lesson:" + System.currentTimeMillis());
            }
        });
        eb.addHandler(ShowPrescriptionResourceEvent.TYPE, new ShowPrescriptionResourceHandler() {
            @Override
            public void showResource(InmhItemData resourceItem) {
                History.newItem("resource:" + resourceItem.getType() + ":" + resourceItem.getFile() + ":"
                        + "" + ":"
                        + SharedData.findOrdinalPositionOfResource(resourceItem) + ":" + resourceItem.getTitle() + ":"
                        + System.currentTimeMillis());
            }
        });
        
        eb.addHandler(ShowEndOfProgramEvent.TYPE, new ShowEndOfProgramEventHandler() {
            @Override
            public void showView() {
                History.newItem("end_of_program");
            }
        });
        
        eb.addHandler(ShowFlashRequiredEvent.TYPE,new ShowFlashRequiredEventHandler() {
           @Override
            public void showFlashRequiredDialog() {
               MessageBox.showError("The prescription assigned to the current quiz requires Flash and cannot be viewed on the IPad.");
            } 
        });
        
        eb.addHandler(MoveToNextSegmentEvent.TYPE, new MoveToNextSegmentEventHandler() {
            @Override
            public void loadNextSegment() {
                eb.fireEvent(new SystemIsBusyEvent(true));
                GetCmProgramFlowAction action = new GetCmProgramFlowAction(SharedData.getUserInfo().getUid(),
                        FlowType.NEXT);
                CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmProgramFlowAction>() {
                    @Override
                    public void onSuccess(CmProgramFlowAction result) {
                        eb.fireEvent(new SystemIsBusyEvent(false));
                        SharedData.setFlowAction(result);

                        eb.fireEvent(new HandleNextFlowEvent(result));
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        eb.fireEvent(new SystemIsBusyEvent(false));
                        Log.error("Error moving to next segment", caught);
                    }
                });
            }
        });

        eb.addHandler(LoadActiveProgramFlowEvent.TYPE, new LoadActiveProgramFlowEventHandler() {
            @Override
            public void loadActiveProgramFlow() {

                eb.fireEvent(new SystemIsBusyEvent(true));
                CatchupMathMobileShared.getCmService().execute(
                        new GetCmMobileLoginAction(SharedData.getUserInfo().getUid()),
                        new AsyncCallback<CmMobileUser>() {
                            public void onSuccess(CmMobileUser result) {
                                eb.fireEvent(new SystemIsBusyEvent(false));
                                SharedData.setFlowAction(result.getFlowAction());
                                eb.fireEvent(new HandleNextFlowEvent(result.getFlowAction()));
                            }

                            @Override
                            public void onFailure(Throwable caught) {
                                eb.fireEvent(new SystemIsBusyEvent(false));
                                MessageBox.showError("Error loading active program flow: " + caught.getMessage());
                                Log.error("Error getting user information: " + caught.getMessage(), caught);
                            }
                        });
            }
        });

    }

}
