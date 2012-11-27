package hotmath.testset.ha;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;

import java.util.Date;

public class AdminInfoCache {

    public static Date getExpireDate(int adminId) throws Exception  {
        AdminCachedInfo admin = getAdminInfo(adminId);
        return admin.getExpireDate();
    }

    private static AdminCachedInfo getAdminInfo(int adminId) throws Exception {
        AdminCachedInfo admin = (AdminCachedInfo)CmCacheManager.getInstance().retrieveFromCache(CacheName.ADMIN_INFO, adminId);
        if(admin == null) {
            Date expireDate = CmAdminDao.getInstance().getAdminExpireDate(adminId);
            
            CmCacheManager.getInstance().addToCache(CacheName.ADMIN_INFO, adminId, new AdminCachedInfo(expireDate));
        }
        return admin;
    }

}


class AdminCachedInfo {
    Date expireDate;
    public AdminCachedInfo(Date expireDate) {
        this.expireDate = expireDate;
    }
    public Date getExpireDate() {
        return expireDate;
    }
}
