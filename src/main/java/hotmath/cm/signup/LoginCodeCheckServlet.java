package hotmath.cm.signup;

import hotmath.HotMathException;
import hotmath.HotMathExceptionUserUnknown;
import hotmath.cm.dao.SubscriberDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberManager;
import hotmath.subscriber.HotMathSubscriberSignupInfo;
import hotmath.testset.ha.HaAdmin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class LoginCodeCheckServlet extends CatchupSignupServlet {
    
	private static final long serialVersionUID = 632874215893149960L;

	static Logger _logger = Logger.getLogger(LoginCodeCheckServlet.class.getName());

    public LoginCodeCheckServlet() {
    	/* empty */
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        _logger.info("Attempting to check a login code for: " + req.getRemoteAddr());
        
        HotMathSubscriber subscriber = null;
        boolean isPilot = false;
        boolean isPilotEmail = false;
        try {
            /** Extract the data from the request 
             * 
             */
            boolean isEmailRequired = false;
            HotMathSubscriberSignupInfo sifo = getSignupInfo(req, isEmailRequired);
            String subscriberId = sifo.getSubscriberId();

            isPilot = true;
            try {
                subscriber = HotMathSubscriberManager.findSubscriber(sifo.getSubscriberId());
                if (subscriber.getService("catchup") == null) {
                	// not a CM subscriber
                	isPilot = false;
                }
            }
            catch (HotMathExceptionUserUnknown e) {
            	isPilot = false;
            }

            if (isPilot == true) {
            	// locate admin record
            	HaAdmin admin = null;
            	try {
                	admin = CmAdminDao.getInstance().getAdmin(subscriberId);
            	}
            	catch(Exception e) {}

            	if (admin == null) isPilot = false;
            }
        	if (isPilot == true)
                isPilot = SubscriberDao.getInstance().isCmPilot(subscriber.getId());

            Map<String, String[]> formData = req.getParameterMap();
            String email = getFData(formData.get("pilot_email"));
            isPilotEmail = subscriber.getEmail().equalsIgnoreCase(email);

            String msg = String.format("{isPilot:'%s', isPilotEmail:'%s'}",  isPilot, isPilotEmail);
            resp.getWriter().write(msg);

        } catch (Exception e) {
            _logger.error("*** Error checking login code and email", e);
            try {
                if (subscriber != null) subscriber.addComment(e.getMessage());
            } catch (Exception ee) {
                _logger.error("*** Error adding comment", ee);
            }
            
            String msg = String.format("{isPilot:'%s', isPilotEmail:'%s'}",  false, false);
            resp.getWriter().write(msg);
        }
    }
/*
	private String maskEmail(String email) {

		if (email == null || email.trim().length() < 10) return email;
		email = email.trim();
		int offset = email.indexOf("@");
		char[] maskedEmail = new char[email.length()];
		char[] origEmail = email.toCharArray();
		maskedEmail[0] = origEmail[0];
		for (int i=1; i<offset-1; i++) {
			maskedEmail[i] = '*';
		}
		for (int i=offset-1; i<email.length(); i++) {
			maskedEmail[i] = origEmail[i];
		}
		return new String(maskedEmail);
	}
*/
}
