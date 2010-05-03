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
    final static String WEB_BASE = "cm_temp";
    final static String PERM_DIR = "retain";
    

    
    int EXPIRE_TIME = 1000; //  * 60 * 60 * 24; // one day
    
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
        
        watcher = new ResourceWatcherThread(__fileBase, EXPIRE_TIME);
        watcher.start();
    }
    
    /** return directory that will not be cleansed by the
     *  automatic file cleaner.
     *    
     *  These files will not be deleted.
     *  
     * @return
     */
    public String getRetainedFileBase() {
        return getFileBase() + "/" + PERM_DIR;
    }
    
    public String getRetailedWebBase() {
        return PERM_DIR;
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
       return WEB_BASE;
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
        
        /** time between checks
         * 
         */
        static final int SLEEP_TIME = 1000 * 60 * 60;

        public ResourceWatcherThread(String base, int expireTime) {
            super("Resource Watcher Thread");
            this.base = base;
            this.expireTime = expireTime;
        }
        
        @Override
        public void run() {
            __logger.info("Starting resource watcher on directory: " + base + ", intervals: " + SLEEP_TIME);
            try {
                while(!cancelWatch) {
                    
                    File fileBase = new File(base);
                    
                    cleanDir(fileBase);
                    
                    try {
                        Thread.sleep(SLEEP_TIME);
                    }
                    catch(InterruptedException ie) {
                        __logger.error("Error putting watcher to sleep", ie);
                    }
                }
            }
            catch(CmException ce) {
                __logger.error("Error watching resource directory", ce);
            }
            __logger.info("Canceling resource watcher");
        }

        private void cleanDir(File dir) throws CmException {
            if(!fileIsNotChildOfWebBase(dir))
                throw new CmException("Cannot delete invalid resource directory: " + dir.getPath());
            
            
            /** do not process the permanent dir
             * 
             */
            if(dir.getName().equals(PERM_DIR)) {
                __logger.info("NOT removing '" + PERM_DIR + "'");
                return;
            }
            
            
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
                        __logger.info("Removing temp resource file: " + kid);
                        kid.delete();
                    }
                }
            }
        }
        
        public void cancelWatch() {
            this.cancelWatch = true;
        }
        
        
        /** Make sure that file is either 'temp'
         * or a child of temp.
         * 
         * @param f
         * @return
         */
        private boolean fileIsNotChildOfWebBase(File f) {
            if(f.getName().equals(WEB_BASE))
                return true;
            else if(f.getParentFile() != null){
                return fileIsNotChildOfWebBase(f.getParentFile());
            }
            else
                return false;
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

