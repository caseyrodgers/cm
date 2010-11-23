function setupPageLocal() {
    YUI().use("event-mouseenter", function(Y) {
        
        Y.on("mouseenter", function (e) {
            this.all('p').addClass("info-box-selected");
         }, ".info-box-wrapper");
     
        Y.on("mouseleave", function (e) {
            this.all('p').removeClass("info-box-selected");
        }, ".info-box-wrapper");
     
    });    
}

function moveToLocation(el) {
  var dest = el.getAttribute('dest');
  document.location.href = dest;
}