package hotmath.testset.ha;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;

import java.util.Date;

/** Hold general information about admin in a temporary cache
 * 
 * @author casey
 *
 */
public class AdminInfoCache {

    public static Date getExpireDate(int adminId) throws Exception  {
        AdminCachedInfo admin = getAdminInfo(adminId);
        return admin.getExpireDate();
    }

    private static AdminCachedInfo getAdminInfo(int adminId) throws Exception {
        AdminCachedInfo admin = (AdminCachedInfo)CmCacheManager.getInstance().retrieveFromCache(CacheName.ADMIN_INFO, adminId);
        if(admin == null) {
            Date expireDate = CmAdminDao.getInstance().getAdminExpireDate(adminId);
            admin = new AdminCachedInfo(expireDate);
            CmCacheManager.getInstance().addToCache(CacheName.ADMIN_INFO, adminId, admin);
        }
        return admin;
    }

}

/** The cached information
 * 
 * @author casey
 *
 */
class AdminCachedInfo {
    Date expireDate;
    public AdminCachedInfo(Date expireDate) {
        this.expireDate = expireDate;
    }
    public Date getExpireDate() {
        return expireDate;
    }
}
