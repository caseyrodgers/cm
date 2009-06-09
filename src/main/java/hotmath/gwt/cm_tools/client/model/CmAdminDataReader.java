package hotmath.gwt.cm_admin.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;

/**
 * Timer used to control the refresh of server data
 * 
 * Refreshes data every REFRESH_MILLS
 * 
 * Implemented as singlton to allow exposing the fireRefreshData
 * 
 * There is a single timer that fires every REFRESH_MILLS. You
 * can call fireRefreshData directly,and the next auto fire will 
 * be skipped.
 * 
 */
public class CmAdminDataReader extends Timer {
    static final private int REFRESH_MILLS = (1000 * 30);
    
    
    private static CmAdminDataReader __instance;
    static public CmAdminDataReader getInstance() {
        if(__instance == null) {
            __instance = new CmAdminDataReader();
        }
        return __instance;
    }
    
    List<CmAdminDataRefresher> dataReaders = new ArrayList<CmAdminDataRefresher>();
    boolean isRefreshing;
    long lastRefresh;

    /** Create a data refresher with set dataReaders.
     * 
     * Each time data is needed, each dataReader is asked to 
     * update itself.
     * 
     * @param dataReaders
     */
    public CmAdminDataReader() {
        scheduleRepeating(REFRESH_MILLS);
    }
    
    public void addReader(CmAdminDataRefresher dataReader) {
        this.dataReaders.add(dataReader);
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis() - lastRefresh;
        boolean needsRefreshing = now > REFRESH_MILLS;
        if(!isRefreshing && needsRefreshing) {
            fireRefreshData();
        }
    }

    /** Fire refresh of all data refreshers 
     * 
     */
    public void fireRefreshData() {
        isRefreshing=true;
        try {
            //System.out.println("CmAdmin: Refreshing data for: " + dataReaders.size());
            for (CmAdminDataRefresher reader : dataReaders) {
                reader.refreshData();
            }
            lastRefresh=System.currentTimeMillis();
        }
        finally {
            isRefreshing = false;
        }
    }
}
