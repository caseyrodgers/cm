package hotmath.cm.util;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.gwt.shared.client.util.CmException;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;

/**
 * Externalized properties for Catchup Math
 * 
 * @author casey
 * 
 */
public class CatchupMathProperties extends Properties {

    static {
        HotmathFlusher.getInstance().addFlushable(new Flushable() {
            @Override
            public void flush() {
                __instance = null;
            }
        });
    }

    static Logger __log = Logger.getLogger(CatchupMathProperties.class);
    static private CatchupMathProperties __instance;

    static public CatchupMathProperties getInstance() throws PropertyLoadFileException {
        if (__instance == null)
            __instance = new CatchupMathProperties();
        return __instance;
    }

    private CatchupMathProperties() throws PropertyLoadFileException {
        
        try {
            /** allow override of system properties
             * 
             */
            String propsToUse = System.getenv("CM_PROPERTIES");
            File pfile=null;
            if(propsToUse != null && propsToUse.length() > 0) {
                pfile = new File(propsToUse);
            }
            else {
                pfile = new File(System.getProperty("user.home"), "cm.properties");
            }
            __log.info("Reading properties file: " + pfile);
            if (!pfile.exists()) {
                throw new Exception("Property file '" + pfile + "' does not exist");
            }
            load(new FileReader(pfile));
        }
        catch(Exception e) {
            throw new PropertyLoadFileException(e);
        }
    }
    
    public int getProblemAsInt(String prop, int defNum) {
        String val = getProperty(prop);
        if(val != null) {
            try {
                return Integer.parseInt(val);
            }
            catch(NumberFormatException nfe) {
                __log.error(nfe);
            }
        }
        return defNum;
    }

    /**
     * return absolute local directory where the catchup math deployed webapp
     * directory.
     * 
     * 
     * @return
     * @throws Exception
     */
    public String getCatchupHome() {
        return getProperty("catchup.home", "/dev/local/cm");
    }
    
    /** get directory containing runtime configuration 
     * files, such as the .mprops.
     * 
     * @return
     */
    public String getCatchupRuntime() {
        return getProperty("catchup.runtime", getCatchupHome() + "/runtime");
    }

    public String getSolutionsServer() {
        return getProperty("solution.server", "");
    }

    public int getClientVersionNumber() {
        return SbUtilities.getInt(getProperty("client.version"));
    }

    /**
     * Solution base is absolute path to OS dir of solution base.
     * 
     * @return
     * @throws Exception
     */
    public String getSolutionBase() throws Exception {
        String base = getProperty("solution.base");
        if (base == null) {
            throw new CmException(
                    "'solution.base' must be set in the cm.properties file to the absolute path of the solutions directory.");
        }
        return base;
    }
}
