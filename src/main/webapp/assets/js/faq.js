
/** Setup listeners on all faq-items
 * 
 * Each FAQ item should have a class of 'faq-item'
 * This item must contain exactly two children:
 *   1. A title
 *   2. The FAQ text (any HTML)
 */
function setupPageLocal() {
	 YUI().use('node', function(Y) {
		 	var faqList = Y.all('.faq-item');
			var faqItemContents = Y.all('.faq-item-content');
			
  	 	    faqList.on('click',function(x){
 	    	    var faqItemContents = Y.all('.faq-item-content').addClass('hide');
				var tarNode = x.target;
                var displayNode = tarNode.get('parentNode').get('parentNode').one('.faq-item-content');
				displayNode.removeClass('hide');
				displayNode.addClass('display');
		    });
	 });
}
/** Display a single FAQ item
 * 
 * @param thep
 * @param items
 */
function showFaqElement(thep,items) {
	for ( var i = 0; i < items.length; i++) {
		items[i].style.display = (items[i] == thep) ? 'block' : 'none';
	}
}


function junk() {

	var faqItems = YAHOO.util.Dom.getElementsByClassName('faq-item');
	var faqItemContents = YAHOO.util.Dom.getElementsByClassName('faq-item-content');
	for ( var i = 0; i < faqItems.length; i++) {
		var item = faqItems[i];
	
		faqItems[i].onclick = function(x) {
			// find the content, and show it
			var thisFaqContent = this.getElementsByTagName("div")[0];
			showFaqElement(thisFaqContent,faqItemContents);
			return false;
		};
	}
}