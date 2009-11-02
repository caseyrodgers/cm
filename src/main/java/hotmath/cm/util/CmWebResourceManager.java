package hotmath.cm.util;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.gwt.shared.client.util.CmException;

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

    static String __fileBase;
    
    static private CmWebResourceManager __instance;
    static public CmWebResourceManager getInstance() throws Exception {
        if(__instance == null)
            __instance = new CmWebResourceManager();
        return __instance;
        
    }
    
    ResourceWatcherThread watcher;
    String webBase;
    
    int EXPIRE_TIME = 1000 * 60 * 60 * 24; // one day
    
    /** the __fileBase variable must be set to the location of the temp
     *  directory.  This is usually set during servlet initialization,
     *  but might need to be done manually when testing or standalone operation.
     */
    private CmWebResourceManager() throws Exception {
        if(__fileBase == null)
            throw new CmException("__fileBase variable must be set prior to initialization");
        
        try {
            if(!new File(__fileBase).exists()) {
                __logger.warn("base directory does not exist: " + __fileBase);
                new File(__fileBase).mkdir();
            }
            else {
                __logger.info("base directory exists: " + __fileBase);
            }
        }
        catch(Exception e) {
            __logger.warn("user: " + System.getProperty("user.name"), e);
            e.printStackTrace();
        }
        
        /** The url base that combined with the filename will 
         *  construct an absolute URL to the resource via HTTP
         *  
         */
        this.webBase = CmMultiLinePropertyReader.getInstance().getProperty("CmWebResourceManager.webBase", "/temp");
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
        return __fileBase;
    }
    
    
    static public void setFileBase(String base) {
        __logger.info("Setting base: " + base);
        __fileBase = base;
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
                
                cleanDir(fileBase);
                
                try {
                    Thread.sleep(1000 * 60 * 100);
                }
                catch(InterruptedException ie) {
                    __logger.error("Error putting watcher to sleep", ie);
                }
                
            }
            __logger.info("Canceling resource watcher");
        }

        private void cleanDir(File dir) {
            File kids[] = dir.listFiles();
            if (kids !=null ) {
                for(File kid:kids) {
                    if (kid.isDirectory()) {
                        cleanDir(kid);
                        continue;
                    }
                    long kidMod = kid.lastModified();
                    long et = (System.currentTimeMillis() - kidMod);
                    if(et > expireTime) {
                        __logger.info("Removing temp file: " + kid);
                        kid.delete();
                    }
                }
            }
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

