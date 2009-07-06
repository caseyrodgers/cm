package com.hotmath.cm.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.ehcache.CacheManager;

public class CmCacheManager implements ServletContextListener {

	public static final String TEST_DEF_CACHE_KEY = "TEST_DEF";
	
	public void contextDestroyed(ServletContextEvent sce) {
		CacheManager.getInstance().shutdown();
	}

	public void contextInitialized(ServletContextEvent sce) {
		CacheManager.create();
		CacheManager cm = CacheManager.getInstance();
		cm.addCache(TEST_DEF_CACHE_KEY);
		System.out.println(String.format("+++ started Cache Manager, added %s cache", TEST_DEF_CACHE_KEY));
	}

}
