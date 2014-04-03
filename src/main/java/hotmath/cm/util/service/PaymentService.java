package hotmath.cm.util.service;

import hotmath.HotMathException;
import hotmath.payment.HotMathPayment;
import hotmath.payment.PaymentResult;
import hotmath.paypal.PayPalManager;
import hotmath.subscriber.HotMathExceptionPurcaseException;

import org.apache.log4j.Logger;

import sb.mail.SbMailManager;
import sb.util.SbException;

public class PaymentService {

	private static final Logger __logger = Logger.getLogger(PaymentService.class);

    static public void doPurchase(String ipAddress, double amount, String ccNum, String ccType, String ccv2,
            String expMonth, String expYear, String ccZip, String ccState, String ccAddress1, String ccAddress2,
            String ccCity, String ccFirstName, String ccLastName, int userId, String email,
            String loginName, String password) throws HotMathException {
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

            addPurchaseResult(result, userId);

            purchaseComplete(result, email, loginName, password); 

        } catch (Throwable t) {
        	__logger.error("Error during purchase", t);
            if (t instanceof HotMathExceptionPurcaseException) {
                throw (HotMathExceptionPurcaseException) t;
            } else {
                // low level error -- give general error
                throw new HotMathException(t,
                        "Sorry, we seem to be having difficulties completing this transaction.   Please try again or use our PayPal service.");
            }
        }
    }

	static private void addPurchaseResult(PaymentResult result, int userId) throws Exception {

		//TODO: persist purchase result
		
		if (result.isSuccess() == false) {
            // addComment("Purchase failed (order_id=" + result.getOrderNumber() + ", approved=" + result.isSuccess() + ")");

			throw new HotMathExceptionPurcaseException(result);
		}
        // addComment("Purchase complete: " + getTimeStamp());
		
	}

	static private void purchaseComplete(final PaymentResult result, final String email, final String loginName, final String password) throws Exception {
        try {

            // send mail in separate thread
            // to ensure user thread does not lock
            new Thread(new Runnable() {
                @SuppressWarnings("unchecked")
                public void run() {

                    StringBuilder sb = new StringBuilder();
                    try {
                    	sb.append("Your Catchup Math purchase is complete.\n\n");
                    	sb.append(" Order Number: ").append(result.getOrderNumber()).append("\n");
                    	sb.append(" Login Name: ").append(loginName).append("\n");
                    	sb.append(" Password: ").append(password).append("\n\n");
                    	
                    	sb.append("Please retain this information.\n\n");
                    	
                    	sb.append("Contact: support@catchupmath.com");
                    	
                        SbMailManager.getInstance().sendMessage("Catchup Math Purchase", sb.toString(), email,
                                "saupport@catchupmath.com", "text/plain", null);
                    } catch (SbException e) {
                        __logger.error("error sending email to: " + email, e);
                    }
                }
            }).start();

        } catch (Exception e) {
            throw new HotMathException(e, "Error sending email confirmation.");
        }
	}

}
