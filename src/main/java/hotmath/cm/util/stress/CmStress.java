package hotmath.cm.util.stress;

import hotmath.gwt.cm_rpc_core.server.rpc.ContextListener;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;


public class CmStress {

    final static Logger __logger = Logger.getLogger(CmStress.class);
    static List<CmStressThread> __instances = new ArrayList<CmStressThread>();
    
    static public void main(String as[]) {
        new ContextListener();
        SbUtilities.addOptions(as);

        int aid = SbUtilities.getInt(SbUtilities.getOption("0", "-admin_id"));
        int count = SbUtilities.getInt(SbUtilities.getOption("100", "-count"));
        int delay = SbUtilities.getInt(SbUtilities.getOption("1", "-delay"));
        String testClassName = SbUtilities.getOption(null, "-test_class");
        
        
        __logger.info("Starting: aid=" + aid + ", count=" + count + ", delay=" + delay + ", testClassName=" + testClassName);
        
        
        try {
            new CmStressRunner(aid, count, delay, testClassName).runTests();

            /**
             * create exit watcher that will perform exit when all threads
             * have completed.
             * 
             * TODO: why do I have to do this?
             */
            Thread exitWatcher = new Thread() {
                public void run() {
                    try {
                        while (__instances.size() > 0) {
                            sleep(1000);
                        }
                        __logger.info("Exiting");
                        System.exit(0);
                    } catch (Exception e) {
                        __logger.error(e);
                    }
                }
            };
            exitWatcher.start();        
        }
        catch(Exception e) {
            __logger.error("Error starting runner", e);
        }
        
    }
}

