/** create way to have client side data
 * 
 */
var Database = (function() {
	var _db=null;
	var _bookIndexes = {};
	return {
		initializeBooks: function(json) {
			_db = eval('(' + json + ')');
		},
	
	    getBookAt: function(which) {
	        return _db[which];  
	    },
	    
	    getBookCount: function() {
	    	return _db.length;
	    },
	    
	    initializeBookIndex: function(textCode, json) {
	    	var bookProbs = eval('(' + json + ')');
	    	_bookIndexes[textCode, bookProbs];
	    	
	    	return bookProbs;
	    },
	    
	    getBookIndex: function(textCode) {
	    	return _bookIndexes[textCode];
	    }
	}
}());