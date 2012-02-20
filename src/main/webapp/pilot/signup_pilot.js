  function checkForm() {
	  clearErrorMessages();
	  
	  haveError = false;
	  fld = $get('fld_title');
	  if(fld.value == "none") {
	      if(showError(fld, "Please specify your title.")) {
	          haveError = true;
	      }
	  }
	  fld = $get('fld_name');
	  if(fld.value == "") {
	      if(showError(fld, "Please specify your name.")) {
	          haveError = true;
	      }
	  }
	  fld = $get('fld_school');
	  if(fld.value == "") {
	      if(showError(fld, "Please specify your school or district.")) {
	          haveError = true;
	      }
	  }
	  fld = $get('fld_student_count');
	  if(fld.value == "") {
	      if(showError(fld, "Please specify the approximate number of students enrolled in your school or district.")) {
	          haveError = true;
	      }
	  }
	  if(isNaN(fld.value) || Number(fld.value) < 200) {
	      if(showError(fld, "Sorry, Catchup Math pilots are only available to schools and colleges with total enrollment of 200 or more.")) {
	          haveError = true;
	      }
	  }
	  fld = $get('fld_zip');
	  if(fld.value == "") {
	      if(showError(fld, "Please specify your zip code.")) {
	          haveError = true;
	      }
	  }	  
	  else if(!validateZip(fld.value)) {
	        if(showError(fld, "This is not a valid zip code (if outside U.S., use 99999)"))
	            haveError = true;
      }

	  fld = $get('fld_email');
	  if(fld.value == "") {
	      if(showError(fld, "Please specify your email.")) {
	          haveError = true;
	      }
	  }
	  var errMsg = validateEmail(fld.value);
	  if(errMsg != null) {
		  if(showError(fld, errMsg))
	            haveError = true;		  
	  }

	  fld = $get('fld_phone');
	  if(fld.value == "") {
	      if(showError(fld, "Please specify your phone number.")) {
	          haveError = true;
	      }
	  }
	  
	  fld = $get('fld_password_prefix');
	  if(fld.value == "" || fld.value.length < 3 || fld.value.length > 4 || !isAlphabet(fld.value)) {
	      if(showError(fld, "Please specify 3 or 4 letters that will be used as the prefix for your password.")) {
	          haveError = true;
	      }
	  }	  

	  fld = $get('fld_additional_emails');
	  if (fld.value != '') {
		  var errMsg = checkAdditionalEmails(fld.value);
		  if (errMsg != null) {
			  if(showError(fld, errMsg))
		            haveError = true;		  
		  }
	  }
	  
	  if (haveError == false)
		  doSignup();
	  
	  return false;   // always return false;
  }

  function isAlphabet(v){
		var alphaExp = /^[a-zA-Z]+$/;
		return v.match(alphaExp);
	}  
  
  function showSuccess() {
	  $get('email-address').innerHTML = $get('fld_email').value; 
	  $get('signup_panel').style.display = 'none';
	  $get('success_panel').style.display = 'block';
  }  

  function showSuccess() {
	  
	  $get('email-address').innerHTML = $get('fld_email').value; 
	  $get('signup_panel').style.display = 'none';
	  $get('success_panel').style.display = 'block';
	  window.scroll(0,0);
  }


  function doSignup() {
	   var formObject = document.getElementById('signup_form'); 
	   YAHOO.util.Connect.setForm(formObject); 

	    var requestCallback = {
	        success: function(o) {
	           YAHOO.cm.signup_progress.destroy();
	           showSuccess(o.responseText);
	        },
	        failure: function(o) {
	            YAHOO.cm.signup_progress.destroy();
	            var rt = o.responseText;
	            var msg="";
	            if(rt.indexOf("Duplicate") > -1)
	            	msg = "Duplicate record exists.  Please contact our sales staff for pilot setup.";
	            else 
	            	msg = "Could not create pilot";
	            
	            showErrorMessage(msg);
	        },
	        argument: null
	    };
	   var cObj = YAHOO.util.Connect.asyncRequest('POST', '_pilot_setup.jsp', requestCallback);
	   showProcessingMessage();
  }

  function checkAdditionalEmails(emails) {
	  var emailArray = emails.split("\n");
	  for (i in emailArray) {
		  if (emailArray[i] != '') {
			  var errMsg = validateEmail(emailArray[i]);
			  if(errMsg != null) {
				  return errMsg + ": " + emailArray[i];
			  }
		  } 
	  }
	  return null;
  }
