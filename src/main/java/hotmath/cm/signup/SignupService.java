package hotmath.cm.signup;

import hotmath.HotMathProperties;
import hotmath.servlet.Registration;
import hotmath.subscriber.HotMathExceptionPurcaseException;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberSignupInfo;
import hotmath.subscriber.PurchasePlan;
import hotmath.subscriber.service.HmService;
import hotmath.subscriber.service.HmServiceImplDefault;
import hotmath.subscriber.service.HmServiceManager;
import hotmath.subscriber.service.HotMathSubscriberServiceFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sb.util.SbUtilities;

public class SignupService extends HttpServlet {
    private static final long serialVersionUID = 590787380495271748L;
    
    static {
        HotMathSubscriberServiceFactory.addServiceType("catchup", SignupSubscriberService.class);
        HmServiceImplDefault service = new HmServiceImplDefault();
        service.setName("catchup");
        service.setCost(0.0);
        service.setMetered(0);
        service.setUnitSize(1);
        service.setUnitType("");
        service.setLabel("Catchup Math");
        HmServiceManager.getInstance().getAllServices().add(service);
    }

    Logger _logger = Logger.getLogger(SignupService.class.getName());

    public SignupService() {
        System.out.println("Creating ..");
    }

    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        _logger.log(Level.INFO, "Attempting to create a new Subscriber account for: " + req.getRemoteAddr());
        
        HotMathSubscriber sub=null;
        try {
            @SuppressWarnings("unchecked")
            Map<String, String[]> formData = req.getParameterMap();

            // String ipOfCaller = request.getRemoteAddr();
            String ipOfCaller = req.getRemoteAddr();

            String lastName = "";
            HotMathProperties.getInstance();

            String email = getFData(formData.get("confirm_email"));
            if (email == null || email.length() == 0)
                throw new Exception("'email' cannot be null");

            String firstName = getFData(formData.get("first_name"));
            String type_school = getFData(formData.get("sel_school_type"));
            lastName = getFData(formData.get("last_name"));
            String cardholder_address1 = getFData(formData.get("address1"));
            String cardholder_address2 = getFData(formData.get("address2"));
            String cardholder_city = getFData(formData.get("city"));
            String cardholder_zip = getFData(formData.get("zip"));
            String cardholder_state = getFData(formData.get("sel_state"));

            String hear_about = getFData(formData.get("sel_hearabout"));
            String store_code = getFData(formData.get("store_code"));
            String ccType = getFData(formData.get("sel_cardtype"));
            String ccCvv2 = getFData(formData.get("card_ccv2"));

            // extract credit card info
            String cardNumber = getFData(formData.get("card_number"));
            String cardExpMonth = getFData(formData.get("sel_card_expire_month"));
            String cardExpYear = getFData(formData.get("sel_card_expire_year"));

            // remove any spaces in card number
            cardNumber = SbUtilities.replaceSubString(cardNumber, " ", "");

            // look up the price plan for each service
            List<PurchasePlan> plans = new ArrayList<PurchasePlan>();
            PurchasePlan purchasePlan = new PurchasePlan("TYPE_SERVICE_CATCHUP");
            plans.add(purchasePlan);

            if (lastName.equals("error")) {
                throw new HotMathExceptionPurcaseException("INTENTIONAL SERVER ERROR");
            }
            if (cardNumber == null || lastName == null) {
                throw new HotMathExceptionPurcaseException("Cardnumber and lastname must be specified");
            }

            HotMathSubscriberSignupInfo signupInfo = new HotMathSubscriberSignupInfo();

            // save non-secure info
            signupInfo.setBillingAddress(cardholder_address1 + cardholder_address2);
            signupInfo.setBillingCity(cardholder_city);
            signupInfo.setBillingState(cardholder_state);
            signupInfo.setBillingZip(cardholder_zip);
            signupInfo.setFirstName(firstName);
            signupInfo.setLastName(lastName);

            // set the secure data as variables only
            signupInfo.setCardEmail(email);
            signupInfo.setCardType(ccType);
            signupInfo.setCardNumber(cardNumber);
            signupInfo.setCardCcv(ccCvv2);
            signupInfo.setCardExpMonth(cardExpMonth);
            signupInfo.setCardExpYear(cardExpYear);

            sub = Registration.setupPurchase(Registration.getLoginInfo(req), email, firstName,
                    lastName, cardholder_zip, hear_about, store_code, type_school, signupInfo);

            String password = sub.getId();

            // perform purchase option
            sub.purchaseHotmath(ipOfCaller, plans, cardNumber, ccType, ccCvv2, cardExpMonth, cardExpYear,
                    cardholder_zip, cardholder_state, cardholder_address1, cardholder_address2, cardholder_city,
                    firstName, lastName);

            // add subscriber record to this user's session in order to allow
            // for enabling tutoring with the enableTutoring button on the
            // success page
            // @TODO: when to remove from session ... is there another way to
            // provide
            // temporary storage of CC info
            req.getSession().setAttribute("sub", sub);

            resp.getWriter().write("SUBSCRIBER creation success:" + password);

        } catch (Exception e) {
            _logger.log(Level.SEVERE,"Error creating new account", e);
            try {
                sub.addComment(e.getMessage());
            } catch (Exception ee) {
                _logger.log(Level.SEVERE,"Error adding comment", ee);
            }
            resp.getWriter().write("error:" + e.getMessage());
        }
    }

    private String getFData(Object o) {
        if (o instanceof String[]) {
            return ((String[]) o)[0];
        } else {
            return SbUtilities.getStringValue(o);
        }
    }

}
