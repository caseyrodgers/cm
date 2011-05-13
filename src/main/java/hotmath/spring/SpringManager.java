package hotmath.spring;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;


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
        Resource res = new FileSystemResource(CatchupMathProperties.getInstance().getCatchupRuntime() + "/spring.xml");
        _beanFactory = new XmlBeanFactory(res);
    }
    
    public BeanFactory getBeanFactory() {
        return _beanFactory;
    }
}
