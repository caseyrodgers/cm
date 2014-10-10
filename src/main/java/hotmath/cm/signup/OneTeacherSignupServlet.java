package hotmath.cm.signup;

import hotmath.cm.util.service.PaymentService;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberManager;
import hotmath.subscriber.HotMathSubscriberSignupInfo;
import hotmath.subscriber.PurchasePlan;
import hotmath.subscriber.service.HotMathSubscriberServiceFactory;
import hotmath.testset.ha.HaAdmin;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class OneTeacherSignupServlet extends CatchupSignupServlet {
    
	private static final long serialVersionUID = 632874215893149960L;

	static Logger _logger = Logger.getLogger(OneTeacherSignupServlet.class.getName());

    public OneTeacherSignupServlet() {
    	/* empty */
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        _logger.info("Attempting to create a CM one-teacher account for: " + req.getRemoteAddr());
        
        HotMathSubscriber subscriber = null;
        try {
            String ipOfCaller = req.getRemoteAddr();

            /** Extract the data from the request 
             * 
             */
            boolean isEmailRequired = false;
            HotMathSubscriberSignupInfo sifo = getSignupInfo(req, isEmailRequired);
            String subscriberId = sifo.getSubscriberId();

            subscriber = HotMathSubscriberManager.findSubscriber(sifo.getSubscriberId());

        	HaAdmin admin = null;
            int adminId = 0;
        	try {
            	admin = CmAdminDao.getInstance().getAdmin(subscriberId);
            	adminId = admin.getAdminId();
        	}
        	catch(Exception e) {
            	String msg = String.format("{error:'Catchup Math Pilot account not found for login code: %s'}", subscriberId);
            	resp.getWriter().write(msg);
            	return;
        	}

            subscriber.addService(HotMathSubscriberServiceFactory.create("catchup"), new PurchasePlan("TYPE_SERVICE_CATCHUP_ONE_TEACHER"));

            // perform purchase option
            PaymentService.doPurchase(ipOfCaller, 249.0, sifo.getCardNumber(), sifo.getCardType(),
            		sifo.getCardCcv(), sifo.getCardExpMonth(), sifo.getCardExpYear(),
            		sifo.getBillingZip(), sifo.getBillingState(), sifo.getBillingAddress(),
            		sifo.getBillingCity(), sifo.getFirstName(), sifo.getLastName(),
            		sifo.getSubscriberId(), adminId, subscriber.getEmail(), subscriber.getId(),
            		subscriber.getPassword(), "One Teacher", "TYPE_SERVICE_CATCHUP_ONE_TEACHER");
            
            /** Return JSON containing key values
             * 
             */
            String returnJson = "{sid:'" + subscriber.getId() + ", " +
                                  "userName:'" + subscriber.getId() + "', " +
                                  "password:'" + subscriber.getPassword() + "'}";
            
            String json = String.format("{sid:'%s', userName:'%s', password:'%s'}",
            		subscriber.getId(), subscriber.getId(), subscriber.getPassword());

            resp.getWriter().write(returnJson);

        } catch (Exception e) {
            _logger.error("*** Error converting pilot to one-teacher account", e);
            try {
                if (subscriber != null) subscriber.addComment(e.getMessage());
            } catch (Exception ee) {
                _logger.error("*** Error adding one-teacher comment", ee);
            }
            
            resp.getWriter().write("{error:'" + e.getMessage() +"'}");
        }
    }

	private int getAdminId(String subscriberId) throws Exception {
		ResultSet rs = null;
        PreparedStatement s1 = null;
        Connection conn = null;
        int adminId = 0;
        try {
            conn = HMConnectionPool.getConnection();
            s1 = conn.prepareStatement("select aid from HA_ADMIN where subscriber_id = ?");
            s1.setString(1, subscriberId);
            rs = s1.executeQuery();
            if(rs.first()) {
            	adminId = rs.getInt("aid");
            }
        }
        finally {
            SqlUtilities.releaseResources(rs, s1, conn);
        }
		return adminId;
	}

}
