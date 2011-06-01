package hotmath.spring;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import sb.util.SbFile;
import sb.util.SbUtilities;


/** Handle the creation of the Spring Context
 * 
 * @author casey
 *
 */
public class SpringManager {
    
    static Logger __logger = Logger.getLogger(SpringManager.class);
    
    
    static {
        HotmathFlusher.getInstance().addFlushable(new Flushable() {
            @Override
            public void flush() {
                __logger.info("Flushing SpringManager");
                __instance = null;
            }
        });
    }
    
    
    static private SpringManager __instance;
    static public SpringManager getInstance() throws Exception {
        if(__instance == null) {
            __instance = new SpringManager();
        }
        return __instance;
    }
    
    
    BeanFactory _beanFactory;
    private SpringManager() throws Exception {
        __logger.info("Creating new SpringManager");
        Map<String,String> map = new HashMap<String,String>(){
            {
                put("user", CatchupMathProperties.getInstance().getProperty("dbpool.user", "hmadmin"));
                put("password", CatchupMathProperties.getInstance().getProperty("dbpool.password", "hmadmin"));
                put("url", CatchupMathProperties.getInstance().getProperty("dbpool.url", "jdbc:mysql://hotmath.kattare.com/hotmath_test?user=$$USER$$"));
            }
        };
        String xml = SbUtilities.replaceTokens(
                new SbFile(CatchupMathProperties.getInstance().getCatchupRuntime() + "/spring.xml").getFileContents().toString("\n"), 
                map);
                
        Resource res = new ByteArrayResource(xml.getBytes());
        _beanFactory = new XmlBeanFactory(res);
    }
    
    public BeanFactory getBeanFactory() {
        return _beanFactory;
    }
}
