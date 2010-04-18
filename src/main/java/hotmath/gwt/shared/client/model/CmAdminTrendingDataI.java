package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmList;

/** Defines the API for generating Trending reports
 * 
 * @author casey
 *
 */
public interface CmAdminTrendingDataI extends Response {
    CmList<ProgramData>  getProgramData();
    CmList<TrendingData> getTrendingData();
}
