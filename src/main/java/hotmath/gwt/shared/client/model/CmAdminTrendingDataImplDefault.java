package hotmath.gwt.shared.client.model;

import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;

public class CmAdminTrendingDataImplDefault implements CmAdminTrendingDataI{
    CmList<TrendingData> trendingData = new CmArrayList<TrendingData>();
    
    public CmAdminTrendingDataImplDefault() { }
    
    public CmAdminTrendingDataImplDefault(CmList<TrendingData> trendingData) {
        this.trendingData = trendingData;
    }
    
    @Override
    public CmList<TrendingData> getTrendingData() {
        return trendingData;
    }
}
