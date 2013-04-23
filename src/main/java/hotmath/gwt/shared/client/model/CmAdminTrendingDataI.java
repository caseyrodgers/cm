package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** Defines the API for generating Trending reports
 * 
 * @author casey
 *
 */
public interface CmAdminTrendingDataI extends Response {
    CmList<ProgramData>  getProgramData();
    CmList<TrendingData> getTrendingData();
}
