package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestApplication;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class NetTestWindow extends GWindow {
    
    
    public interface NetTestProperties extends PropertyAccess<String> {
        @Path("name")
        ModelKeyProvider<NetTestModel> id();
        ValueProvider<NetTestModel, String> name();
        ValueProvider<NetTestModel, Long> size();
        ValueProvider<NetTestModel, Long> time();
    }
    
    
    private static final NetTestProperties props = GWT.create(NetTestProperties.class);
    
    Grid<NetTestModel> _grid;
    TextButton _btnCheck;
    TestApplication testApplication;
    Integer uid;

    public NetTestWindow(TestApplication testApp, Integer uid) {
        super(true);
        setHeadingText("Connection Check");
        setHeight(300);
        setModal(true);
        setResizable(true);
        
        
        this.testApplication = testApp;
        this.uid = uid;
        
        ListStore<NetTestModel> store = new ListStore<NetTestModel>(props.id());
        _grid = defineGrid(store,defineColumns());
        
        BorderLayoutContainer borderLayout = new BorderLayoutContainer();
        
        String html = "<p style='padding: 10px;'>Press Check to inform Catchup Math about your connection to the Internet.</p>";
        borderLayout.setNorthWidget(new HTML(html), new BorderLayoutData(40));
        borderLayout.setCenterWidget(_grid);
     
        if(CmCore.isDebug() == true) {
            addButton(new TextButton("Stop Tests", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    stopTimer();
                }
            }));
        }
        _btnCheck = new TextButton("Check",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                runTests();
            }
        });
        addButton(_btnCheck);
        addButton(new TextButton("Close",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                stopTimer();
                close();
            }
        }));
        
        setWidget(borderLayout);
        setVisible(true);
    }
    
    Timer _timer;

    /** repeat call for test on each interval of mills
     * 
     * @param testEveryMills
     */
    public void repeatTestEvery(int testEveryMills) {
        _timer = new Timer() {
            @Override
            public void run() {
                runTests();
            }
        };
        _timer.scheduleRepeating(testEveryMills);
    }
    
    private void stopTimer() {
        if(_timer != null) {
            _timer.cancel();
            _timer = null;
        }        
    }
    
    private ColumnModel<ColumnConfig<NetTestModel,?>> defineColumns() {
        ArrayList<ColumnConfig<NetTestModel, ?>> configs = new ArrayList<ColumnConfig<NetTestModel,?>>();
        configs.add(new ColumnConfig<NetTestModel, String>(props.name(), 120, "Test Name"));
        configs.add(new ColumnConfig<NetTestModel, Long>(props.size(), 48, "Size"));
        configs.add(new ColumnConfig<NetTestModel, Long>(props.time(), 48, "Time"));
        return new ColumnModel(configs);
    }
    
    
    /** run a series of every increasing data sized
     * tests and record the round trip time.
     * 
     */
    ProcessTracker pTrac;
    int _numTestsToRun;
    final double TEST_MULTIPLIER=10;
    public void runTests() {
        
        String numNetTests = CmCore.getQueryParameter("net_test_count");
        if(numNetTests != null) {
            _numTestsToRun = Integer.parseInt(numNetTests);
        }
        else {
            _numTestsToRun = 5;
        }
        
        pTrac = new TestProcessTracker(_numTestsToRun, new CmAsyncRequestImplDefault() {
            @Override
            public void requestComplete(String requestData) {
                sendResultsToServer();
            }
        });
        
        _grid.getStore().clear();
        
        runTest(0, Math.round(TEST_MULTIPLIER));
    }
    
    
    private void runTest(final int testNum, final long dataSize) {
        
        if(CmCore.isDebug() == false)
            _btnCheck.setEnabled(false);

        new RetryAction<NetTestModel>() {
            long timeStart;
            @Override
            public void attempt() {
                pTrac.beginStep();
                CmBusyManager.setBusy(true);
                timeStart=System.currentTimeMillis();
                
                RunNetTestAction action = new RunNetTestAction(testApplication, TestAction.RUN_TEST, NetTestWindow.this.uid, testNum, dataSize);
                setAction(action);
                
                CmShared.getCmService().execute(action, this);
            }
            
            @Override
            public void oncapture(NetTestModel testResults) {
                CmBusyManager.setBusy(false);
                long timeEnd=System.currentTimeMillis();
                testResults.setNumber(testNum);
                testResults.setTime(timeEnd - timeStart);
                testResults.setSize(dataSize);
                _grid.getStore().add(testResults);
                pTrac.completeStep();
                
                /** run next test on completion */
                if((testNum+1) < _numTestsToRun)
                    runTest(testNum+1, Math.round(TEST_MULTIPLIER * (testNum+2)));
            }
            
            public void onFailure(Throwable error) {
                stopTimer();
                super.onFailure(error);
            }
        }.register();
    }
    
    /** send results to server to analysis
     * 
     */
    private void sendResultsToServer() {
        new RetryAction<NetTestModel>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                CmList<NetTestModel> list = new CmArrayList<NetTestModel>();
                list.addAll(_grid.getStore().getAll());
                RunNetTestAction action = new RunNetTestAction(testApplication, TestAction.SAVE_RESULTS, NetTestWindow.this.uid, list);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            
            @Override
            public void oncapture(NetTestModel value) {
                CmBusyManager.setBusy(false);
                if(CmCore.isDebug() == false) {
                    CatchupMathTools.showAlert("Thank you", "Thank you.  The results have been saved on our server.");
                }
            }
        }.register();
    }
    
    private Grid<NetTestModel> defineGrid(final ListStore<NetTestModel> store, ColumnModel cm) {
        final Grid<NetTestModel> grid = new Grid<NetTestModel>(store, cm);
        grid.setBorders(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        grid.setWidth("200px");
        grid.setHeight("250px");
        return grid;
    }
}



/** track tests, when last test is complete callback listener
 * 
 * @author casey
 *
 */
class TestProcessTracker implements ProcessTracker {
    
    int testCount;
    int runTests;
    CmAsyncRequest callBack;
    public TestProcessTracker(int count,CmAsyncRequest callBack) {
        this.testCount = count;
        this.callBack = callBack;
    }
    
    @Override
    public void finish() {
    }
    
    @Override
    public void completeStep() {
        runTests++;
        
        if(runTests == testCount) {
            callBack.requestComplete("OK");
        }
    }
    
    @Override
    public void beginStep() {
        
    }
};
