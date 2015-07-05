package hotmath.cm.signup;

import hotmath.cm.server.model.CmPurchaseOrder;
import hotmath.cm.server.model.CmPurchaseOrderDao;
import hotmath.cm.util.service.PaymentService;
import hotmath.payment.PaymentResult;
import hotmath.subscriber.HotMathExceptionPurcaseException;
import hotmath.subscriber.SalesZone;
import hotmath.subscriber.SalesZone.Representative;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import com.cedarsoftware.util.io.JsonWriter;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class PurchaseOrderServlet extends CatchupSignupServlet {
    
	private static final long serialVersionUID = 3381316087339267095L;

	static Logger _logger = Logger.getLogger(PurchaseOrderServlet.class.getName());

    public PurchaseOrderServlet() {
    	/* empty */
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        _logger.info("Attempting to create a CM Purchase Order for: " + req.getRemoteAddr());
        
        try {
            String ipOfCaller = req.getRemoteAddr();

            /** Extract the data from the request 
             */
            CmPurchaseOrder po = createPurchaseOrder(req);

            PurchaseOrderResult poRes = setSalesRep(po);
            poRes.school = po.getSchool().getName();

            // persist purchase order
            CmPurchaseOrderDao dao = CmPurchaseOrderDao.getInstance();
            dao.create(po);

            // perform purchase option
            PaymentResult result = PaymentService.doPurchaseOrder(ipOfCaller, po.getTotal(), po.getPayment().getCardNumber(),
            		po.getPayment().getType(), po.getPayment().getCcv2(), po.getPayment().getExpirationMonthCC(), po.getPayment().getExpirationYearCC(),
            		po.getPayment().getAddress().getZipCode(), po.getPayment().getAddress().getState(),
            		po.getPayment().getAddress().getStreet1(), po.getPayment().getAddress().getStreet2(),
            		po.getPayment().getAddress().getCity(),
            		po.getPayment().getContact().getFirstName(), po.getPayment().getContact().getLastName(),
            		-1, po.getContact().getEmail(),
            		po.getContact().getName(), po.getContact().getPhone(), po.getSchool().getName(),
            		poRes.repName, poRes.repEmail);
            po.getPayment().setTransactionIdCC(result.getOrderNumber());
            
            poRes.isSuccess = result.isSuccess();
            //String json = JsonWriter.objectToJson(poRes);
            StringBuilder sb = new StringBuilder();
            sb.append("{repEmail:'").append(poRes.repEmail).append("', isSuccess:'").append(poRes.isSuccess);
            String repName = (poRes.repName.equals("lincoln") == true)?"David Lincoln":"Tina Hamilton";
            sb.append("', school:'").append(poRes.school).append("', repName:'").append(repName).append("'}");
            resp.getWriter().write(sb.toString());

        } catch (Exception e) {
            _logger.error("*** Error creating Purchase Order", e);
            
            resp.getWriter().write("{error:'" + e.getMessage() +"'}");
        }
    }

    private CmPurchaseOrder createPurchaseOrder(HttpServletRequest req) throws Exception {
    	CmPurchaseOrder po = new CmPurchaseOrder();
    
        Map<String, String[]> formData = req.getParameterMap();
        
        po.getContact().setName(getFData(formData.get("primary_name")));
        po.getContact().setTitle(getFData(formData.get("primary_title")));
        po.getContact().setEmail(getFData(formData.get("primary_email")));
        po.getContact().setPhone(getFData(formData.get("primary_phone")));
        po.getContact().setAlternateContact(getFData(formData.get("primary_alternate")));

        po.getSchool().setName(getFData(formData.get("institution_name")));
        po.getSchool().setLoginName(getFData(formData.get("login_name")));
        po.getSchool().getAddress().setDepartment(getFData(formData.get("intitution_department")));
        po.getSchool().getAddress().setCity(getFData(formData.get("intitution_city")));
        po.getSchool().getAddress().setState(getFData(formData.get("intitution_state_sel")));
        po.getSchool().getAddress().setZipCode(getFData(formData.get("intitution_zip")));

        po.getLicense().setNumStudents(Integer.parseInt(getFData(formData.get("num_students"))));
        po.getLicense().setNumYears(Integer.parseInt(getFData(formData.get("num_years"))));
        String licenseFee   = getFData(formData.get("license_fee"));
        // null check shouldn't be needed, but just in case...
        licenseFee = (licenseFee != null)?licenseFee.replaceAll("\\$",""):"0";
        po.getLicense().setTotal(Double.parseDouble(licenseFee));

        // null check shouldn't be needed, but just in case...
        String totalOrder = getFData(formData.get("total_order"));
        totalOrder = (totalOrder != null)?totalOrder.replaceAll("\\$", ""):"0";
        po.setTotal(Double.parseDouble(getFData(totalOrder)));
        po.setOrderDate(new Date());

        String addlSchools = getFData(formData.get("num_schools"));
        int addlSchoolsNum = (addlSchools != null && addlSchools.isEmpty() == false) ?
        		Integer.parseInt(addlSchools.trim()) : 0;
        po.getAddlSchools().setNumSchools(addlSchoolsNum);
        addlSchools = getFData(formData.get("addl_schools_fee"));
        double addlSchoolsFee = (addlSchools != null && addlSchools.isEmpty() == false) ?
        		Double.parseDouble(addlSchools.trim()) : 0;
        po.getAddlSchools().setTotal(addlSchoolsFee);

        String profDev = getFData(formData.get("num_pd_days"));
        int profDevDays = (profDev != null && profDev.isEmpty() == false) ?
        		Integer.parseInt(profDev.trim()) : 0;
        po.getProfDevl().setNumDays(profDevDays);
        profDev = getFData(formData.get("pd_days_fee"));
        double profDevFee = (profDev != null && profDev.isEmpty() == false) ?
        		Integer.parseInt(profDev.trim()) : 0;
        po.getProfDevl().setTotal(profDevFee);
        
        // extract credit card info
        po.getPayment().getContact().setFirstName(getFData(formData.get("first_name")));
        po.getPayment().getContact().setLastName(getFData(formData.get("last_name")));
        po.getPayment().setCardholder(getFData(formData.get("last_name")));
        po.getPayment().setCardholder(getFData(formData.get("last_name")));
        po.getPayment().getAddress().setStreet1(getFData(formData.get("address1")));
        po.getPayment().getAddress().setCity(getFData(formData.get("city")));
        po.getPayment().getAddress().setState(getFData(formData.get("sel_state")));
        po.getPayment().getAddress().setZipCode(getFData(formData.get("zip")));
        po.getPayment().setCcv2(getFData(formData.get("card_ccv2")));
        String cardNumber = getFData(formData.get("card_number"));
        // remove any spaces in card number
        cardNumber = cardNumber.replaceAll(" ", "");
        po.getPayment().setCardNumber(cardNumber);
        po.getPayment().setType(getFData(formData.get("sel_cardtype")));
        po.getPayment().setExpirationMonthCC(getFData(formData.get("sel_card_expire_month")));
        po.getPayment().setExpirationYearCC(getFData(formData.get("sel_card_expire_year")));

        if (po.getPayment().getContact().getLastName().equals("error")) {
            throw new HotMathExceptionPurcaseException("INTENTIONAL SERVER ERROR");
        }

		return po;
	}

    private PurchaseOrderResult setSalesRep(CmPurchaseOrder po) throws Exception {
    	Connection conn = null;
    	PurchaseOrderResult poRes = new PurchaseOrderResult();
    	try {
    		conn = HMConnectionPool.getConnection();
    		String zip = po.getSchool().getAddress().getZipCode();
    		Representative salesPerson = SalesZone.getSalesRepresentativeByZip(conn, zip);
    		_logger.info("getSalesRep(): ZIP: " + zip + ", repId: " + salesPerson.getRepId());
    		po.setSalesZone(salesPerson.getRepId());
    		poRes.repName = salesPerson.getRepId();
    		poRes.repEmail = salesPerson.getEmail();
    		return poRes;
    	}
    	
    	finally {
    		SqlUtilities.releaseResources(null, null, conn);
    	}
    }

    class PurchaseOrderResult {
    	boolean isSuccess;
    	String repName;
    	String repEmail;
    	String school;
    }

}
