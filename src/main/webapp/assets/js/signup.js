var _card_what_msg='<p class="tooltip" >The Card Verification Number is a 3-digit number printed on the back of the card, except for Amex it is a 4-digit number on the front of the card.</p>';

var _terms_msg = '<p class="tooltip">Please review our sample session before buying, as the purchase price (as well as any live tutoring purchased) is non-refundable.  Subscription is for use by one person, and may be suspended if abused. We provide our service as-is and when-available. Compensation for claims you may have (including consequential and punitive damages) shall be limited to the amount you have paid for Catchup Math services.</p>';


function setupSignupPage() {
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
   
   
   setCmSelection();
   
   
   var e1 = document.getElementById('TYPE_SERVICE_CATCHUP');
   e1.onclick = function() {setCmSelection()};


   var e2 = document.getElementById('TYPE_SERVICE_CATCHUP_YEAR');
   e2.onclick = function() {setCmSelection();};
}


var _plan;
function setCmSelection() {
   var e1 = document.getElementById('TYPE_SERVICE_CATCHUP');
   var e2 = document.getElementById('TYPE_SERVICE_CATCHUP_YEAR');
   
   var ew;
   if(e1.checked) {
	   e2.checked = false;
       ew = e1;
   }
   else {
	   e1.checked = false;
	   ew = e2;
   }
   
   _plan = ew.id;
   
   setTotalCost(ew.getAttribute("cost"));
   
   document.getElementById('selected_services').value = _plan;
}



var _validationErrorCount=0;
var _totalCost=99;
function checkForm() {

    clearErrorMessages();
    
    fld = $get('parent_email');
    if(fld.value == "") {
        if(showError(fld, "Please specify your email.")) {
            return false;
        }    }
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

function checkCreditCard() {
    fld = $get('sel_cardtype');
    if(fld.selectedIndex < 2) {
        if(showError(fld, "Which type of credit card?"))
            return false;

    }
    var cardType = $get('sel_cardtype').value;

    fld = $get('card_number');
    var cardNumOnlyNums = removeAllNonNumeric(fld.value);
    if(!cardNumOnlyNums) {
        if(showError(fld, "What is your card number."))
            return false;
    }
    else if(!cardnumberIsValid(cardNumOnlyNums)) {
        if(showError(fld, "Invalid credit card number."))
            return false;
    }


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


function showError(fld, msg) {
    _validationErrorCount++;
    divs = fld.parentNode.getElementsByTagName("div");
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

    fld.focus();
    return true;
}



// remove any messages
function clearErrorMessages() {
 // turn off all error message
 _validationErrorCount=0;
 fields = document.getElementsByTagName("div");
 for(f = 0, t = fields.length; f < t;f++) {
     fields[f].style.border = "none";
     if(fields[f].className == 'input_error') {
         fields[f].style.display = 'none';
     }
 }
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
 for (i=0; i < s.length; i++) {
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
     return "Please enter an email address";
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
     return "Not a valid email address";
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
 for (i=0; i < s.length; i++) {
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
              "<p><a href='/loginService?uid=" + userId + "'>Begin Using Catchup Math</a></p>";
    
    
    YUI().use("node", function(Y) {
       Y.one("#signup_page").setStyle('display', 'none');
       var success = Y.one('#signup_success');
       success.set('innerHTML', html);
       success.setStyle('display', 'block');
       window.scrollTo(0,0);
    });
}
   
function setTotalCost(cost) {
   _totalCost = cost;
   $get('service-total').innerHTML = 'Total: $' + _totalCost + '.00';
}
