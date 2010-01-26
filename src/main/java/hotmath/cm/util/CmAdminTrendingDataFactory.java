package hotmath.cm.util;

import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataImplDefault;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataImplDummy;
import hotmath.gwt.shared.client.util.CmException;

public class CmAdminTrendingDataFactory {
    
    static public CmAdminTrendingDataI create(TYPE type) throws Exception {
        if(type == TYPE.DUMMY) {
            return new CmAdminTrendingDataImplDummy();
        }
        else if(type == TYPE.LIVE) {
            return new CmAdminTrendingDataImplDefault();
        }
        else {
            throw new CmException("Unknown TYPE: " + type);
        }
    }
    static public enum TYPE{DUMMY,LIVE};
}
