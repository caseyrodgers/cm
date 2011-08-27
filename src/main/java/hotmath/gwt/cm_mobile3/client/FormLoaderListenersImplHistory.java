package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile3.client.data.SharedData;
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
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;

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
    public void setupListeners(EventBus eb) {

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
                String initial =  quizResult!=null?"initial":"";
                String token = "quiz:" + initial + ":" + System.currentTimeMillis();
                History.newItem(token);
            }
        });
        eb.addHandler(ShowWorkViewEvent.TYPE, new ShowWorkViewHandler() {
            @Override
            public void showWorkView(String pid) {
                History.newItem("show_work:" + pid + ":" + System.currentTimeMillis());
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
                        + resourceItem.getWidgetJsonArgs() + ":" + SharedData.findOrdinalPositionOfResource(resourceItem) + ":"
                        + resourceItem.getTitle() + ":" + System.currentTimeMillis());
            }
        });

    }

}
