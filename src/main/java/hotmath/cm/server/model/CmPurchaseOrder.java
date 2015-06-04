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
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getZipCode() {
			return zipCode;
		}
		public void setZipCode(String zipCode) {
			this.zipCode = zipCode;
		}
	}
	
	class School {
		String name;
		boolean isCollege;
		Address address = new Address();
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean isCollege() {
			return isCollege;
		}
		public void setCollege(boolean isCollege) {
			this.isCollege = isCollege;
		}
		public Address getAddress() {
			return address;
		}
		public void setAddress(Address address) {
			this.address = address;
		}
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
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getLastFourCC() {
			return lastFourCC;
		}
		public void setLastFourCC(String lastFourCC) {
			this.lastFourCC = lastFourCC;
		}
		public String getCheckNumber() {
			return checkNumber;
		}
		public void setCheckNumber(String checkNumber) {
			this.checkNumber = checkNumber;
		}
		public String getPoNumber() {
			return poNumber;
		}
		public void setPoNumber(String poNumber) {
			this.poNumber = poNumber;
		}
		public Date getExpirationDateCC() {
			return expirationDateCC;
		}
		public void setExpirationDateCC(Date expirationDateCC) {
			this.expirationDateCC = expirationDateCC;
		}
		public String getTransactionIdCC() {
			return transactionIdCC;
		}
		public void setTransactionIdCC(String transactionIdCC) {
			this.transactionIdCC = transactionIdCC;
		}
		public boolean isSuccess() {
			return isSuccess;
		}
		public void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}
		public Address getAddress() {
			return address;
		}
		public void setAddress(Address address) {
			this.address = address;
		}
		public String getCardholder() {
			return cardholder;
		}
		public void setCardholder(String cardholder) {
			this.cardholder = cardholder;
		}
	}
	
	class Contact {
		String name;
		String title;
		String phone;
		String alternateContact;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getAlternateContact() {
			return alternateContact;
		}
		public void setAlternateContact(String alternateContact) {
			this.alternateContact = alternateContact;
		}
	}

	class CmLicense {
		int numStudents;
		int numYears;
		Date startDate;
		float total;
		public int getNumStudents() {
			return numStudents;
		}
		public void setNumStudents(int numStudents) {
			this.numStudents = numStudents;
		}
		public int getNumYears() {
			return numYears;
		}
		public void setNumYears(int numYears) {
			this.numYears = numYears;
		}
		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		public float getTotal() {
			return total;
		}
		public void setTotal(float total) {
			this.total = total;
		}
	}

	class CmAddlSchools {
		int numSchools;
		int feePerSchool;
		int total;
		public int getNumSchools() {
			return numSchools;
		}
		public void setNumSchools(int numSchools) {
			this.numSchools = numSchools;
		}
		public int getFeePerSchool() {
			return feePerSchool;
		}
		public void setFeePerSchool(int feePerSchool) {
			this.feePerSchool = feePerSchool;
		}
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
	}

	class CmProfDevl {
		int numDays;
		int feePerDay;
		int total;
		public int getNumDays() {
			return numDays;
		}
		public void setNumDays(int numDays) {
			this.numDays = numDays;
		}
		public int getFeePerDay() {
			return feePerDay;
		}
		public void setFeePerDay(int feePerDay) {
			this.feePerDay = feePerDay;
		}
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
	}

}
