package hotmath.cm.server.filter;

import hotmath.cm.server.MyServletRequestWrapper;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DevelopmentFilter implements Filter {
	private static final Logger LOGGER = Logger.getLogger(DevelopmentFilter.class.getName());

	public DevelopmentFilter() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
		MyServletRequestWrapper httpReq = new MyServletRequestWrapper((HttpServletRequest)request);
		HttpServletResponse httpRes = (HttpServletResponse)response;
		HttpSession 	session = httpReq.getSession();
		
		
		String acrHeaders = httpReq.getHeader("Access-Control-Request-Headers");
		String acrMethod = httpReq.getHeader("Access-Control-Request-Method");
		
		httpRes.setHeader("Access-Control-Allow-Origin", "*");
		httpRes.setHeader("Access-Control-Allow-Headers", acrHeaders);
		httpRes.setHeader("Access-Control-Allow-Methods", acrMethod);
		
		
		chain.doFilter(httpReq, httpRes);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}

