/** Initialize system
 *  Might be modified in 
 *  individual pages */
var CmPage = {
        menuItem:-1,
        sticky:[]
};

function $get(x) {
    return document.getElementById(x);
}


function setupPage() {
    /** setup menu buttons
     * 
     */
    YUI().use('node', function(Y) {
        
        Y.on('domready',function() {
            
            if(CmPage.menuItem > -1) {
                /** Set the selected menu button */
                var menuButtons = Y.one('#menubar2').all('a');
                _mainMenuItemObject = menuButtons.item(CmPage.menuItem);
                _mainMenuItemObject.removeClass('notselected');
                _mainMenuItemObject.removeClass('sexy_cm_gray');				
                _mainMenuItemObject.addClass('sexy_cm_green');
                _mainMenuItemObject.addClass('sexybutton');
            }
                
            /** Setup the sticky nodes area with two random
             *  quotes from QuoteManager.
             *  
             */
            var postIt = Y.one('#postit');
            if(postIt) {
                if(CmPage.sticky.length == 2) {
                    var html= formatQuote(Quotes[CmPage.sticky[0]]);
                    html += formatQuote(Quotes[CmPage.sticky[1]]);
                    postIt.set('innerHTML',html);
                }
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
    var closeHead = '<a href="#" onclick="closeMonaVideo();return false" class="close">X</a>';
    
    var closeFoot = '<div style="text-align: center;margin-top: 25px;">' +
                 '    <a class="sexybutton sexysimple" href="#" onclick="closeMonaVideo();return false;">Close</a>' +
                 '</div>';
    
    
    var html = closeHead + 
            '<iframe src="/motivational_video/" width="100%" height="250px" scrolling="no" frameborder="no"></iframe>' +
            closeFoot;
    
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
    _overlay.set("bodyContent", "");  // make sure video stops
    _overlay.hide();
}


        function formatQuote(quote) {
            
            var url = quote.link_type == 'success'?'/success.html':'/research.html';
            var readMore = (quote.link_type == 'success')?'Read more testimonials':'Read more research';
            return '<p>' +
                       quote.text +
                       '<div class="credit">' + quote.link_text + '</div><a href="' + url + 
                       '"> ' + readMore + '</a>' + 
                   '</p>';
        }        



var Quotes = 
    [
     {
         text:"A test-reteach-retest model that targets and remediates individual skill weaknesses dramatically accelerates learning.",
         link_text: 'Bloom, B.S.  (1984)',
         link_type:'research'
     }
     ,
     {
         text:"A new student initially tested into our lowest math course. Not satisfied, she used Catchup Math for three weeks and then retested four course levels higher --- a full year and a quarter.",
         link_text: 'College Instructor, Ohio',
         link_type:'success'             
     },
     {
         text:"I've never done so well in a math class before.",
         link_text: ' Kentucky',
         link_type:'success'             
     },
     {
         text:"Technology-infused Interventions ... were the top model predictors of improved high stakes test scores, dropout rate reduction, and improved discipline.",
         link_text: ' Greaves',
         link_type:'research'             
     },         
     {
         text:"It worked so well over the last 3 weeks that my students are all caught up and doing their regular work now.",
         link_text: 'New York',
         link_type:'success'             
     }, 
     {
         text:"Instructional Software is proven to improve learning.",
         link_text: ' National Math Panel',
         link_type:'research'             
     }, 
     {
         text:"Use of multi-media to provide alternate forms of representation improves effectiveness.",
         link_text: '  Mayer',
         link_type:'research'             
     }
 ];
