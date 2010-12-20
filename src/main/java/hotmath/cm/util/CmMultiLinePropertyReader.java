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
 * 
 * 
 * default property file: src/main/resources/hotmath/cm/util/catchup.mprops
 * (note it lives in resources, not java)
 * 
 * 
 * You can cause a re-read of the propertes by flushing the server via
 * loading resources/util/cm_system_flush.jsp.
 
 * 
 * @author casey
 * 
 */
public class CmMultiLinePropertyReader extends AbstractCmMultiLinePropertyReader {

	private static final long serialVersionUID = -2215233022960313983L;

	static private CmMultiLinePropertyReader __instance;

    static public CmMultiLinePropertyReader getInstance() {
        if (__instance == null)
            __instance = new CmMultiLinePropertyReader("catchup.mprops");

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
    
    static Logger __logger = Logger.getLogger(CmMultiLinePropertyReader.class);

    private CmMultiLinePropertyReader(String propFile) {

        __logger.info("Reading multi-line problem file: " + propFile);
        
        loadProps(propFile);
        
    }
    
	@Override
	protected Logger getLogger() {
		return __logger;
	}
}
