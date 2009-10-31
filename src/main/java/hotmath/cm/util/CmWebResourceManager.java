package hotmath.cm.util;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;

import java.io.File;

import org.apache.log4j.Logger;

/** Manage an output directory containing temp files
 *  that are accessed via HTTP.
 *  
 *  automatically remove any resources after they have expired
 *   
 * @author casey
 *
 */
public class CmWebResourceManager {
    final static Logger __logger = Logger.getLogger(CmWebResourceManager.class);
    
    static private CmWebResourceManager __instance;
    static public CmWebResourceManager getInstance() {
        if(__instance == null)
            __instance = new CmWebResourceManager();
        return __instance;
        
    }
    
    ResourceWatcherThread watcher;
    String base;
    String webBase;
    
    int EXPIRE_TIME = 1000 * 60 * 60 * 24; // one day
    private CmWebResourceManager() {

        /** Where the output files should be written
         * @TODO: could be set when servlet starts up,
         * but that will create an needed dependency
         */
        this.base = CmMultiLinePropertyReader.getInstance().getProperty("CmWebResourceManager.baseDir", "/dev/local/cm/src/main/webapp/temp").trim();
        try {
            if(!new File(base).exists()) {
                __logger.warn("base directory does not exist: " + base);
                new File(base).mkdir();
            }
            else {
                __logger.info("base directory exists: " + base);
            }
        }
        catch(Exception e) {
            __logger.warn("user: " + System.getProperty("user.name"), e);
            e.printStackTrace();
        }
        
        /** The url base that combined with the filename will 
         *  constructed an absolute URL to the reource via HTTP
         *  
         */
        this.webBase = CmMultiLinePropertyReader.getInstance().getProperty("CmWebResourceManager.webBase", "/temp");
        
        watcher = new ResourceWatcherThread(base, EXPIRE_TIME);
        watcher.start();
    }
    
    
    public void flush() {
      if(watcher != null)
          watcher.cancelWatch();
      watcher = null;
    }
    
    
    /** Return the base user used to access via HTTP
     * 
     * @return
     */
    public String getWebBase() {
       return "/temp"; 
    }
    
    
    /** Return the base directory on file system.
     * 
     * Path must be absolute to allow required control of the output file which
     * needs to be in the physical directory that can be served up by web app.
     * 
     * @return
     */
    public String getFileBase() {
        return base;
    }
    
    
    public void setFileBase(String base) {
        this.base = base;
    }
    

    static class ResourceWatcherThread extends Thread {
        
        int expireTime;
        boolean cancelWatch;
        String base;
        
        public ResourceWatcherThread(String base, int expireTime) {
            super("Resource Watcher Thread");
            this.base = base;
            this.expireTime = expireTime;
        }
        
        @Override
        public void run() {
            __logger.info("Starting resource watcher on directory: " + base);
            while(!cancelWatch) {
                
                File fileBase = new File(base);
                File kids[] = fileBase.listFiles();
                if(kids !=null ) {
                    for(File kid:kids) {
                        long kidMod = kid.lastModified();
                        long et = (System.currentTimeMillis() - kidMod);
                        if(et > expireTime) {
                            __logger.info("Removing temp file: " + kid);
                            kid.delete();
                        }
                    }
                }
                try {
                    Thread.sleep(1000 * 60 * 100);
                }
                catch(InterruptedException ie) {
                    __logger.error("Error putting watcher to sleep", ie);
                }
                
            }
            __logger.info("Canceling resource watcher");
        }
        
        public void cancelWatch() {
            this.cancelWatch = true;
        }
        
    }
    
    
    static {
        HotmathFlusher.getInstance().addFlushable(new Flushable() {
            public void flush() {
                if(__instance!=null) {
                    __instance.flush();
                    __instance = null;
                }
            }
        });
    }
}

