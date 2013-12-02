package hotmath.cm.dao;

import org.apache.log4j.Logger;


/** fix up the URL with a proper format if needed.
 * 
 * @author casey
 *
 */
public class LinkConvertImplNoHTTP implements LinkConvert {
    
    Logger _logger = Logger.getLogger(LinkConvertImplNoHTTP.class);    

    @Override
    public String doConversion(String url) {
        if(!url.startsWith("http")) {
            return "http://" + url;
        }
        else {
            return null;
        }
    }

}
