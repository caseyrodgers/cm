

function showTutoringExample() {
	var html = '<iframe height="550" width="750" frameborder="no" src="/live-online-tutoring-example.html"></iframe>';
	showInfoWindow("Tutoring Example", html);
}

function showInfoWindow(title, infoMessage) {
	var closer = '<a id="closer" href="#" onclick="YAHOO.infoWin.destroy();return false">Close</a>';
	infoMessage = closer + infoMessage;

	YAHOO.infoWin = new Object();
	YAHOO.infoWin = new YAHOO.widget.Panel("info-window", {
		width : "775px",
		height : "auto",
		draggable : true,
		fixedcenter : true,
		close : true
	});

	YAHOO.infoWin.setHeader(title);
	YAHOO.infoWin.setBody(infoMessage);
	YAHOO.infoWin.setFooter("");
	YAHOO.infoWin.render("bd");

	YAHOO.infoWin.show();
}
