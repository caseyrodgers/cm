  function checkForm() {
	  clearErrorMessages();
	  
	  haveError = false;
	  fld = $get('fld_title');
	  alert('fld_title: ' + fld);
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
	  if(fld.value == "" || fld.value.split(' ').join('') == "") {
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
	  if(isNaN(fld.value) || fld.value < 1 || (10*parseInt(fld.value,10) != parseInt(10*fld.value,10)) ||
	     Number(fld.value) < 200) {
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

  var k12Select = "Title<br/>" +
                  "<select type='text' name='fld_title' id='fld_title' class='input_field' tabindex='1'>" +
                  "    <option value='none'>--- select title ---</option>" +
                  "    <option value='Math Teacher'>Math Teacher</option>" +
                  "    <option value='Intervention/RTI Specialist'>Intervention/RTI Specialist</option>" +
                  "    <option value='Special Ed Teacher'>Special Ed Teacher</option>" +
                  "    <option value='School Administrator'>School Administrator</option>" +
                  "    <option value='District Administrator'>District Administrator</option>" +
                  "    <option value='Other K-12'>Other</option>"+
                  "</select>";

  var collegeSelect =  "Title<br/>" +
  	                   "<select type='text' name='fld_title' id='fld_title' class='input_field' tabindex='1'>" +
                       "    <option value='none'>--- select title ---</option>" +
                       "    <option 'Math Professor/Instructor'>Math Professor/Instructor</option>" +
                       "    <option value='Learning Center Administrator'>Learning Center Administratorr</option>" +
                       "    <option value='Other K-12'>Other</option>"+
                       "</select>";

  function radioClicked(type) {
	  var e = $get("title_div");
	  if (type == 'k12') {
		  e.innerHTML = k12Select;
	  }
	  else {
		  e.innerHTML = collegeSelect;
	  }
  }

