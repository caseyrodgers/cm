package hotmath.cm.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import hotmath.gwt.cm_rpc.client.ClientInfo;
import hotmath.cm.util.ClientInfoHolder;

/**
 *
 * ClientInfoFilter creates a ClientInfoHolder (ThreadLocal) for the current request thread
 *
 * @author bob
 * 
 */
public class ClientInfoFilter implements Filter {

    //~ Instance fields --------------------------------------------------------

    private final Logger logger = Logger.getLogger(ClientInfoFilter.class);

    //~ Methods ----------------------------------------------------------------

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain filterChain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
        	ClientInfo clientInfo = (ClientInfo)((HttpServletRequest)request).getSession().getAttribute("clientInfo");
        	ClientInfoHolder.set(clientInfo);
        }

        filterChain.doFilter(request, response);
        
    }

    public void destroy() {
    }
}
