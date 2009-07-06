package hotmath.cm.util;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.testset.ha.HaTestDef;

import java.util.logging.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;


/** Manage a set of caches 
 * 
 * @author bob
 * @author casey
 *
 */
public class CmCacheManager  {

    
    static private CmCacheManager __instance;
    static public CmCacheManager getInstance() {
        if(__instance == null) {
            __instance = new CmCacheManager();
        }
        return __instance;
    }
    
    
    static {
       HotmathFlusher.getInstance().addFlushable(new Flushable() {
        public void flush() {
            if(__instance != null) {
                __instance.flushCache();
                __instance = null;
            }
        }
       });
    }
    
    static Logger logger = Logger.getLogger(CmCacheManager.class.getName());
    
    
    
    /** Distinct cache names as defined in configuration files (ehcache.xml)
     * 
     */
	public static enum CacheName{TEST_DEF};
	
	
	private void flushCache() {
	    logger.info("Shutting down EHCache");
		CacheManager.getInstance().shutdown();
	}

	/** Create EHCache manager
	 *  This reads the configuration file stored in 
	 *  src/main/resources/ehcache.xml
	 *  
	 */
	public  CmCacheManager() {
		CacheManager.create();
		logger.info("+++ started Cache Manager");
	}
	
	/** Return named object from specified cache.
	 *  
	 * @param cacheName  The cache name to use
	 * @param name The name of the object in the cache
	 * 
	 * @return The cached object or null
	 */
    public Object retrieveFromCache(CacheName cacheName, String name) {
        CacheManager cm = CacheManager.getInstance();
        Cache cache = cm.getCache(cacheName.toString());
        
        Element el = cache.get(name);
        if(el != null) {
            // return value if in cache
            return (HaTestDef)cache.get(name).getObjectValue();
        }
        else {
            // if not in cache, return null
            return null;
        }
    }
    
    /** Add the object to the named cache.
     * 
     * @param cacheName The cache to insert the object
     * @param key the key for the object
     * @param toCache The object to cache
     */
    public void addToCache(CacheName cacheName, String key, Object toCache) {
        logger.info("addToCache called with " + cacheName + " " + key);
        CacheManager cm = CacheManager.getInstance();
        Cache cache = cm.getCache(cacheName.toString());
        Element e = new Element(key, toCache);
        cache.put(e);
    }	

}
