package hotmath.gwt.cm_test.client;

import hotmath.gwt.cm_rpc.client.rpc.GetCatchupMathDebugAction;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc.client.rpc.GetCatchupMathDebugAction.DebugAction;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmService;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanel2Callback;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

public class CatchupMathTest implements EntryPoint {
    
    
    TextArea _textArea;
    ShowWorkPanel2 _showWork;
    SimplePanel _mainPanel;
    
 public void onModuleLoad() {
     _mainPanel = new SimplePanel();
     
     DockPanel docPanel = new DockPanel();

     docPanel.add(_mainPanel, DockPanel.CENTER);
     
      HorizontalPanel toolBar = new HorizontalPanel();
      toolBar.add(new Button("Stop", new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
              _forceStop=true;
          }
      }));
      toolBar.add(new Button("Start", new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
              startTests();
          }
      }));

      docPanel.add(toolBar,DockPanel.NORTH);
      
      _textArea = new TextArea();
      _textArea.getElement().setAttribute("style", "margin-top: 100px;margin-bottom: 10px;");
      _textArea.setSize("500px",  "200px");

      docPanel.add(_textArea, DockPanel.SOUTH);
     RootPanel.get().add(docPanel);
     
     
     startTests();

    }
 
 private void logMessage(String msg) {
     _textArea.setValue(_textArea.getValue() + "\n" + msg);
 }
 
 boolean _forceStop;
 int _count;
 int MAX_TESTS=100;
 private void startTests() {
     _count=0;
     _forceStop=false;
     doTests();
 }
 
 
 private void doTests() {
     if(_forceStop) {
         logMessage("Force stop!!!");
         return;
     }
     
     if(_count++ > MAX_TESTS) {
         logMessage("Max tests reached: " + MAX_TESTS);
     }
     
     logMessage("Running test: " + _count);
     
     GetCatchupMathDebugAction action = new GetCatchupMathDebugAction(DebugAction.GET_NEXT);
     getCmService().execute(action,  new AsyncCallback<RpcData>() {
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
     final GetWhiteboardDataAction action = new GetWhiteboardDataAction(uid, pid, rid);
     getCmService().execute(action,new AsyncCallback<CmList<WhiteboardCommand>>() {
         @Override
        public void onSuccess(CmList<WhiteboardCommand> result) {
             logMessage("Read whiteboard for: " + action + ", " + result.size());
             
             showNewWhiteboard(result);
             //_showWork.loadWhiteboard(result);
             
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

protected void showNewWhiteboard(final CmList<WhiteboardCommand> result) {
     _showWork = new ShowWorkPanel2(new ShowWorkPanel2Callback() {
        @Override
        public void windowResized() {
        }
        
        @Override
        public void showWorkIsReady() {
            _showWork.loadWhiteboard(result);
            
            logMessage(getWhiteboardSize());
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
    return "Whiteboard size: " + res; 
}-*/;

static CmServiceAsync _serviceInstance;
 static public CmServiceAsync getCmService() {
     return _serviceInstance;
 }

 static private void setupServices() {
     final CmServiceAsync cmService = (CmServiceAsync)GWT.create(CmService.class);
     String url = "/cm_admin/services/cmService";
     ((ServiceDefTarget) cmService).setServiceEntryPoint(url);
     _serviceInstance = cmService;
 }
 
 static {
     setupServices();
 }

}
