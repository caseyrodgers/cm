package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanelCallbackDefault;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;


/** Generic Show Work Panel
 * 
 * @author casey
 *
 */
public class ShowWhiteboardWindow extends GWindow {

    
    private static ShowWhiteboardWindow __instance;
    public static ShowWhiteboardWindow getInstance() {
        if(__instance == null) {
            __instance = new ShowWhiteboardWindow();
        }
        return __instance;
    }
    
    BorderLayoutContainer _mainBorderPanel = new BorderLayoutContainer();

    public ShowWhiteboardWindow() {
        super(true);

        setPixelSize(700, 400);
        setResizable(true);
        setMaximizable(true);

        String title = "Whiteboard";
        setHeadingText(title);

        FramedPanel fp = new FramedPanel();
        fp.setHeaderVisible(false);
        fp.setWidget(createCenterPanel());
        setWidget(fp);
    }

    ShowWorkPanel2 _showWorkPanel;
    SolutionInfo _solutionInfo;

    /**
     * Create the center, main container with the show work and solution loaded
     * for pid.
     * 
     * @param pid
     * @return
     */
    private Widget createCenterPanel() {
        // create temp user object to identify this student

        _showWorkPanel = new ShowWorkPanel2(new ShowWorkPanelCallbackDefault() {
            @Override
            public void showWorkIsReady(ShowWorkPanel2 showWork) {
                CmList<WhiteboardCommand> whiteboardCommands = new CmArrayList<WhiteboardCommand>();
                _showWorkPanel.loadWhiteboard(whiteboardCommands);
            }

            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType,
                    String data) {
                return null;
            }

            @Override
            public void windowResized() {
                forceLayout();
            }
        });

        return _showWorkPanel;
    }
}
