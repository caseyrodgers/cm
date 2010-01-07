package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;

/**
 * Timer used to control the refresh of server data
 * 
 * Refreshes data every REFRESH_MILLS
 * 
 * Implemented as singleton to allow exposing the fireRefreshData
 * 
 * There is a single timer that fires every REFRESH_MILLS. You can call
 * fireRefreshData directly and the next auto fire will be skipped.
 * 
 */
public class CmAdminDataReader extends Timer {
    static final private int REFRESH_MILLS = (1000 * 30);
    
    
    private static CmAdminDataReader __instance;
    static public CmAdminDataReader getInstance() {
        if (__instance == null) {
            __instance = new CmAdminDataReader();
        }
        return __instance;
    }
    
    List<CmAdminDataRefresher> dataReaders = new ArrayList<CmAdminDataRefresher>();
    boolean isRefreshing;
    long lastRefresh;
    boolean skipRefresh;

    /** Create a data refresher with set dataReaders.
     * 
     * Each time data is needed, each dataReader is asked to 
     * update itself.
     * 
     * @param dataReaders
     */
    private CmAdminDataReader() {
        // scheduleRepeating(REFRESH_MILLS);
        
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_MODAL_WINDOW_OPEN) {
                    skipRefresh=true;
                }
                else if(event.getEventType() == EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED) {
                    skipRefresh=false;
                }
                else if(event.getEventType() == EventType.EVENT_TYPE_USERCHANGED) {
                    fireRefreshData();
                }
                else if(event.getEventType() == EventType.EVENT_TYPE_USER_PROGRAM_CHANGED) {
                    fireRefreshData();
                }
            }
        });
    }
    
    public void addReader(CmAdminDataRefresher dataReader) {
        this.dataReaders.add(dataReader);
    }
    
    public void removeReader(CmAdminDataRefresher dataReader) {
    	this.dataReaders.remove(dataReader);
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis() - lastRefresh;
        boolean needsRefreshing = now > REFRESH_MILLS;
        if(skipRefresh) {
            Log.debug("CmAdminDataReader: skipped, by request");
        }
        else if(isRefreshing) {
            Log.debug("CmAdminDataReader: skipped, is currently being refresehed&& needsRefreshing");
        }
        else if(!needsRefreshing) {
            Log.debug("CmAdminDataReader: skipped, not needed");
        }
        else {
            fireRefreshData();
        }
    }

    /** Fire refresh of all data refreshers 
     * 
     */
    public void fireRefreshData() {
        isRefreshing=true;
        try {
            
            // InfoPopupBox.display(new CmInfoConfig("Updating Student List", "Updating student list"));
            Log.debug("CmAdminDataReader: refreshing data");
            for (CmAdminDataRefresher reader : dataReaders) {
                reader.refreshData();
            }
            lastRefresh = System.currentTimeMillis();
        }
        finally {
            isRefreshing = false;
        }
    }
}
