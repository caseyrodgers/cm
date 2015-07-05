var _card_what_msg='<p class="tooltip_2" >The Card Verification Number is a 3-digit number printed on the back of the card, except for Amex it is a 4-digit number on the front of the card.</p>';

var _terms_msg = '<p class="tooltip_2">Please review our sample session before buying, as the purchase price (as well as any live tutoring purchased) is non-refundable.  Subscription is for use by one person, and may be suspended if abused. We provide our service as-is and when-available. Compensation for claims you may have (including consequential and punitive damages) shall be limited to the amount you have paid for Catchup Math services.</p>';

var _po_terms_msg = '<p class="tooltip_2">Please review our sample session before buying, as the purchase price is non-refundable.  Subscription is for use by the specified institution, and may be suspended if abused. We provide our service as-is and when-available. Compensation for claims you may have (including consequential and punitive damages) shall be limited to the amount you have paid for Catchup Math services.</p>';

var _one_teacher_terms_msg = '<p class="tooltip_2">Please note that the purchase price is non-refundable.  Subscription is for use by one teacher with up to forty-nine students, and may be suspended if abused. We provide our service as-is and when-available. Compensation for claims you may have (including consequential and punitive damages) shall be limited to the amount you have paid for Catchup Math services.</p>';

function setupSignupPage() {
   setupToolTips();   
   
   setCmSelection();

   var e1 = document.getElementById('TYPE_SERVICE_CATCHUP');
   if (e1) e1.onclick = function() {setCmSelection()};

   var e2 = document.getElementById('TYPE_SERVICE_CATCHUP_YEAR');
   if (e2) e2.onclick = function() {setCmSelection();};
}


function setupToolTips() {
   new YAHOO.widget.Tooltip("info_tip1", {
                 context:"card_info",  
                 text:_card_what_msg, 
                 showDelay:400 }
   );
   new YAHOO.widget.Tooltip("info_tip2", {
        context:"card_info2",  
        text:_card_what_msg, 
        showDelay:400 }
   );
   new YAHOO.widget.Tooltip("terms_inf", {
        context:"terms_info",  
        text:_terms_msg,
        autodismissdelay:15000,
        showDelay:400 }
   );
   new YAHOO.widget.Tooltip("one_teacher_terms_inf", {
       context:"one_teacher_terms_info",  
       text:_one_teacher_terms_msg,
       autodismissdelay:15000,
       showDelay:400 }
  );     
   new YAHOO.widget.Tooltip("po_terms_inf", {
       context:"po_terms_info",  
       text:_po_terms_msg,
       autodismissdelay:15000,
       showDelay:400 }
  );     
	   
}


var _plan;
function setCmSelection() {
   var e1 = document.getElementById('TYPE_SERVICE_CATCHUP');
   var e2 = document.getElementById('TYPE_SERVICE_CATCHUP_YEAR');

   var ew = null;
   if(e1 && e1.checked) {
	   e2.checked = false;
       ew = e1;
   }
   else if (e2 && e2.checked) {
	   e1.checked = false;
	   ew = e2;
   }

   if (ew != null) { 
   
       _plan = ew.id;
       var cost = ew.getAttribute("cost");
       if (cost == "10") {
           _plan = "TYPE_SERVICE_CATCHUP_GOODMATH15_3MON";
       }
       else if (cost == "30") {
           _plan = "TYPE_SERVICE_CATCHUP_GOODMATH15_YEAR";
       }
   
       setTotalCost(ew.getAttribute("cost"));
   
       document.getElementById('selected_services').value = _plan;
   }
   else {
       _plan = null;
       setTotalCost("0");
       var e = document.getElementById('selected_services');
       if (e) e.value = null;
   }
}

function applyCode() {
	var code = document.getElementById('catchup_code');
	var e1 = document.getElementById('TYPE_SERVICE_CATCHUP');
	var e2 = document.getElementById('TYPE_SERVICE_CATCHUP_YEAR');
	var e3 = document.getElementById('3_months');
	var e4 = document.getElementById('12_months');
	var e5 = document.getElementById('code_msg');
	if (code.value.toUpperCase() == getCode()) {
		e1.setAttribute("cost", "10");
		e2.setAttribute("cost", "30");
		e3.innerHTML = "$10";
		e4.innerHTML = "$30";
		e5.innerHTML = "Thank you, prices are updated";
		e5.setAttribute("style", "color:green; display:block;");
		
	}
	else {
		e1.setAttribute("cost", "99");
		e2.setAttribute("cost", "199");
		e3.innerHTML = "$99";
		e4.innerHTML = "$199";
		e5.innerHTML = "Sorry, code is invalid";
		e5.setAttribute("style", "color:red; display:block;");
	}
	setCmSelection();
}

var _validationErrorCount=0;
var _totalCost=99;
function checkForm() {

    clearErrorMessages();
    
    fld = $get('parent_email');
    if(fld.value == "") {
        if(showError(fld, "Please specify your email.")) {
            return false;
        }
    }
    var errMsg = validateEmail(fld.value);
    
    if(errMsg != null) {
        if(showError(fld, errMsg))
            return false;
    }
    fld = $get('confirm_email');
    if(fld.value == '') {
        if(showError(fld, "Please confirm the email."))
            return false;

    }

    if( (fld.value != $get('parent_email').value) ) {
        if(showError(fld, "Must match above."))
            return false;
    }
    

    fld = $get('first_name');
    if(fld.value == '') {
        if(showError(fld, "What is your first name?"))
            return false;

    }

    fld = $get('last_name');
    if(fld.value == '') {
        if(showError(fld, "What is your last name?"))
            return false;

    }

    fld = $get('address1');
    if(fld.value == '') {
        if(showError(fld, "Your address is?"))
            return false;

    }

    fld = $get('city');
    if(fld.value == '') {
        if(showError(fld, "What is your city?"))
            return false;

    }

    fld = $get('sel_state');
    if(fld.selectedIndex < 2) {
        if(showError(fld, "Please tell us your state."))
            return false;

    }

    fld = $get('zip');
    if(fld.value == '') {
        if(showError(fld, "Please enter your zip code"))
            return false;

    }
    else if(!validateZip(fld.value)) {
        if(showError(fld, "This is not a valid zip code"))
            return false;

    }

    if(!checkCreditCard())
        return false;

    doSignup();

    return false;   // always return false;
}

function checkSelfPayForm() {

	_totalCost=29;
    clearErrorMessages();
    var isValid = true;
    
    fld = $get('student_first_name');
    if(fld.value == '') {
        if(showError(fld, "What is your first name?"))
            isValid = false;
    }

    fld = $get('student_last_name');
    if(fld.value == '') {
        if(showError(fld, "What is your last name?"))
            isValid = false;
    }

    fld = $get('student_birth_month');
    if(fld.selectedIndex < 1) {
        if(showError(fld, "Please specify your birthday."))
            isValid = false;
    }

    fld = $get('student_birth_day');
    if(fld.selectedIndex < 1) {
        if(showError(fld, "Please specify your birthday."))
            isValid = false;
    }

    fld = $get('student_email');
    if(fld.value == "") {
        if(showError(fld, "Please specify your email.")) {
            isValid = false;
        }
    }
    var errMsg = null;
    if (isValid == true)
    	errMsg = validateEmail(fld.value);
    if (errMsg != null) {
        if(showError(fld, errMsg))
            isValid = false;
    }

    fld = $get('confirm_email');
    if(fld.value == '') {
        if(showError(fld, "Please confirm the email."))
            isValid = false;
    }

    if( (fld.value != $get('student_email').value) ) {
        if(showError(fld, "Must match above."))
            isValid = false;
    }

    if (checkCreditCardData() == false)
    	isValid = false;

    if (isValid == true)
    	doSelfPaySignup();

    return false;   // always return false;
}

var isValidLoginCodeAndEmail = false;
var loginCode = "";
var accountEmail = "";

function checkOneTeacherPayForm() {
	_totalCost = 249;

	clearErrorMessages();

	fld = $get('pilot_login');
	var loginField = fld;
	var isValid = true;

	var loginCodeMsg = null;
	if(fld.value == '') {
		loginCodeMsg = 'What is your login code?';
		showError(fld, loginCodeMsg);
		isValidLoginCodeAndEmail = false;
		loginCode = fld.value;
		isValid = false;
	}

	fld = $get('pilot_email');
	var emailField = fld;
	var emailMsg = validateEmail(fld.value);
	if (fld.value == '') emailMsg = "Account email address is required.";
	if (emailMsg != null) {
		showError(fld, emailMsg);
		isValidLoginCodeAndEmail = false;
		accountEmail = fld.value;
		isValid = false;
	}

	if (loginCode != loginField.value ||
			accountEmail != emailField.value ||
			isValidLoginCodeAndEmail == false) {
		var formObject = document.getElementById('sub_form'); 
		YAHOO.util.Connect.setForm(formObject);
		loginCode = loginField.value;
		accountEmail = emailField.value;

		var requestCallback = {
				success: function(o) {
					var obj = eval('(' + o.responseText + ')');

					var isPilot       = obj.isPilot;
					var isPilotEmail  = obj.isPilotEmail;

					if (isPilot == "true" && isPilotEmail == "true") {
						isValidLoginCodeAndEmail = true;
						if (checkCreditCardData() == true) {
							doOneTeacherSignup();
							return;
						}
					}
					if (isPilot == "false") {
						var txt = (loginCodeMsg != null) ?
								loginCodeMsg : 'Login code not recognized.';
						showError(loginField, txt);
						isValidLoginCodeAndEmail = false;
					}
					if (isPilotEmail == "false") {
						var txt = (emailMsg != null) ?
								emailMsg : 'Primary Email, login must match.';
						showError(emailField, txt);
						isValidLoginCodeAndEmail = false;
					}
					checkCreditCardData();
				},
				failure: function(o) {
					alert('Error checking login code: ' + o.status);
				},
				argument: null
		};
		var cObj = YAHOO.util.Connect.asyncRequest('POST', '/logincode', requestCallback);
	}
	else if (checkCreditCardData() == true) {
		doOneTeacherSignup();
	}

	return false;   // always return false;
}

var payNowEnabled=true;

function checkPurchaseOrderForm() {

	_totalCost=0;
    clearErrorMessages();
    var isValid = true;
    
    isValid = verifyPurchaseOrder();

    if (payNowEnabled == true && checkCreditCardData() == false)
    	isValid = false;

    if (isValid == true)
    	doPurchaseOrder();

    return false;   // always return false;
}

function verifyPurchaseOrder() {

    var isValid = true;

    fld = $get('institution_name');
    if(fld.value.trim() == '') {
        if(showError(fld, "Instituion name is required"))
            isValid = false;
    }

    fld = $get('institution_city');
    if(fld.value.trim() == '') {
        if(showError(fld, "City is required"))
            isValid = false;
    }

    fld = $get('institution_state_sel');
    if(fld.selectedIndex < 1) {
        if(showError(fld, "State is required"))
            isValid = false;
    }

    fld = $get('institution_zip');
    if(fld.value.trim() == '') {
        if(showError(fld, "Zip code is required"))
            isValid = false;
    }

    fld = $get('primary_name');
    if(fld.value.trim() == "") {
        if(showError(fld, "Name is required")) {
            isValid = false;
        }
    }

    fld = $get('primary_title');
    if(fld.value.trim() == "") {
        if(showError(fld, "Title is required")) {
            isValid = false;
        }
    }

    fld = $get('primary_email');
    if(fld.value.trim() == "") {
        if(showError(fld, "Email address is required")) {
            isValid = false;
        }
    }

    var errMsg = null;
    if (isValid == true)
    	errMsg = validateEmail(fld.value.trim());
    if (errMsg != null) {
        if(showError(fld, errMsg))
            isValid = false;
    }

    fld = $get('primary_phone');
    if(fld.value.trim() == '') {
        if(showError(fld, "Phone number is required"))
            isValid = false;
    }

    if (verifyNumericFldsPO() == false) {
        isValid = false;
    }

    return isValid;
}

function verifyNumericFldsPO() {
    var isValid = true;

    var fld = $get('license_fee');
    if(fld.value.trim() != '') {
        if (isNumber(fld.value.trim(), 1) == false) {
            if(showError(fld, "Please enter a numeric value >= 1."))
                isValid = false;
        }
    }    

    fld = $get('addl_schools_fee');
    if(fld.value.trim() != '') {
        if (isNumber(fld.value.trim(), 0) == false) {
            if(showError(fld, "Please enter a numeric value >= 0."))
                isValid = false;
        }
    }    

    fld = $get('pd_days_fee');
    if(fld.value.trim() != '') {
        if (isNumber(fld.value.trim(), 0) == false) {
            if(showError(fld, "Please enter a numeric value >= 0."))
                isValid = false;
        }
    }    

    return isValid;
}

function isNumber(v, min) {
	var val = 1 * v;
    return (val >= min);
}

function checkCreditCardData() {
	var isValid = true;

	fld = $get('first_name');
    if(fld != null && fld.value == '') {
        showError(fld, "What is cardholder's first name?");
        isValid = false;
    }

    fld = $get('last_name');
    if(fld.value == '') {
        showError(fld, "What is cardholder's last name?");
        isValid = false;
    }

    fld = $get('address1');
    if(fld.value == '') {
        showError(fld, "Cardholder's address is?");
        isValid = false;
    }

    fld = $get('city');
    if(fld.value == '') {
        if(showError(fld, "What is cardholder's city?"))
            isValid = false;
    }

    fld = $get('sel_state');
    if(fld.selectedIndex < 2) {
        if(showError(fld, "Please tell us cardholder's state."))
            isValid = false;
    }

    fld = $get('zip');
    if(fld.value == '') {
        if(showError(fld, "Please enter cardholder's zip code."))
            isValid = false;
    }
    else if(!validateZip(fld.value)) {
        if(showError(fld, "This is not a valid zip code"))
            isValid = false;
    }

    if (checkCreditCard() == false)
        isValid = false;
    
    return isValid;
}

function checkCreditCard() {
    fld = $get('sel_cardtype');
    var isValid = true;
    if(fld.selectedIndex < 2) {
        if(showError(fld, "Which type of credit card?"))
            isValid = false;
    }
    var cardType = $get('sel_cardtype').value;

    fld = $get('card_number');
    var cardNumOnlyNums = removeAllNonNumeric(fld.value);
    if(!cardNumOnlyNums) {
        if(showError(fld, "What is credit card number?"))
            isValid = false;
    }
    else if(!cardnumberIsValid(cardNumOnlyNums)) {
        if(showError(fld, "Invalid credit card number."))
            isValid = false;
    }

    if (isValid == false)
    	return isValid;

    // make sure card_type and number match up
    if(cardType == 'Amex') {
        if(cardNumOnlyNums.substring(0,1) != '3') {
            showError(fld, "This card number is not valid for American Express");
            return false;
        }
    }
    else if(cardType == 'Visa') {
       var x = cardNumOnlyNums.substring(0,1);
        if(x != '4') {
           alert('x does not equal 4');
            showError(fld, "This card number is not valid for Visa");
            return false;
        }
    }
    else if(cardType == 'MasterCard') {
        if(cardNumOnlyNums.substring(0,1) != '5') {
            showError(fld, "This card number is not valid for Master Card");
            return false;
        }
    }
    else if(cardType == 'Discover') {
        if(cardNumOnlyNums.substring(0,1) != '6') {
            showError(fld, "This card number is not valid for Discover");
            return false;
        }
    }

    fld = $get('card_ccv2');
    if(!cardSecurityCodeIsValid(cardType, fld.value)) {
        if(cardType == 'Amex') {
            showError(fld, "Your CVV2 value?  The CVV2 for American Express is 4 digits.");
        }
        else {
            showError(fld, "Your CVV2 value?  This is a three digit number.");
        }
        return false;
    }

    fld = $get('sel_card_expire_month');
    if(fld.selectedIndex < 1) {
        if(showError(fld, "Expiration month?"))
            return false;

    }

    fld = $get('sel_card_expire_year');
    if(fld.selectedIndex < 1) {
        if(showError(fld, "Expiration year?"))
            return false;
    }
    
    return true;
}

var _firstErrorFld = null;

function showError(fld, msg) {
    _validationErrorCount++;
    var divs = fld.parentNode.getElementsByTagName("div");
    if(divs.length > 0) {
        divs[0].innerHTML = "^ -- " + msg;
        divs[0].style.display = 'block';
    }
    else {
        alert('no error div for: ' + fld.id);
    }

    // highlight to draw attention to it.
    myTd = fld.parentNode;
    myTd.style.border = "1px solid red";

    if (_firstErrorFld == null) {
    	_firstErrorFld = fld;
    	fld.focus();
    }
    return true;
}

// remove any messages
function clearErrorMessages() {
 // turn off all error message
 _validationErrorCount=0;
 _firstErrorFld = null;
 fields = document.getElementsByTagName("div");
 for(f = 0, t = fields.length; f < t;f++) {
     fields[f].style.border = "none";
     if(fields[f].className == 'input_error') {
         fields[f].style.display = 'none';
     }
 }
}

function getCode() {
    return 'GOODMATH15';
}

// Standardized method to check a field is null, show message
// and return true or false
function _checkFieldNotNull(field, messageIfNull) {
 if(field.value == "") {
     field.focus();
     alert(messageIfNull);
     return false;
 }
 return true;
}

// validate CVV2 code.
//
function cardSecurityCodeIsValid(cardType, s) {
 if(s.length < 3 || s.length > 4) {
     return false;
 }
 // if American Express then must be 4
 if(s.length == 4) {
     if(cardType != 'Amex') {
         return false;
     }
 }
 else if(s.length == 3) {
     if(cardType == 'Amex') {
         return false;
     }
 }
 return true;
}

// Is creditcard number value
// Return true is is valid, false otherwise
function cardnumberIsValid(s) {

 if(s == '4321')
     return true;

 // remove non-numerics
 var v = "0123456789";
 var w = "";
 for (var i=0; i < s.length; i++) {
     x = s.charAt(i);
     if (v.indexOf(x,0) != -1)
         w += x;
 }
 // validate number
 j = w.length / 2;
 if (j < 6.5 || j > 8 || j == 7) return false;
 k = Math.floor(j);
 m = Math.ceil(j) - k;
 c = 0;
 for (i=0; i<k; i++) {
     a = w.charAt(i*2+m) * 2;
     c += a > 9 ? Math.floor(a/10 + a%10) : a;
 }
 for (i=0; i<k+m; i++) c += w.charAt(i*2+1-m) * 1;
 return (c%10 == 0);
}

function validateZip(s) {
 if(s.length == 5) {
     return true;
 }
 else {
     return false;
 }
}

// Return error message, or null if is valid
function validateEmail(s) {
 s = s.trim();
 if (s.length == 0) {
     return "Please enter an email address.";
 }

 // DO NOT ALLOW www to be part of email...
 if(s.toLowerCase().indexOf("www.") > -1) {
     return "Sorry, but we do not accept email addresses containing \"www.\"";
 }
 // changed at end to not allow trailing spaces
 regExp = /^[A-Z0-9._%-]+@(?:[A-Z0-9-]+\.)+[A-Z]{2,4}$/i;
 if (s.match(regExp)) {
     return null;
 }
 else {
     return "Not a valid email address.";
 }
}


function doSignup() {
   
   var formObject = document.getElementById('sub_form'); 
   YAHOO.util.Connect.setForm(formObject); 

        var requestCallback = {
        success: function(o) {
           YAHOO.cm.signup_progress.destroy();
           signupComplete(o.responseText);
        },
        failure: function(o) {
            YAHOO.cm.signup_progress.destroy();
            alert('Error performing signup: ' + o.status);
        },
        argument: null
    };

   var cObj = YAHOO.util.Connect.asyncRequest('POST', '/signup_cm', requestCallback);
   
   showProcessingMessage();
}

function doOneTeacherSignup() {
	document.getElementById('selected_services').value = 'TYPE_CATCHUP_MATH_ONE_TEACHER';

	var formObject = document.getElementById('sub_form'); 
	YAHOO.util.Connect.setForm(formObject); 

	   var requestCallback = {
	   success: function(o) {
	           YAHOO.cm.signup_progress.destroy();
	           oneTeacherComplete(o.responseText);
	        },
	        failure: function(o) {
	            YAHOO.cm.signup_progress.destroy();
	            alert('Error performing signup: ' + o.status);
	        },
	        argument: null
	    };

	   var cObj = YAHOO.util.Connect.asyncRequest('POST', '/oneteacher', requestCallback);
	   
	   showProcessingMessage();

}

function doPurchaseOrder() {

	var formObject = document.getElementById('sub_form'); 
	YAHOO.util.Connect.setForm(formObject); 

	var requestCallback = {
	    success: function(o) {
	       YAHOO.cm.signup_progress.destroy();
	       //alert("ret: " + o.responseText);
	       cmPurchaseComplete(o.responseText);
	    },
	    failure: function(o) {
	        YAHOO.cm.signup_progress.destroy();
	        alert('Error performing purchase: ' + o.status);
	    },
	    argument: null
	};

	var cObj = YAHOO.util.Connect.asyncRequest('POST', '/purchase', requestCallback);
	   
	showProcessingMessage();

}

function showProcessingMessage() {
	/** Show signup progress */
	var html = "<p style='margin-top: 15px;font-size: 1em;color: white;'>Please wait while your request is being processed.</p>";
	    
	    YAHOO.cm = new Object();
	    YAHOO.cm.signup_progress = new YAHOO.widget.Panel("signup_progress", {
	       width : "400px",
	       height : "100px",
	       draggable : false,
	       fixedcenter : true,
	       close : false,
	       modal: true
	    });
	    YAHOO.cm.signup_progress.setHeader("<span style='font-size: 1.2em;color: black;'>Processing Catchup Math Request</span>");  
	    YAHOO.cm.signup_progress.setBody(html);
	    YAHOO.cm.signup_progress.setFooter("");
	    YAHOO.cm.signup_progress.render(document.body);
}

function doSelfPaySignup() {
	var formObject = document.getElementById('sub_form');

	YAHOO.util.Connect.setForm(formObject);

	var requestCallback = {
		success : function(o) {
			YAHOO.cm.signup_progress.destroy();
			selfpayComplete(o.responseText);
		},
		failure : function(o) {
			YAHOO.cm.signup_progress.destroy();
			alert('Error performing signup: ' + o.status);
		},
		argument : null
	};

	var cObj = YAHOO.util.Connect.asyncRequest('POST', '/selfpay', requestCallback);

	showProcessingMessage();
}

/** show standard error message */
function showErrorMessage(msg) {
	
	var html = "<div style='text-align: left'>" + msg + "</div>";
	
	YAHOO.cm = new Object();
	YAHOO.cm.errorPanel = new YAHOO.widget.Panel("signup_error", {
	   width : "400px",
	   height : "100px",
	   draggable : true,
	   fixedcenter : true,
	   close : true,
	   modal: true
	});
	YAHOO.cm.errorPanel.setHeader("Signup Error");  
	YAHOO.cm.errorPanel.setBody(html);
	YAHOO.cm.errorPanel.setFooter("");
	YAHOO.cm.errorPanel.render(document.body);
}

// return new string containing
// only the numeric values of s
function removeAllNonNumeric(s) {
 // remove non-numerics
 var v = "0123456789";
 var w = "";
 for (var i=0; i < s.length; i++) {
     x = s.charAt(i);
     if (v.indexOf(x,0) != -1)
         w += x;
 }
 return w;
}


function overlib() {
}

function nd() {
}


function showAlert(title, msg) {
    var mySimpleDialog = new YAHOO.widget.SimpleDialog("dlg", { 
	                 width: "20em", 
                     effect:{effect:YAHOO.widget.ContainerEffect.FADE,  duration:0.25 }, 
		             fixedcenter:true,
                     width:150,height:150,
		             modal:true,
                     visible:false,
                     draggable:false 
    });

    msg = "<p style='width: 300px;margin-top: 10px; padding: 5px;text-align: left;'>" + msg + "</p>";

    mySimpleDialog.setHeader(title);
    mySimpleDialog.setBody(msg);
    mySimpleDialog.cfg.setProperty("icon",YAHOO.widget.SimpleDialog.ICON_WARN); 
    
    mySimpleDialog.render(document.body); 


    mySimpleDialog.show();
}

function signupComplete(data) {

   if(data.indexOf("error") == 0) {
       showAlert('Signup Problem','Error while purchasing Catchup Math: ' + data.substring(6));
       return;
   }

   var obj = eval('(' + data + ')');

   var cmKey = obj.key;  // login security key
   var userId = obj.uid;
   var userName = obj.userName;
   var password = obj.password;

   var email = $get('parent_email').value;
   var html = "<h1>Catchup Math Signup Success</h1><p><b>Congratulations!</b><br/>You have successfully signed up for Catchup Math.</p>" +
               "<p>Your login information is: <br/>" +
               "<div class='login-info'>" +
               "<div class='col'>Login Name: </div><div class='val'>" + userName + "</div>" +
               "<div class='col'>Password: </div><div class='val'>" + password + "</div>" +
               "</div>" +
               "</p>" + 
              "<p>Visit <a href='http://catchupmath.com'>http://catchupmath.com</a>" +
              " and enter the login information shown above.</p>" +
              "<p class='info-sent'>Your signup information has also been sent to the email address: " + email + "</p>" +
              "<p class='info-sent'>If you do not receive your account email within a few minutes, please check your spam folder. " +
              "If not there, please email <a href='mailto:support@catchupmath.com'>support@catchupmath.com</a></p><br/>" +
              "<p><a href='/loginService?uid=" + userId + "'>Begin Using Catchup Math</a></p>"; 

   showSignupSuccess(html);
}
   
function selfpayComplete(data) {

   if(data.indexOf("error") == 0) {
       showAlert('Signup Problem','Error while purchasing Catchup Math: ' + data.substring(6));
       return;
   }

   var obj = eval('(' + data + ')');

   var html;
   var errorMsg = obj.error;
   if (errorMsg == null) {

   var cmKey = obj.key;  // login security key
   var userId = obj.uid;
   var loginName = obj.loginName;
   var password = obj.password;

   var email = $get('student_email').value;
   html = "<h1>Catchup Math Signup Success</h1><p>Congratulations!  You have successfully signed up for Catchup Math.</p>" +
       "<p>Your personal login information is: " +
       "<div class='login-info'>" +
       "<div class='col'>Login Name: </div><div class='val'>" + loginName + "</div>" +
       "<div class='col'>Password: </div><div class='val'>" + password + "</div>" +
       "</div>" +
       "</p>" + 
       "<p>Visit <a href='http://catchupmath.com'>http://catchupmath.com</a>" +
       " as directed by your instructor and enter the login information shown above.</p>" +
       "<p class='info-sent'>A confirmation has also been emailed to: " + email + "</p>" +
       "<p class='info-sent'>If you do not receive your account email within a few minutes, please check your spam folder. " +
       "If not there, please email <a href='mailto:support@catchupmath.com'>support@catchupmath.com</a></p><br/>" +
       "<p>Thank you!</p>";
   }
   else {
	   html = "<h1>Catchup Math Signup Error</h1><p><b>Unfortunately, there was a problem processing your request.</b><br/></p>" +
       "<p>Error message: <br/>" + errorMsg + "</p>";
   }

   showSignupSuccess(html);
}

function oneTeacherComplete(data) {

	if (data.indexOf("error") == 0) {
		showAlert('Signup Problem', 'Error while purchasing Catchup Math: '
				+ data.substring(6));
		return;
	}

	var obj = eval('(' + data + ')');

	var html;
	var errorMsg = obj.error;
	if (errorMsg == null) {

		var userName = obj.userName;
		var password = obj.password;
		//var email    = obj.email;
		var school   = obj.school;
		var expires  = obj.expires;

		html = "<h1>Catchup Math Signup Success</h1>" 
			    +"<p>Thank you for your order of a One-Teacher Catchup Math License for up to 49 students at " + school + "!</p>"
				+ "<p><div class='login-info'>"
				+ "<div class='col'>Login Name: </div><div class='val'>"
				+ userName
				+ "</div>"
				+ "<div class='col'>Password: </div><div class='val'>"
				+ password
				+ "</div>"
				+ "<div class='col'>Expires: </div><div class='val'>"
				+ expires
				+ "</div>"
				+ "</div>"
				+ "</p><br/>"
				+ "<p>We are more than happy to provide support by telephone and email.</p>"
				+ "<p>We wish you and your students the very best success with our service!</p>"
				+ "<p>Thank you!<br/>The team from Catchupmath.com</p>"
				+ "<p>Making Math Education Easier!</p>"
	} else {
		html = "<h1>Catchup Math Signup Error</h1><p><b>Unfortunately, there was a problem processing your request.</b><br/></p>"
				+ "<p>Error message: <br/>" + errorMsg + "</p>";
	}

    showSignupSuccess(html);
}

function cmPurchaseComplete(data) {

    if (data.indexOf("error") == 0) {
        showAlert('Purchase Problem', 'Error while purchasing Catchup Math: ' + data.substring(6));
        return;
    }

    var obj = eval('(' + data + ')');
    var html;

    var repName = obj.repName;
    var repEmail = obj.repEmail;
    var isSuccess = obj.isSuccess;
    var school = obj.school;

    if (isSuccess == 'true') {
        html = "<h1>Catchup Math Purchase Success</h1>"
             + "<p>Thank you for your Catchup Math purchase for " + school + "!</p>"
             + "<p>Your account manager will contact you shortly or you may <a href='/contact.html'>contact</a> "
             + "them now.</p>";
    }
    else {
        html = "<h1>Catchup Math Purchase</h1>"
             + "<p>Thank you for attempting to order Catchup Math for " + school + "</p>"
             + "<p>Unfortunately, the credit card was not approved.</p>"
             + "<p>Please <a href='#' onclick='showSignupPage();return false;'>try again</a> "
             + "or <a href='/contact.html'>contact</a> your account manager, to discuss.</p>";
    }

    showSignupSuccess(html);
}

function showSignupPage() {
     var e1 = document.getElementById('signup_page');
     e1.setAttribute('style', 'display:block');
     var e2 = document.getElementById('signup_success');
     e2.setAttribute('style', 'display:none');
     window.scrollTo(0, 0);
}

function showSignupSuccess(html) {
     var e1 = document.getElementById('signup_page');
     e1.setAttribute('style', 'display:none');
     var result = document.getElementById('signup_success');
     result.innerHTML = html;
     result.setAttribute('style', 'display:block');
     window.scrollTo(0, 0);
}

function setTotalCost(cost) {
   _totalCost = cost;
   var e = $get('service-total');
   if (e) e.innerHTML = 'Total: $' + _totalCost + '.00';
}

function payNowToggle(chkbox) {
    if (chkbox.checked) {
        enablePayNow(true);
    }
    else {
        enablePayNow(false);
    }
}

function enablePayNow(enable) {
	var e1 = document.getElementById('payNow_div');
	var e2 = document.getElementById('card_number');
	var e3 = document.getElementById('card_ccv2');
	var e4 = document.getElementById('first_name');
	var e5 = document.getElementById('last_name');
	var e6 = document.getElementById('address1');
	var e7 = document.getElementById('city');
	var e8 = document.getElementById('zip');
	var e9 = document.getElementById('paynow_ckbx');
	payNowEnabled = enable;
	if (enable == true) {
    	e1.setAttribute('style', 'display:block');
    	e2.attributes.required = "required";
    	e3.attributes.required = "required";
    	e4.attributes.required = "required";
    	e5.attributes.required = "required";
    	e6.attributes.required = "required";
    	e7.attributes.required = "required";
    	e8.attributes.required = "required";
    	e9.setAttribute('title', 'Uncheck to pay later');
    }
    else {
     	e1.setAttribute('style', 'display:none');
    	e2.attributes.required = "not-required";
    	e3.attributes.required = "not-required";
    	e4.attributes.required = "not-required";
    	e5.attributes.required = "not-required";
    	e6.attributes.required = "not-required";
    	e7.attributes.required = "not-required";
    	e8.attributes.required = "not-required";
    	e9.setAttribute('title', 'Click to pay now');
    }
}

function calcOrderTotal() {
    var totalOrder = $get('total_order');
    totalOrder.value = '';
    clearErrorMessages();

    if (verifyNumericFldsPO() == false)
        return;

	var fld = $get('license_fee');
    var licenseFee = 1 * fld.value;
    var addlSchlFee = calcAddlSchFee();
    var profDevFee = 1 * $get('pd_days_fee').value;
    var total = 0;
    total = licenseFee + profDevFee + addlSchlFee;
    //alert('total: ' + total);
    totalOrder.value = '$' + total;
}

function updateLicenseCost() {
    var fee = calculateFee();
    var licenseFee = $get('license_fee');
    if (fee != 0) licenseFee.value = '$' + fee;
    else licenseFee.value = '$0';
    updateTotalOrder();   
}

function updateProfDevCost() {
    var fee = calcProfDevFee();
    var profDevFee = $get('pd_days_fee');
    if (fee != 0) profDevFee.value = "$" + fee;
    else profDevFee.value = '';
    updateTotalOrder();   
}

function calcProfDevFee() {
    //var numPdDays = $get('num_pd_days');
    //if (numPdDays.value != "")
    //    return numPdDays.value * 1500;
    var fee = 1 * $get('pd_days_fee').value;
    return fee;
}

function updateAddlSchoolsCost() {
    var fee = calcAddlSchFee();
    var addlSchFee = document.getElementById('addl_schools_fee');
    if (fee != 0) addlSchFee.value = "$" + fee;
    else addlSchFee.value = '';
    updateTotalOrder();   
}

function calcAddlSchFee() {
    //var numSchools = $get('num_schools');
    //if (numSchools.value != "")
    //    return numSchools.value * 250;
	var addlSchlFee = 1 * $get('addl_schools_fee').value;
    return addlSchlFee;
}

function updateTotalOrder() {
    var fee = calculateFee() + calcAddlSchFee() + calcProfDevFee();
    var totalOrder = $get('total_order');
    if (fee != 0) totalOrder.value = '$' + fee;
    else totalOrder.value = '$0';
}

function calculateFee() {
    var numStudents = $get('num_students');
    var numYears = $get('num_years');
    if (numStudents.value == "" || numYears.value == "") {
        return 0;
    }
    return calcFee(numStudents.value, numYears.value);
}

function calcFee(nStudents, nYears) {
    var fee;
    if (nStudents <= 50) {
        if (nYears == 1) {
            fee = nStudents * 15;
        }
        else if (nYears == 2) {
            fee = 599;
        }
        else if (nYears == 3) {
            fee = 849;
        }
        return fee;
    }

    else if (nStudents <= 100) {
        if (nYears == 1) {
            fee = 599;
        }
        else if (nYears == 2) {
            fee = 1099;
        }
        else if (nYears == 3) {
            fee = 1599;
        }
        return fee;
    }

    else if (nStudents < 1000) {
        if (nYears == 1) {
            fee = 4.99;
        }
        if (nYears == 2) {
            fee = 4.75;
        }
        if (nYears == 3) {
            fee = 4.49;
        }
        return fee * nStudents;
    }
    else if (nStudents < 5000) {
        if (nYears == 1) {
            fee = 4.75;
        }
        if (nYears == 2) {
            fee = 4.49;
        }
        if (nYears == 3) {
            fee = 4.25;
        }
        return fee * nStudents;
    }

    else {
        if (nYears == 1) {
            fee = 4.49;
        }
        if (nYears == 2) {
            fee = 4.25;
        }
        if (nYears == 3) {
            fee = 3.99;
        }
        return fee * nStudents;
    
    }

}
