package hotmath.gwt.shared.client.model;

import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;

public class CmAdminTrendingDataImplDefault implements CmAdminTrendingDataI{
    
    CmList<TrendingData> trendingData = new CmArrayList<TrendingData>();
    CmList<ProgramData> programData = new CmArrayList<ProgramData>();
    public CmAdminTrendingDataImplDefault() { }
    
    public CmAdminTrendingDataImplDefault(CmList<TrendingData> trendingData, CmList<ProgramData> programData) {
        this.trendingData = trendingData;
        this.programData = programData;
    }
    
    @Override
    public CmList<TrendingData> getTrendingData() {
        return trendingData;
    }

    @Override
    public CmList<ProgramData> getProgramData() {
        // TODO Auto-generated method stub
        return programData;
    }
}
