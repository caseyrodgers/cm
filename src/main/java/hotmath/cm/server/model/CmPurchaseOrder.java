package hotmath.cm.server.model;

import java.util.Date;

public class CmPurchaseOrder {

	private School school = new School();
	private Payment payment = new Payment();
	private Contact contact = new Contact();
	private CmLicense license = new CmLicense();
	private CmAddlSchools addlSchools = new CmAddlSchools();
	private CmProfDevl profDevl = new CmProfDevl();
	private String salesZone;
	private int total;

	public CmPurchaseOrder() {
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public CmLicense getLicense() {
		return license;
	}

	public void setLicense(CmLicense license) {
		this.license = license;
	}

	public CmAddlSchools getAddlSchools() {
		return addlSchools;
	}

	public void setAddlSchools(CmAddlSchools addlSchools) {
		this.addlSchools = addlSchools;
	}

	public CmProfDevl getProfDevl() {
		return profDevl;
	}

	public void setProfDevl(CmProfDevl profDevl) {
		this.profDevl = profDevl;
	}

	public String getSalesZone() {
		return salesZone;
	}

	public void setSalesZone(String salesZone) {
		this.salesZone = salesZone;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public class Address {
		String street;
		String city;
		String state;
		String zipCode;
	}
	
	class School {
		String name;
		boolean isCollege;
		Address address = new Address();
	}
	
	class Payment {
		String type;
		String lastFourCC;
		String checkNumber;
		String poNumber;
		Date  expirationDateCC;
		String transactionIdCC;
		boolean isSuccess;
		Address address = new Address();
		String cardholder;
	}
	
	class Contact {
		String name;
		String title;
		String phone;
		String alternateContact;
	}

	class CmLicense {
		int numStudents;
		int numYears;
		Date startDate;
		int total;
	}

	class CmAddlSchools {
		int numSchools;
		int feePerSchool;
		int total;
	}

	class CmProfDevl {
		int numDays;
		int feePerDay;
		int total;
	}

}
