function $get(x) {
	return document.getElementById(x);
}

function setTutoringPage(pageNum) {
	var imgHtml;
	var p1 = $get('pg1');
	var p2 = $get('pg2');
	var p3 = $get('pg3');

	if (pageNum == 1) {
		var imgHtml = "<img src='/images/tutor/tutor-xmpl-1-of-3-540x440.gif' alt='Tutoring Example' width='540' height='440' />";
		p1.innerHTML = '<b>1</b>';
		p2.innerHTML = "<a href='#' onclick='setTutoringPage(2);'>2</a>";
		p3.innerHTML = "<a href='#' onclick='setTutoringPage(3);'>3</a>";
		setDialogScrollPosition('dialogPage1');
	}
	if (pageNum == 2) {
		var imgHtml = "<img src='/images/tutor/tutor-xmpl-2-of-3-540x440.gif' alt='Tutoring Example' width='540' height='440' />";
		p2.innerHTML = '<b>2</b>';
		p1.innerHTML = "<a href='#' onclick='setTutoringPage(1);'>1</a>";
		p3.innerHTML = "<a href='#' onclick='setTutoringPage(3);'>3</a>";
		setDialogScrollPosition('dialogPage2');
	}
	if (pageNum == 3) {
		var imgHtml = "<img src='/images/tutor/tutor-xmpl-3-of-3-540x440.gif' alt='Tutoring Example' width='540' height='440' />";
		p3.innerHTML = '<b>3</b>';
		p1.innerHTML = "<a href='#' onclick='setTutoringPage(1);'>1</a>";
		p2.innerHTML = "<a href='#' onclick='setTutoringPage(2);'>2</a>";
		setDialogScrollPosition('dialogPage3');
	}
	var d = $get('tutoring-example-page');
	d.innerHTML = imgHtml;
}

//Find y value of given object
function findPos(obj) {
	var curtop = 0;
	if (obj.offsetParent) {
		do {
			curtop += obj.offsetTop;
		} while (obj = obj.offsetParent);
		//alert('curtop: ' + curtop);
		return [ curtop ];
	}
}

function setDialogScrollPosition(id) {
	var dialogPageObj = $get(id);
	var dialogDiv = $get('dialog');

	//Scroll to location of dialogPageObj
	dialogDiv.scrollTop = findPos(dialogPageObj) - findPos($get('dialogPage1'));
}

setTutoringPage(1);
