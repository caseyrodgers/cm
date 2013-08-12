package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public interface ShowWorkView extends IPage {
    void setHeaderTitle(String title);
    void setPresenter(Presenter presenter);
    static public interface Presenter {
        void prepareShowWorkView(ShowWorkView view);
        String getProblemStatementHtml();
        void goBack();
    }
    void loadWhiteboard(CmList<WhiteboardCommand> commands);
}
