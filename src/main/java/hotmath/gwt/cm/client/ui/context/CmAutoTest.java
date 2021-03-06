package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_tools.client.ui.AutoTestWindow;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;

public class CmAutoTest {

    MyQueue<ResourceObject> resourcesToRun;
    boolean finished;
    CmAsyncRequest callback;
    
    public CmAutoTest(List<ResourceObject> resourcesToRun, CmAsyncRequest callback) {
        this.resourcesToRun = new MyQueue<ResourceObject>(resourcesToRun);
        this.callback = callback;        
        new TestTimer(1);
    }
    
    class TestTimer extends Timer {
        
        Integer runNext;
        
        public TestTimer(int runNext) {
            this.runNext = runNext;
            schedule(runNext>0?runNext:1);
        }
        
        @Override
        public void run() {
            
            loadNextResource();
    
            if(!finished && UserInfo.getInstance().isAutoTestMode()) {
                new TestTimer(AutoTestWindow.getInstance().getTimeForSingleResource());
            }
            else {
                AutoTestWindow.getInstance().addLogMessage("Resource complete");
                
                callback.requestComplete("ok");
            }
        }
        
        private void loadNextResource() {
            final ResourceObject resource = resourcesToRun.getLocation();
            if(resource == null)
                finished = true;
            else {
                AutoTestWindow.getInstance().addLogMessage("Loading (" + resourcesToRun.size() + "): " + resource + ", "  + resource.which);
                CmHistoryManager.loadResourceIntoHistory(resource.item.getType().label(),Integer.toString(resource.which));
                
                if(resource.item.getType() == CmResourceType.PRACTICE) {
                    
                    PrescriptionCmGuiDefinition.markResourceAsViewed(resource.item, new CallbackOnComplete() {
                        @Override
                        public void isComplete() {
                            
                            EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REQUIRED_COMPLETE, resource.item));
                            // PrescriptionResourcePanel.setLessonCompleted(resource.item.getFile());
                        }
                    });
                    
                    
                    PrescriptionResourcePanel.__instance.updateCheckMarks(CmResourceType.PRACTICE);
                }
            }
        }    
    }    
    
    static public class ResourceObject {
        private InmhItemData item;
        private Integer which;
        
        public ResourceObject(InmhItemData i, Integer w) {
            this.item = i;
            this.which = w;
        }
        
        @Override
        public String toString() {
            return item.toString() + ", " + which;
        }
    }
}



/** Simple queue that returns and returns oldest element
 * 
 * (became frustrated with java.util.Queue)
 * 
 */
class MyQueue<ResourceObject> extends ArrayList<ResourceObject> {
    
    public MyQueue(List<ResourceObject> resources) {
        addAll(resources);
    }
    
    public ResourceObject getLocation() {
        ResourceObject cm=null;
        if(size() > 0) {
            
            /** get a random item, return and remove it
             * 
             */
            int randIndex = (int)(Math.random() * size());
            cm = get(randIndex);
            remove(randIndex);
        }
        return cm;
    }
}