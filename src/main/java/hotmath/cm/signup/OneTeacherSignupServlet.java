package hotmath.cm.signup;

import hotmath.cm.dao.SubscriberDao;
import hotmath.cm.util.service.PaymentService;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberManager;
import hotmath.subscriber.HotMathSubscriberSignupInfo;
import hotmath.subscriber.PurchasePlan;
import hotmath.subscriber.SalesZone.Representative;
import hotmath.subscriber.service.HotMathSubscriberServiceFactory;
import hotmath.testset.ha.HaAdmin;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import sb.mail.SbMailManager;

public class OneTeacherSignupServlet extends CatchupSignupServlet {
    
	private static final long serialVersionUID = 632874215893149960L;

	static Logger _logger = Logger.getLogger(OneTeacherSignupServlet.class.getName());

	private static int ONE_TEACHER_MAX_STUDENTS = 49;

	private static final SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

            // perform purchase option
            PaymentService.doPurchase(ipOfCaller, 249.0, sifo.getCardNumber(), sifo.getCardType(),
            		sifo.getCardCcv(), sifo.getCardExpMonth(), sifo.getCardExpYear(),
            		sifo.getBillingZip(), sifo.getBillingState(), sifo.getBillingAddress(),
            		sifo.getBillingCity(), sifo.getFirstName(), sifo.getLastName(),
            		sifo.getSubscriberId(), adminId, subscriber.getEmail(), subscriber.getId(),
            		"admin123", "One Teacher", "TYPE_SERVICE_CATCHUP_ONE_TEACHER_ORDER");
            
            PurchasePlan purchasePlan = new PurchasePlan("TYPE_SERVICE_CATCHUP_ONE_TEACHER_ORDER");
            subscriber.addService(HotMathSubscriberServiceFactory.create("catchup"), purchasePlan);

            // set max students and expiration date 
            SubscriberDao.getInstance().setMaxStudents(null, subscriber, ONE_TEACHER_MAX_STUDENTS);
            SubscriberDao.getInstance().setExpireDate(subscriberId, purchasePlan.getExpiration());

            sendEmail(subscriber);

            /** Return JSON containing key values
             * 
             */
            String expireDate = _dateFormat.format(purchasePlan.getExpiration());
            String json = String.format("{userName:'%s', password:'%s', email:'%s', school:'%s', expires:'%s'}",
            		subscriber.getId(), "admin123", subscriber.getEmail(), subscriber.getSchoolType(), expireDate);

            resp.getWriter().write(json);

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

    private void sendEmail(final HotMathSubscriber sub) throws Exception{

    	try {
    		// send email in separate thread
    		// to ensure user thread does not lock
    		new Thread(new Runnable() {
    			public void run() {

    				/** send a tracking email to subscriber and sales rep
    				 * 
    				 */
    				try {
    					String emailTemplate = "CM-One-Teacher License";
    					sub.sendEmailConfirmation(emailTemplate, null);
    				}
    				catch(Exception e) {
    					_logger.error(String.format("*** problem sennding tracking email for one-teacher purchase for ID: %s", sub.getId()), e);
    				}

    				String txt = "A Catchup Math One-Teacher purchase was made by:"
    						+ "\nSubscriber ID: " + sub.getId()
    						+ "\nName: " + sub.getResponsibleEmail() 
    						+ "\nEmail: " + sub.getEmail() 
    						+ "\nSalesZone: " + sub.getSalesZone();
    				try { 
    					Representative rep = SubscriberDao.getInstance().getSalesRepresentativeByName(sub.getSalesZone());

    					String sendTo[];
    					sendTo = new String[2];
    					sendTo[0] = rep.getEmail();
    					sendTo[1] = "cgrant.hotmath@gmail.com";
    					SbMailManager.getInstance().
    					sendMessage("Catchup Math One-Teacher Purchase", txt, sendTo, "registration@catchupmath.com", "text/plain");
    				} catch (Exception e) {
    					_logger.error(String.format("*** problem sending one-teacher purchase email: %s", txt), e);
    				}
    			}

    		}).start();

    	} catch (Exception e) {
    		throw new Exception("Error sending email confirmation.", e);
    	}

    }    

}
