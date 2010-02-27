package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.Timer;

public class NetTestWindow extends CmWindow {
    
    Grid<NetTestModel> _grid;
    ListStore<NetTestModel> store = new ListStore<NetTestModel>();
    Button _btnCheck;

    public NetTestWindow() {
        _grid = defineGrid(store,defineColumns());
        setLayout(new BorderLayout());
        
        setHeight(300);
        setHeading("Connection Check");
        setModal(true);
        setResizable(true);
        String html = "<p style='padding: 10px;'>Press Check to inform Catchup Math about your connection to the Internet.</p>";
        add(new Html(html), new BorderLayoutData(LayoutRegion.NORTH,40));
        add(_grid, new BorderLayoutData(LayoutRegion.CENTER));
     
        _btnCheck = new Button("Check",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                runTests();
            }
        });
        addButton(_btnCheck);
        addButton(new Button("Close",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));
        setVisible(true);
    }
    
    /** repeatailly call test each interval of mills
     * 
     * @param testEveryMills
     */
    public void repeatTestEvery(int testEveryMills) {
        new Timer() {
            @Override
            public void run() {
                runTests();
            }
        }.scheduleRepeating(testEveryMills);
    }
    
    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig group = new ColumnConfig();
        group.setId("name");
        group.setHeader("Test Name");
        group.setWidth(120);
        group.setSortable(true);
        configs.add(group);
        
        ColumnConfig usage = new ColumnConfig();
        usage.setId("size");
        usage.setHeader("Size");
        usage.setWidth(48);
        usage.setSortable(true);
        configs.add(usage);
        ColumnModel cm = new ColumnModel(configs);
        

        ColumnConfig time = new ColumnConfig();
        time.setId("time");
        time.setHeader("Time");
        time.setWidth(48);
        time.setSortable(true);
        configs.add(time);
                
        return cm;
    }
    
    
    /** run a series of every increasing data sized
     * tests and record the round trip time.
     * 
     */
    ProcessTracker pTrac;
    public void runTests() {
        int NUM_TESTS=40;
        String numNetTests = CmShared.getQueryParameter("net_test_count");
        if(numNetTests != null) {
            NUM_TESTS = Integer.parseInt(numNetTests);
        }
        double TEST_MULTIPLIER=4;
        pTrac = new TestProcessTracker(NUM_TESTS, new CmAsyncRequestImplDefault() {
            @Override
            public void requestComplete(String requestData) {
                sendResultsToServer();
            }
        });
        
        _grid.getStore().removeAll();
        for(int i=0;i<NUM_TESTS;i++) {
            runTest(i, Math.round((i+1)*TEST_MULTIPLIER));
        }
    }
    
    private void runTest(final int testNum, final long dataSize) {
        
        if(CmShared.getQueryParameter("debug")==null)
            _btnCheck.setEnabled(false);

        new RetryAction<NetTestModel>() {
            long timeStart;
            @Override
            public void attempt() {
                pTrac.beginStep();
                CmBusyManager.setBusy(true);
                timeStart=System.currentTimeMillis();
                RunNetTestAction action = new RunNetTestAction(TestAction.RUN_TEST, UserInfo.getInstance().getUid(), testNum, dataSize);
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
            }
        }.attempt();
    }
    
    /** send results to server to analysis
     * 
     */
    private void sendResultsToServer() {
        new RetryAction<NetTestModel>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                RunNetTestAction action = new RunNetTestAction(TestAction.SAVE_RESULTS, UserInfo.getInstance().getUid(), _grid.getStore().getModels());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            
            @Override
            public void oncapture(NetTestModel value) {
                CmBusyManager.setBusy(false);
                if(CmShared.getQueryParameter("debug")==null) {
                    CatchupMathTools.showAlert("Thank you", "Thank you.  The results have been saved on our server.",new CmAsyncRequestImplDefault() {
                        @Override
                        public void requestComplete(String requestData) {
                            //
                        }
                    });
                }
            }
        }.attempt();
    }
    
    private Grid<NetTestModel> defineGrid(final ListStore<NetTestModel> store, ColumnModel cm) {
        final Grid<NetTestModel> grid = new Grid<NetTestModel>(store, cm);
        grid.setBorders(true);
        grid.setStripeRows(true);
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
