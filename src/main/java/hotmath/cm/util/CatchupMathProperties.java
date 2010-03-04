package hotmath.cm.util;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;

/** Externalized properties for Catchup Math
 * 
 * @author casey
 *
 */
public class CatchupMathProperties extends Properties {
    static Logger __log = Logger.getLogger(CatchupMathProperties.class);
    static private CatchupMathProperties __instance;
    static public CatchupMathProperties getInstance() throws Exception {
        if(__instance == null)
            __instance = new CatchupMathProperties();
        return __instance;
    }
    private CatchupMathProperties() throws Exception {
        File pfile = new File(System.getProperty("user.home"),"cm.properties");
        __log.info("Reading properties file: " + pfile);
        if(!pfile.exists()) {
            throw new Exception("Property file '" + pfile + "' does not exist");
        }
        load(new FileReader(pfile));
    }
    
    public int getClientVersionNumber() {
        return SbUtilities.getInt(getProperty("client.version"));
    }
}
