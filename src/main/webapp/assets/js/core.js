function $get(x) {
    return document.getElementById(x);
}


var _mainMenuItemObject=null;

function setupPage() {
    /** setup menu buttons
     * 
     */
    YUI().use('node', function(Y) {
        
        Y.on('domready',function() {
            
            if(_mainMenuItem > -1) {
                /** Set the selected menu button */
                var menuButtons = Y.one('#menubar2').all('a');
                _mainMenuItemObject = menuButtons.item(_mainMenuItem);
                _mainMenuItemObject.removeClass('notselected');
                _mainMenuItemObject.addClass('sexy_cm_green');
                _mainMenuItemObject.addClass('sexybutton');
            }
                
            /** Setup the sticky nodes area with two random
             *  quotes from QuoteManager.
             *  
             */
            var postIt = Y.one('#postit');
            if(postIt) {
                var quote1 = QuoteManager.getRandomQuoteNotMatching(null);
                var html = QuoteManager.formatQuote(quote1);
                html += QuoteManager.formatQuote(QuoteManager.getRandomQuoteNotMatching(quote1));
                postIt.set('innerHTML',html);
            }
            
           setupPageLocal();
     });
    });
 }

function setupPageLocal() {
    /** empty */
}

YUI().use('node',function(Y) {
    Y.on('domready',function() {
        setupPage();
    });
});



var _overlay=null;

function showMonaMotivationalVideo() {
    var closer = '<div style="text-align: center;margin-top: 25px;">' +
                 '<a class="sexybutton sexysimple" href="#" onclick="closeMonaVideo();return false;">Close</a></div>';
    var html = '<iframe src="/motivational_video/" width="100%" height="250px" scrolling="no" frameborder="no"></iframe>'
            + closer;
    
    YUI().use('anim','overlay',
                    function(Y) {
                        _overlay = new Y.Overlay(
                                {   
                                    id:"mona-video",
                                    width : "350px",
                                    height : "350px",
                                    centered : true,
                                    headerContent : "Catchup Math Motivational Video",
                                    bodyContent : html,
                                    zIndex : 2,
                                    visible:true
                                });
                        
                       _overlay.render();
    });
}

/** Closed from close button */
function closeMonaVideo() {
    _overlay.hide();
}




var Quotes = 
    [
     {
         text:"A test-reteach-retest model that targets and remediates individual skill weaknesses dramatically accelerates learning.",
         link_text: 'Bloom, B.S.  (1984)',
         link_url:'/research.html'
     }
     ,
     {
         text:"A new student initially tested into our lowest math course. Not satisfied, she used Catchup Math for three weeks and then retested four course levels higher --- a full year and a quarter.",
         link_text: 'College Instructor, Ohio',
         link_url:'/success.html'             
     },
     {
         text:"I've never done so well in a math class before.",
         link_text: ' Kentucky',
         link_url:'/success.html'             
     },
     {
         text:"Technology-infused Interventions ... were the top model predictors of improved high stakes test scores, dropout rate reduction, and improved discipline.",
         link_text: ' Greaves',
         link_url:'/research.html'             
     },         
     {
         text:"It worked so well over the last 3 weeks that my students are all caught up and doing their regular work now.",
         link_text: 'New York',
         link_url:'/success.html'             
     }, 
     {
         text:"Instructional Software is proven to improve learning.",
         link_text: ' National Math Panel',
         link_url:'/research.html'             
     }, 
     {
         text:"Use of multi-media to provide alternate forms of representation improves effectiveness.",
         link_text: '  Mayer',
         link_url:'/research.html'             
     }
 ];

/** return a random quote object:
 *    quote.text == text of the quote
 *    
 *    
 *  if quoteMustBeDifferentThan is specified
 *  then the return quote must be different
 *  to make sure caller does not end up with 
 *  same quote twice.
 *  
 */
var QuoteManager = {
        getRandomQuoteNotMatching: function(quoteMustBeDifferentThan) {
            var max = Quotes.length;
            
            var MAXCHECK=5;
            var check=0;
            while(check < MAXCHECK) {
                var rand = Math.floor(Math.random()*max);
                var quote = Quotes[rand];
                
                if(quoteMustBeDifferentThan == quote) {
                    check++;
                    continue; // try again
                }
                else {
                    return quote;
                }
            }
            
            /** if fall off end, simply return the first Quote */
            return Quotes[0];
        },
        formatQuote:function(quote) {
            return '<p>' +
                       quote.text +
                       '<a href="' + quote.link_url + '">' + quote.link_text + '</a>' + 
                   '</p>';
        }        
};