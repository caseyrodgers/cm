function fixupReportForPrint(element, widgetType) {
	
	var widgets = $('[name=hm_flash_widget]', element);
	for(var i=0;i<widgets.size();i++) {
		var widget = widgets.get(i);
		widget.innerHTML = getHtmlForWidget(widgetType);
		widget.setAttribute('name','');
		widget.setAttribute('class','widget_print');
	}
}


function getHtmlForWidget(widgetType) {
	  switch(widgetType) {
	  case "widget_plot":
	  case "xy":
		  return "<img src='/assets/images/x-y-axes.png'>";
	  case "x":
		  return "<img src='/assets/images/number-line.png'>"; 
    default:
  	  return "<div class='assign_prob_ans'>Answer:</div> "; // "<img src='/assets/images/underline.png'>";
	  }
	  
}