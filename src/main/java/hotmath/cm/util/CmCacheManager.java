package hotmath.cm.util;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;

import java.util.Arrays;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.log4j.Logger;

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
	public static enum CacheName {
		TEST_DEF, 
		TEST, 
		TEST_HTML, 
		TEST_HTML_CHECKED, 
		TEST_DEF_DESCRIPTION,
		REPORT_ID, 
		SUBJECT_CHAPTERS,
		BULK_UPLOAD_FILE,
		PRESCRIPTION,
		STUDENT_PAGED_DATA,
		HA_USER,
		
		/** IneedMoreHelpItems looked for a pid */
		INMH_ITEMS,
		
		/** Lists of solutions that can be used to create lesson RPPS */
		WOOKBOOK_POOL,
		
		/** Map of lesson files and their sort rankings */
		LESSON_RANKINGS,
		
		/** list of books in a given category */
		CATEGORY_BOOKS,
		
		
		/** Book Info Model used by HmMobile */
		BOOK_INFO_MODEL,
		
		/** Book Model for HM Mobile */
		BOOK_MODEL,
		
		/** Problem number lists used in Hm Mobile */
		PROBLEM_NUMBERS,
		
		/** Activity Times for Time-on-task */
		ACTIVITY_TIMES,
		
		/** List of all custom program lesson names */
		ALL_CUSTOM_PROGRAM_LESSONS, ALL_CUSTOM_QUIZ_LESSONS,
		
		/** anonymous topic searches */
		TOPIC_SEARCH_PRESCRIPTION, 
		
		/** distinct list of assigment problems
		 *  and any problem type information
		 */
		ASSIGNMENT_PROBLEMS, 
		
		/** Temporary look up information for admins
		 * 
		 */
		ADMIN_INFO, 

		/** List of lists of quiz alternate test ids 
		 * 
		 */
		QUIZ_ALTERNATES,
		
		/** Assignments, problems,  students and 
		 * their statuses for each problem.
		 * 
		 */
		ASSIGNMENT_STUDENTS,

		/** Subject Proficiency Sections for each Proficiency
		 *  subject
		 */
	    SUBJECT_PROF_SECTIONS

	};

	final static public String KEY_ALL="all";
	
	private void flushCache() {
		logger.debug("Shutting down EHCache");
		CacheManager.getInstance().shutdown();
	}

	/** Create EHCache manager
	 *  This reads the configuration file stored in 
	 *  src/main/resources/ehcache.xml
	 *  
	 */
	public  CmCacheManager() {
		CacheManager.create();
        String[] cnArray = CacheManager.getInstance().getCacheNames();
        List<String> cNames = null;
        if (cnArray != null) {
        	cNames = Arrays.asList(cnArray);
        	// registered listener for each cache
        	for(String cn: cNames) {
        	    CacheManager.getInstance().getCache(cn).getCacheEventNotificationService().registerListener(new CmCacheEventListener(cn));            
        	}
        }
		logger.info("+++ started Cache Manager, cache names: " + cNames);
		logger.info("+++ Temp directory: " + System.getProperty("java.io.tmpdir"));
	}
	
	public String getCacheMemoryStatus() {
	    String[] cnArray = CacheManager.getInstance().getCacheNames();
        List<String> cNames = null;
        String status="";
        if (cnArray != null) {
            cNames = Arrays.asList(cnArray);
            // registered listener for each cache
            for(String cn: cNames) {
                Cache cache = CacheManager.getInstance().getCache(cn);
                
                if(status.length() > 0)
                    status += ", ";
                status += " cache: " + cn + ", memoryStoreSize: " + cache.getMemoryStoreSize(); 
            }
        }	 
        return status;
	}

	/** Return named object from specified cache.
	 *  
	 * @param cacheName  The cache name to use
	 * @param name The name of the object in the cache
	 * 
	 * @return The cached object or null
	 */
    public Object retrieveFromCache(CacheName cacheName, Object key) {
        CacheManager cm = CacheManager.getInstance();
        Cache cache = cm.getCache(cacheName.toString());

        Element el = cache.get(key);
        if(el != null) {
          	if (logger.isDebugEnabled())
          		logger.debug(String.format("retrieveFromCache(): retrieved: cacheName: %s, key: %s", cacheName, key));
            // return value if in cache
            return el.getObjectValue();
        }
        else {
            // if not in cache, return null
            return null;
        }
    }

    /** Add the object to the named cache.
     * 
     * @param cacheName the cache to add the object to
     * @param key the key for the object
     * @param toCache the object to cache
     */
    public void addToCache(CacheName cacheName, Object key, Object toCache) {
        addToCache(cacheName, key, toCache, false);
    }
    
    public void addToCache(CacheName cacheName, Object key, Object toCache, boolean flushCache) {
        if (logger.isDebugEnabled())
        	logger.debug(String.format("addToCache(): cacheName: %s, key: %s", cacheName, key));

        CacheManager cm = CacheManager.getInstance();
        Cache cache = cm.getCache(cacheName.toString());
        Element e = new Element(key, toCache);
        cache.put(e);

        if (flushCache)
            cache.flush();
    }	

    
    class CmCacheEventListener implements CacheEventListener {

        String name;
        
        public CmCacheEventListener(String name) {
            super();
            this.name = name;
        }
        
        @Override
        public void dispose() {
            if (logger.isDebugEnabled())
                logger.debug(name + " disposed");
        }

        @Override
        public void notifyElementEvicted(Ehcache cache, Element element) {
            if (logger.isDebugEnabled())
            	logger.debug(cache.getName() + " notifyElementEvicted: " + element.getKey());
        }

        @Override
        public void notifyElementExpired(Ehcache cache, Element element) {
            if (logger.isDebugEnabled())
            	logger.debug(cache.getName() + " notifyElementExpired: " + element.getKey());
        }

        @Override
        public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
        	if (logger.isDebugEnabled())
        		logger.debug(cache.getName() + " notifyElementPut: " + element.getKey());            
        }

        @Override
        public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
        	if (logger.isDebugEnabled())
        		logger.debug(cache.getName() + " notifyElementRemoved: " + element.getKey());
        }

        @Override
        public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
        	if (logger.isDebugEnabled())
        		logger.debug(cache.getName() + " notifyElementUpdated: " + element.getKey());            
        }

        @Override
        public void notifyRemoveAll(Ehcache cache) {
        	if (logger.isDebugEnabled())
        		logger.debug(cache.getName() + " notifyRemoveAll");
        }
        
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
	
	
