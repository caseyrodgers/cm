package hotmath.cm.util;

import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;

import org.apache.log4j.Logger;

/**
 * Provides a means of reading in multi-line property files without having to
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
 * default property file: runtime/catchup.mprops
 * (note it lives in runtime not src/main)
 * 
 * 
 * You can cause a re-read of the properties by flushing the server via
 * loading resources/util/cm_system_flush.jsp.
 
 * 
 * @author casey
 * 
 */
public class CmMultiLinePropertyReader extends AbstractCmMultiLinePropertyReader {

	private static final long serialVersionUID = -2215233022960313983L;

	static private CmMultiLinePropertyReader __instance;

    static public CmMultiLinePropertyReader getInstance() throws Exception {
        if (__instance == null) {
            String mprops = CatchupMathProperties.getInstance().getCatchupRuntime() + "/catchup.mprops";   
            __instance = new CmMultiLinePropertyReader(mprops);
        }

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
