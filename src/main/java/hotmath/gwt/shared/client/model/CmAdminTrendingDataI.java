package hotmath.gwt.shared.client.model;

import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmList;

/** Defines the API for generating Trending reports
 * 
 * @author casey
 *
 */
public interface CmAdminTrendingDataI extends Response {
    CmList<TrendingData> getTrendingData();
}
