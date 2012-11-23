package hotmath.cm.util.stress;

import org.apache.log4j.Logger;

/**
 * Stand alone tool and called from /assets/util to stress test the Login
 * Process.
 * 
 * 
 * Log in as each user and do the first thing required. Which might be create a
 * test, prescription, etc..
 * 
 * 
 * @author casey
 * 
 */
public class CmStressThread extends Thread {



    String user, pass;
    int delay;
    boolean stopThread;
    private String testClassName;
    private int uid;
    private int aid;

    static int __counter;

    final static Logger __logger = Logger.getLogger(CmStressThread.class);

    public CmStressThread(int aid, int uid, String userName, String pass, int delay, String testClassName) {
        this.aid = aid;
        this.uid = uid;
        this.user = userName;
        this.pass = pass;
        this.delay = delay;
        this.testClassName = testClassName;
        CmStress.__instances.add(this);
    }

    int id = __counter++;

    
    public void runTest() {
        __logger.info("Login test start: " + this);
        try {
            Thread.sleep(delay);
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "CmStress [user=" + user + ", pass=" + pass + ", aid=" + aid + ", uid=" + uid + ", delay=" + delay + ", id=" + id + "]";
    }

    @Override
    public void run() {

        long start = System.currentTimeMillis();
        try {

            __logger.info(id + " test start [delay=" + delay + "] (" + testClassName + ")");
            
            StressTest test = (StressTest)Class.forName(testClassName).newInstance();
            test.runTest(this.aid, this.uid, this.user, this.pass);
            
            __logger.info(id + " test complete, time: " + (System.currentTimeMillis() - start) / 1000);

        } catch (Exception e) {
            /** only log error, do not throw exception */
            __logger.error("Error during CmStress test: " + this, e);
        } finally {
            CmStress.__instances.remove(this);
        }
    }

}
