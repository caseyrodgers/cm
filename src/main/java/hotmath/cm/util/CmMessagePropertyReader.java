package hotmath.cm.util;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;

/**
 * Provides a means of reading in mulit-line property files without having to
 * provide line continuation escapes.
 * 
 * Each entry is marked as follows:
 * 
 * .key=THE_KEY_NAME
 * 
 * ## comment lines
 * 
 * ANY TEXT WITH MULTI LINES MORE TXT dfdf ...
 * 
 * 
 * .key=ANOTHER_KEY
 * 
 * The other text until EOF or new .key
 * 
 * EOF
 * 
 * loads property file: src/main/resources/hotmath/cm/util/cm-messages.mprops
 * (note it lives in resources, not java)
 * 
 * You can cause a re-read of the propertes by flushing the server via
 * loading resources/util/cm_system_flush.jsp.
 * 
 * @author bob
 * 
 */
public class CmMessagePropertyReader extends AbstractCmMultiLinePropertyReader {

	private static final long serialVersionUID = 1094084759518263654L;

	static private CmMessagePropertyReader __instance;

    static public CmMessagePropertyReader getInstance() {
        if (__instance == null)
            __instance = new CmMessagePropertyReader("cm-messages.mprops");

        return __instance;
    }

    
    /** Add a Flusher to allow properties to be reset without system restart/compile
     * 
     */
    static {
        HotmathFlusher.getInstance().addFlushable(new Flushable() {
            @Override
            public void flush() {
                if (__instance != null) {
                    __instance.clear(); // remove props
                    __instance = null;
                }
            }
        });
    }
    
    static Logger __logger = Logger.getLogger(CmMessagePropertyReader.class);

    private CmMessagePropertyReader(String propFile) {

        __logger.info(String.format("Reading multi-line CM message property file: %s", propFile));
        
        loadProps(propFile);
        
    }
    
	@Override
	protected Logger getLogger() {
		return __logger;
	}
}
