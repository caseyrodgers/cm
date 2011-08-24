package hotmath.gwt.cm_mobile3.client.event;

import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;

import com.google.gwt.event.shared.EventHandler;

public interface ShowQuizViewHandler extends EventHandler {
    void showQuizView(QuizHtmlResult quizResult);
}
