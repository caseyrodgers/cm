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
	private double total;
	Date orderDate;
	private String errMsg;

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

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public class Address {
		String department;
		String street1;
		String street2;
		String city;
		String state;
		String zipCode;

		public String getDepartment() {
			return department;
		}
		public void setDepartment(String department) {
			this.department = department;
		}
		public String getStreet1() {
			return street1;
		}
		public void setStreet1(String street) {
			this.street1 = street;
		}
		public String getStreet2() {
			return street2;
		}
		public void setStreet2(String street2) {
			this.street2 = street2;
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
	
	public class School {
		String name;
		String loginName;
		boolean isCollege;
		Address address = new Address();
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getLoginName() {
			return loginName;
		}
		public void setLoginName(String loginName) {
			this.loginName = loginName;
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
	
	public class Payment {
		String type;
		String lastFourCC;
		String ccv2;
		String checkNumber;
		String poNumber;
		String expirationMonthCC;
		String expirationYearCC;
		String transactionIdCC;
		boolean isSuccess;
		Address address = new Address();
		Contact contact = new Contact();
		String cardholder;
		private String cardNumber;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getLastFourCC() {
			return lastFourCC;
		}
		public String getCcv2() {
			return ccv2;
		}
		public void setCcv2(String ccv2) {
			this.ccv2 = ccv2;
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
		public String getExpirationMonthCC() {
			return expirationMonthCC;
		}
		public void setExpirationMonthCC(String expirationMonthCC) {
			this.expirationMonthCC = expirationMonthCC;
		}
		public String getExpirationYearCC() {
			return expirationYearCC;
		}
		public void setExpirationYearCC(String expirationYearCC) {
			this.expirationYearCC = expirationYearCC;
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
		public Contact getContact() {
			return contact;
		}
		public void setContact(Contact contact) {
			this.contact = contact;
		}
		public String getCardholder() {
			return cardholder;
		}
		public void setCardholder(String cardholder) {
			this.cardholder = cardholder;
		}
		public String getCardNumber() {
			return cardNumber;
		}
		public void setCardNumber(String cardNumber) {
			this.cardNumber = cardNumber;
			if (cardNumber != null && cardNumber.length()>4) {
				lastFourCC = cardNumber.substring(cardNumber.length()-4); 
			}
			else {
				lastFourCC = cardNumber;
			}
		}
	}
	
	public class Contact {
		String name;
		String firstName;
		String lastName;
		String title;
		String phone;
		String email;
		String alternateContact;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
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
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getAlternateContact() {
			return alternateContact;
		}
		public void setAlternateContact(String alternateContact) {
			this.alternateContact = alternateContact;
		}
	}

	public class CmLicense {
		int numStudents;
		double numYears;
		double total;
		public int getNumStudents() {
			return numStudents;
		}
		public void setNumStudents(int numStudents) {
			this.numStudents = numStudents;
		}
		public double getNumYears() {
			return numYears;
		}
		public void setNumYears(double numYears) {
			this.numYears = numYears;
		}
		public double getTotal() {
			return total;
		}
		public void setTotal(double total) {
			this.total = total;
		}
	}

	public class CmAddlSchools {
		int numSchools;
		double feePerSchool;
		double total;
		public int getNumSchools() {
			return numSchools;
		}
		public void setNumSchools(int numSchools) {
			this.numSchools = numSchools;
		}
		public double getFeePerSchool() {
			return feePerSchool;
		}
		public void setFeePerSchool(double feePerSchool) {
			this.feePerSchool = feePerSchool;
		}
		public double getTotal() {
			return total;
		}
		public void setTotal(double total) {
			this.total = total;
		}
	}

	public class CmProfDevl {
		int numDays;
		double feePerDay;
		double total;

		public int getNumDays() {
			return numDays;
		}
		public void setNumDays(int numDays) {
			this.numDays = numDays;
		}
		public double getFeePerDay() {
			return feePerDay;
		}
		public void setFeePerDay(double feePerDay) {
			this.feePerDay = feePerDay;
		}
		public double getTotal() {
			return total;
		}
		public void setTotal(double total) {
			this.total = total;
		}
	}

}
