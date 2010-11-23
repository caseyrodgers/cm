function setupPageLocal() {
    /** hookup onclick listeners to 
        each of the six large buttons
    */
}

function moveToLocation(el) {
  var dest = el.getAttribute('dest');
  document.location.href = dest;
}