package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.HasWhiteboard;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;

public interface QuizView extends IPage, HasWhiteboard {
    void setPresenter(Presenter presenter);
    void setQuizHtml(String quizHtml, int questionCount);
    void clearQuizHtml();
    static public interface Presenter {
        void prepareQuizView(QuizView quizView);
        void checkQuiz();
        void loadWhiteboard(ShowWorkPanel2 _showWork, String pid);
        Action<? extends Response> getWhiteboardSaveAction(String pid, CommandType commandType, String data);        
    }
    void showWhiteboard(String pid);
}
