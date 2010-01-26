package hotmath.cm.util;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataImplDummy;
import hotmath.gwt.shared.client.util.CmException;

public class CmAdminTrendingDataFactory {
    
    static public CmAdminTrendingDataI create(TYPE type) throws Exception {
        if(type == TYPE.DUMMY) {
            return new CmAdminTrendingDataImplDummy(new CmAdminDao().getAccountInfo(2));
        }
        else {
            throw new CmException("Unknown TYPE: " + type);
        }
    }
    static public enum TYPE{DUMMY,LIVE};
}
