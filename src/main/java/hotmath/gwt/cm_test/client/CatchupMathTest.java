package hotmath.gwt.cm_test.client;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc.client.rpc.GetCatchupMathDebugAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCatchupMathDebugAction.DebugAction;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmService;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanel2Callback;
import br.com.freller.tool.client.Print;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

public class CatchupMathTest implements EntryPoint {

    TextArea _textArea;
    TextBox _gotoInfo;
    ShowWorkPanel2 _showWork;
    SimplePanel _mainPanel;

    public void onModuleLoad() {
        _mainPanel = new SimplePanel();

        DockPanel docPanel = new DockPanel();

        HorizontalPanel toolBar = new HorizontalPanel();
        toolBar.add(new Button("Stop", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                _forceStop = true;
            }
        }));
        toolBar.add(new Button("Start", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                startTests();
            }
        }));
        toolBar.add(new Button("Print", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                printPage();
            }
        }));
        
        _gotoInfo = new TextBox();
        _gotoInfo.setWidth("100%");
        _gotoInfo.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == 13) {
                    loadGotoInfo(_gotoInfo.getValue());
                }
            }
        });

        _mainPanel.setPixelSize(600, 480);
        docPanel.add(_mainPanel, DockPanel.SOUTH);

        docPanel.add(toolBar, DockPanel.NORTH);

        docPanel.add(_gotoInfo, DockPanel.NORTH);

        _textArea = new TextArea();
        // _textArea.getElement().setAttribute("style",
        // "margin-top: 100px;margin-bottom: 10px;");
        _textArea.setSize("500px", "200px");

        docPanel.add(_textArea, DockPanel.SOUTH);
        RootPanel.get().add(docPanel);

        String gotoWhiteboard = CmGwtUtils.getQueryParameter("goto");
        if (gotoWhiteboard != null) {
            String p[] = gotoWhiteboard.split(",");
            _forceStop = true;
            readWhiteboardFromServer(Integer.parseInt(p[0]), Integer.parseInt(p[1]), p[2]);
        }

        // startTests();

    }

    native protected void printPage() /*-{
        $wnd.print();
    }-*/;

    /**
     * parse: [pid=quiz:quiz, runId=0, uid=31181]
     * 
     * @param value
     */
    protected void loadGotoInfo(String value) {

        _forceStop = true;

        String p[] = value.split(",");
        String pid = p[0].split("=")[1];
        String rid = p[1].split("=")[1];
        String uid = p[2].split("=")[1].split("]")[0];

        readWhiteboardFromServer(Integer.parseInt(uid), Integer.parseInt(rid), pid);
    }

    private void logMessage(String msg) {
        _textArea.setValue(_textArea.getValue() + "\n" + msg);
    }

    boolean _forceStop;
    int _count;
    int MAX_TESTS = 100;

    private void startTests() {
        _count = 0;
        _forceStop = false;
        doTests();
    }

    private void doTests() {
        if (_forceStop) {
            logMessage("Force stop!!!");
            return;
        }

        if (_count++ > MAX_TESTS) {
            logMessage("Max tests reached: " + MAX_TESTS);
        }

        logMessage("Running test: " + _count);

        GetCatchupMathDebugAction action = new GetCatchupMathDebugAction(DebugAction.GET_NEXT);
        getCmService().execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                readWhiteboardFromServer(result.getDataAsInt("uid"), result.getDataAsInt("rid"), result.getDataAsString("pid"));
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error: " + caught);
                Log.error("Error", caught);
            }
        });
    }

    protected void readWhiteboardFromServer(int uid, int rid, String pid) {

        final String message = "[pid=" + pid + ", " + "rid=" + rid + ", uid=" + uid + "]";
        _gotoInfo.setValue(message);

        final GetWhiteboardDataAction action = new GetWhiteboardDataAction(uid, pid, rid);
        getCmService().execute(action, new AsyncCallback<CmList<WhiteboardCommand>>() {
            @Override
            public void onSuccess(CmList<WhiteboardCommand> result) {
                showNewWhiteboard(result, message);
                // _showWork.loadWhiteboard(result);

                // now wait ... and do the next one
                new Timer() {

                    @Override
                    public void run() {
                        doTests();
                    }
                }.schedule(5000);

            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error", caught);
            }
        });
    }

    protected void showNewWhiteboard(final CmList<WhiteboardCommand> result, final String message) {
        _showWork = new ShowWorkPanel2(new ShowWorkPanel2Callback() {
            @Override
            public void windowResized() {
            }

            @Override
            public void showWorkIsReady() {
                _showWork.loadWhiteboard(result);

                logMessage(getWhiteboardSize() + "\t" + message);
            }

            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return null;
            }
        });
        _mainPanel.setWidget(_showWork);
    }

    native protected String getWhiteboardSize() /*-{
                                                var res = $wnd._theWhiteboard.getSizeOfWhiteboard();
                                                return "" + res; 
                                                }-*/;

    static CmServiceAsync _serviceInstance;

    static public CmServiceAsync getCmService() {
        return _serviceInstance;
    }

    static private void setupServices() {
        final CmServiceAsync cmService = (CmServiceAsync) GWT.create(CmService.class);
        String url = "/cm_admin/services/cmService";
        ((ServiceDefTarget) cmService).setServiceEntryPoint(url);
        _serviceInstance = cmService;
    }

    static {
        setupServices();
    }

}
