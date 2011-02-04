// UTILITY FUNCTIONS START ///////////////////////////////////////
function regExpMatch(reg,str) {
  var re = new RegExp(reg);
  if (str.match(re)) {
    return true;
  } else {
    return false;
  }
}
function regExpReplace(reg,str,repstr) {
  var re = new RegExp(reg, "g");
  return str.replace(re, repstr);
}
function prepareForEval(str){
        var inputValue=str      
        if(regExpMatch('[\\w][(]',inputValue)){
            inputValue = inputValue.replace(/([\w])?[(]/g, function($0, $1){return $1 ? $1 + '*(' : $0;});
        }
        if(regExpMatch('\\)[\\w]',inputValue)){
            inputValue = inputValue.replace(/[)](?=[\w])/g, ")*");
        }
        if(regExpMatch('[0-9][a-zA-Z]',inputValue)){        
            inputValue = inputValue.replace(/([0-9])([a-zA-Z])/g, "$1*$2");
        }
        if(regExpMatch('[a-zA-Z][0-9]',inputValue)){        
            inputValue = inputValue.replace(/([a-zA-Z])([0-9])/g, "$1*$2");
        }
        if(regExpMatch('[a-zA-Z][^a-zA-Z]',inputValue)){            
            inputValue = inputValue.replace(/[a-zA-Z](?=([^a-zA-Z]))/g, "x");
        }
        if(regExpMatch('[a-zA-Z]$',inputValue)){            
            inputValue = inputValue.replace(/[a-zA-Z]$/g, "x");
        }
        return inputValue
}

function cleanExtra(str) {
    var _str = String(str).split("*").join("");
    _str = _str.split("(").join("");
    _str = _str.split(")").join("");
    _str = _str.split("-").join("");
    if (_str.indexOf('1.') == -1) {
        if (parseInt(_str) == 1) {
            _str = _str.split("1").join("");
        }
    }
    if (_str.indexOf('0.') == -1) {
        if (parseInt(_str) ==0) {
            _str = "0";
        }
    }
    return _str;
}
function cleanBrac(str, a, b, c) {
	var c1 = "(" + b + ")";
	var c2 = "(" + c + ")";
	var c3 = isNaN(a) ? "(" + a + ")" : null;
	var _str = str.split(c1).join(String(b));
	_str = _str.split(c2).join(String(c));
	if (c3 != null) {
		_str = _str.split(c3).join(String(a));
	}
	return _str;
}
function splitAtOp(exp, op) {
	function exists(str, e) {
		var o = str.indexOf(e, 0);
		if (o == -1) {
			return false;
		} else {
			return true;
		}
	}
	function isUnary(e) {
		if (pc == "" || pc == "(" || exists(opArrS, pc)) {
			return true;
		} else {
			return false;
		}
	}
	var opStr = op.toString();
	var opArrS = '-+'
	var opL = op.length;
	var tstr = exp.split(" ").join("");
	tstr = tstr.split("–").join("-");
	tstr = tstr.split("∙").join("*");
	var expL = tstr.length;
	var c;
	var eArr= [];
	var topArr = [];
	var cs = "";
	var pc = "";
	var ind = 0;
	for (var i = 0; i < expL; i++) {
		c = tstr.charAt(i);
		trace(c);
		if (exists(opStr, c) && !isUnary(c)) {
			eArr.push((cs));
			topArr.push(c);
			cs = "";
		} else {
			cs += c;
		}
		pc = c;
	}
	eArr.push(cs);
	return {e:eArr, op:topArr};
}
function get_vars(){

/**
 * in word problem activity validation code needs the variables used in the
 * problem, since the user have to use same variables to get it correct...
 * 
 * fetch the variables used in problem and return it as string
 * 
 * The variables in a problem is defined in problem xml's answer node as 'vars'
 * attribute
 * 
 */
}
function isArray(obj) {
//returns true is it is an array
if (obj.constructor.toString().indexOf("Array") == -1)
return false;
else
return true;
}

// UTILITY FUNCTIONS END ///////////////////////////////////////