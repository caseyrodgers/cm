package hotmath.cm.util.service;

import hotmath.HotMathException;
import hotmath.cm.server.model.CmPaymentDao;
import hotmath.payment.HotMathPayment;
import hotmath.payment.PaymentResult;
import hotmath.paypal.PayPalManager;
import hotmath.subscriber.HotMathExceptionPurcaseException;

import org.apache.log4j.Logger;

import sb.mail.SbMailManager;
import sb.util.SbException;

/**
 * 
 * General purpose payment service
 * - perform purchase via PayPal
 * - send confirmation email
 * - record transaction
 *
 * @author bob
 * 
 */

public class PaymentService {

	private static final Logger __logger = Logger.getLogger(PaymentService.class);

    static public void doPurchase(String ipAddress, double amount, String ccNum, String ccType, String ccv2,
            String expMonth, String expYear, String ccZip, String ccState, String ccAddress1, String ccAddress2,
            String ccCity, String ccFirstName, String ccLastName, int userId, String email,
            String loginName, String password, String groupName, String serviceType) throws HotMathException {
        try {

            // for testing
            if (ccNum.equals("4321"))
                amount = 0;

            // only charge account if plan is a for pay plan
            PaymentResult result = null;
            if (amount > 0) {
                int expMonthi = Integer.parseInt(expMonth);
                int expYeari = Integer.parseInt(expYear);
                HotMathPayment pay = new HotMathPayment(ccNum, ccType, ccv2, expMonthi, expYeari, "US", ccZip, ccState,
                        ccAddress1, ccAddress2, ccCity, ccFirstName, ccLastName, String.valueOf(userId), ipAddress, amount);
                result = PayPalManager.getInstance().doDirectPayment(pay);
            } else {
                result = new PaymentResult(String.valueOf(userId), true);
            }

            addPurchaseResult(result, userId, amount);

            purchaseComplete(result, email, loginName, password, groupName, amount, serviceType); 

        } catch (Throwable t) {
        	__logger.error("Error during purchase", t);
            if (t instanceof HotMathExceptionPurcaseException) {
                throw (HotMathExceptionPurcaseException) t;
            } else {
                // low level error -- give general error
                throw new HotMathException(t,
                        "Sorry, we seem to be having difficulties completing this transaction.  Please try again.");
            }
        }
    }

	static private void addPurchaseResult(PaymentResult result, int userId, double amount) throws Exception {

		CmPaymentDao dao = CmPaymentDao.getInstance();
		try {
			dao.create(result, userId, amount);
		}
		catch (Exception e) {
			String msg = String.format("Error saving payment: Order #: %s, userId: %d, amount: %.2f, success: %s",
					result.getOrderNumber(), userId, amount, result.isSuccess());
			__logger.error(msg, e);
		}
		
		if (result.isSuccess() == false && amount > 0) {
			throw new HotMathExceptionPurcaseException(result);
		}
	}

	static private void purchaseComplete(final PaymentResult result, final String email, final String loginName,
			final String password, final String groupName, final double amount, final String serviceType) throws Exception {
        try {

            // send email in separate thread
            // to ensure user thread does not lock
            new Thread(new Runnable() {
                public void run() {

                    boolean sendEmail = false;
                    try {
                    	String msg = "";
                    	if ("TYPE_SERVICE_CATCHUP_SELF_PAY".equals(serviceType)) {
                    		msg = getSelfPayMessage(groupName, loginName, password, result.getOrderNumber(), amount);
                    		sendEmail = true;
                    	}
                    	
                        if (sendEmail == true)
                        	SbMailManager.getInstance().sendMessage("Catchup Math Purchase", msg, email,
                                "support@catchupmath.com", "text/plain", null);
                    } catch (SbException e) {
                        __logger.error("error sending email to: " + email, e);
                    }
                }

            }).start();

        } catch (Exception e) {
            throw new HotMathException(e, "Error sending email confirmation.");
        }
	}

	private static String getSelfPayMessage(String groupName, String loginName, String password, String orderNumber, double amount) {
		StringBuilder sb = new StringBuilder();
    	sb.append("Thank You for your Catchup Math purchase for ").append(groupName).append(".\n\n");
    	sb.append("To log in (as directed by your instructor), go to Catchupmath.com and enter:\n");
    	sb.append(" Login Name: ").append(loginName).append("\n");
    	sb.append(" Password: ").append(password).append("\n\n");
    	sb.append("Please retain this email in case you forget your password.\n\n");
    	
    	sb.append("Order Number: ").append(orderNumber).append("\n");
    	sb.append(String.format(" Amount Charged: $ %.2f\n\n", amount));
    	
    	sb.append("We wish you every success with Catchup Math!\n\n");
    	sb.append("CM Support");
    	return sb.toString();
	}

	private static String getOneTeacherMessage(String loginName, String password, String orderNumber, double amount) {
		StringBuilder sb = new StringBuilder();
    	sb.append("Thank You for your Catchup Math purchase.\n\n");
    	sb.append("To log in, go to Catchupmath.com and enter:\n");
    	sb.append(" Login Name: ").append(loginName).append("\n");
    	sb.append(" Password: ").append(password).append("\n\n");
    	sb.append("Please retain this email in case you forget your password.\n\n");
    	
    	sb.append("Order Number: ").append(orderNumber).append("\n");
    	sb.append(String.format(" Amount Charged: $ %.2f\n\n", amount));
    	
    	sb.append("We wish you every success with Catchup Math!\n\n");
    	sb.append("CM Support");
    	return sb.toString();
	}

}
