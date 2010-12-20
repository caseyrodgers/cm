
<%@page import="net.sf.ehcache.Cache"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="net.sf.ehcache.CacheManager"%><%

String[] cnArray =  CacheManager.getInstance().getCacheNames();
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
%>

Cache status: <%= status %>