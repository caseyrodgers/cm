package hotmath.cm.signup;

import hotmath.cm.dao.CmUserDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.servlet.Registration;
import hotmath.subscriber.HotMathExceptionPurcaseException;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberSignupInfo;
import hotmath.subscriber.PurchasePlan;
import hotmath.subscriber.service.HmServiceImplDefault;
import hotmath.subscriber.service.HmServiceManager;
import hotmath.subscriber.service.HotMathSubscriberServiceFactory;
import hotmath.testset.ha.HaAdmin;
import hotmath.testset.ha.HaLoginInfo;
import hotmath.testset.ha.HaUser;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sb.util.SbUtilities;

public class CatchupSignupServlet extends HttpServlet {
    private static final long serialVersionUID = 590787380495271748L;
    
    static {
        /** Register with the Hotmath signup system custom 
         *  Catchup Math implementations of key classes. 
         */
        HotMathSubscriberServiceFactory.addServiceType("catchup", HotmathSubscriberServiceCatchup.class);
        HmServiceImplDefault service = new HmServiceImplDefault();
        service.setName("catchup");
        service.setCost(0.0);
        service.setMetered(0);
        service.setUnitSize(1);
        service.setUnitType("");
        service.setLabel("Catchup Math");
        HmServiceManager.getInstance().getAllServices().add(service);
    }

    static Logger _logger = Logger.getLogger(CatchupSignupServlet.class.getName());

    public CatchupSignupServlet() {
    	/* empty */
    }

    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        _logger.info("Attempting to create a new Subscriber account for: " + req.getRemoteAddr());
        
        HotMathSubscriber sub=null;
        try {

            // String ipOfCaller = request.getRemoteAddr();
            String ipOfCaller = req.getRemoteAddr();

            
            /** Extract the data from the request 
             * 
             */
            HotMathSubscriberSignupInfo sifo = getSignupInfo(req);

            
            /** Setup the purchase by either creating or reusing existing SUBSCRIBER record based
             *  on this user's email.
             */
            sub = Registration.setupPurchase(Registration.getLoginInfo(req), sifo.getCardEmail(), sifo.getFirstName(),
                    sifo.getLastName(), sifo.getBillingZip(), "","", "", sifo);

            @SuppressWarnings("unchecked")
            Map<String, String[]> formData = req.getParameterMap();
            String selectedService = getFData(formData.get("selected_services"));

            // look up the price plan for each service
            List<PurchasePlan> plans = new ArrayList<PurchasePlan>();
            PurchasePlan purchasePlan = new PurchasePlan(selectedService);
            plans.add(purchasePlan);
            
            
            // perform purchase option
            sub.purchaseHotmath(ipOfCaller, plans, sifo.getCardNumber(),sifo.getCardType(),sifo.getCardCcv(), sifo.getCardExpMonth(), sifo.getCardExpYear(),
                    sifo.getBillingZip(), sifo.getBillingState(), sifo.getBillingAddress(), "", sifo.getBillingCity(),
                    sifo.getFirstName(), sifo.getLastName());

            /** create or re-create the HA_ADMIN user 
             *  associated with this SUBSCRIBER record
             */
            CmAdminDao cad = new CmAdminDao();
            HaAdmin haAdmin = cad.getAdmin(sub.getId());
            String uniquePassword = sub.getPassword();
            if(haAdmin == null) {
                String uniqAdminUser = "admin_" + sifo.getCardEmail();
                haAdmin = new CmAdminDao().addAdmin(sub.getId(),uniqAdminUser, uniquePassword);
            }
            
            
            /** Create or re-create the HaUser associated with this HaAdmin account
             * 
             */
             CmUserDao uad = new CmUserDao();
             
             List<HaUser> users = uad.getUsers(haAdmin.getAdminId());
             HaUser user = null;
             if(users.size() > 0)
                 user = users.get(0); // get first user 
             
             if(user == null) {
                 // create new user
                 StudentModelI student = new StudentModel();
                 student.setName(sifo.getFirstName() + " " + sifo.getLastName());
                 
                 student.setPasscode(uniquePassword);
                 student.setPassPercent("80%");
                 student.setAdminUid(haAdmin.getAdminId());
                 student.setGroupId("1");
                 
                 StudentProgramModel stdProgram = new StudentProgramModel();
                 stdProgram.setProgramType("Auto-Enroll");
                 stdProgram.setSubjectId("");
                 student.setProgram(stdProgram);
                 
                 student.getSettings().setTutoringAvailable(true);
                 student.getSettings().setShowWorkRequired(false);
                 
                 Connection conn=null;
                 try {
                     conn = HMConnectionPool.getConnection();
                     CmStudentDao csd = new CmStudentDao();
                     student = csd.addStudent(conn, student);
                 }
                 finally {
                     SqlUtilities.releaseResources(null,null,conn);
                 }
                 
                 user = new HaUser();
                 user.setUid(student.getUid());
             }
            
             
             /** Register a login, and return security key allowing auto login
              * 
              */
             HaLoginInfo userInfo = new HaLoginInfo(user);

            
            /** Return JSON containing key values
             * 
             */
            String returnJson = "{sid:'" + sub.getId() + "',key: '" + userInfo.getKey() + "',uid:" +  user.getUid() + ", " +
                                  "userName:'" + sifo.getCardEmail() + "', " +
                                  "password:'" + uniquePassword + "'" +
                                 "}";
            resp.getWriter().write(returnJson);

        } catch (Exception e) {
            _logger.error("*** Error creating new account", e);
            try {
                sub.addComment(e.getMessage());
            } catch (Exception ee) {
                _logger.error("*** Error adding comment", ee);
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
    
    
    /** Extract data from request and build SignupInfo to encapsulate request
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private HotMathSubscriberSignupInfo getSignupInfo(HttpServletRequest req) throws Exception {
        
        @SuppressWarnings("unchecked")
        Map<String, String[]> formData = req.getParameterMap();
        
        String email = getFData(formData.get("confirm_email"));
        if (email == null || email.length() == 0)
            throw new Exception("'email' cannot be null");

        String firstName = getFData(formData.get("first_name"));
        String lastName = getFData(formData.get("last_name"));
        String cardholder_address1 = getFData(formData.get("address1"));
        String cardholder_address2 = getFData(formData.get("address2"));
        String cardholder_city = getFData(formData.get("city"));
        String cardholder_zip = getFData(formData.get("zip"));
        String cardholder_state = getFData(formData.get("sel_state"));

        String ccType = getFData(formData.get("sel_cardtype"));
        String ccCvv2 = getFData(formData.get("card_ccv2"));

        // extract credit card info
        String cardNumber = getFData(formData.get("card_number"));
        String cardExpMonth = getFData(formData.get("sel_card_expire_month"));
        String cardExpYear = getFData(formData.get("sel_card_expire_year"));

        // remove any spaces in card number
        cardNumber = SbUtilities.replaceSubString(cardNumber, " ", "");

        if (lastName.equals("error")) {
            throw new HotMathExceptionPurcaseException("INTENTIONAL SERVER ERROR");
        }
        if (cardNumber == null || lastName == null) {
            throw new HotMathExceptionPurcaseException("Cardnumber and lastname must be specified");
        }

        
        /** Move signup data into persistent storage
         * 
         */
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
        
        
        return signupInfo;
    }

}
