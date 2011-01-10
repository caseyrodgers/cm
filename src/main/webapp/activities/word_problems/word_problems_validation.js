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
// UTILITY FUNCTIONS END ///////////////////////////////////////
//
// VALIDATION CODE START ////////////////////////////////////////
function validateWordProblem1(expectedValue, vars, value1, value2, value3,value3p){
    try {
        var vars = vars.split(",");
        var len = vars.length;
        var sc = false;
        var isFrac=value2!==null;
        var isFrac2=value3!==null;
        var isFrac3=false;// $get('widget_input_field_3p').value!="";
        var inputValue
        var x
        var den="";
        if (!isFrac) {
            inputValue = value1;
            //
            if (len == 1) {
                var varStr1 = vars[0];
                
                /**
                 * den is not initialized ...
                 * 
                 */
                var hasVar = inputValue.indexOf(varStr1) > -1 || den.indexOf(varStr1) > -1;
                if (hasVar) {
                    var ieqn = prepareForEval(inputValue);
                    var oeqn = prepareForEval(expectedValue);
                    x=2
                    var einput = eval(ieqn);
                    var eoutput = eval(oeqn);
                    if (einput == eoutput) {
                        sc = true;
                    }
                }
            } else if (len == 2) {
                var varStr1 = vars[0]
                var varStr2 = vars[1]
                var hasVar = (inputValue.indexOf(varStr1) > -1 || den.indexOf(varStr1) > -1) && (inputValue.indexOf(varStr2) > -1 || den.indexOf(varStr2) > -1);
                if (hasVar) {
                    var isp = inputValue.split("+");
                    var osp = expectedValue.split("+");
                    var fieqn = cleanExtra(isp[0]);
                    var foeqn = cleanExtra(osp[0]);
                    var sieqn = cleanExtra(isp[1]);
                    var soeqn = cleanExtra(osp[1]);
                    var boo1 = (fieqn == foeqn && sieqn == soeqn);
                    var boo2 = (fieqn == soeqn && sieqn == foeqn);
                    if (boo1 || boo2) {
                        sc = true;
                    }
                }
            } else if (len == 3) {
                var varStr1 = vars[0]
                var varStr2 = vars[1]
                var varStr3 = vars[2]
                var hasVar = (inputValue.indexOf(varStr1) > -1 || den.indexOf(varStr1) > -1) && (inputValue.indexOf(varStr2) > -1 || den.indexOf(varStr2) > -1) && (inputValue.indexOf(varStr3) > -1 || den.indexOf(varStr3) > -1);
                if (hasVar) {
                    var isp = inputValue.split("+");
                    var osp = expectedValue.split("+");
                    var ife = cleanExtra(isp[0]);
                    var ofe = cleanExtra(osp[0]);
                    var ise = cleanExtra(isp[1]);
                    var ose = cleanExtra(osp[1]);
                    var ite = cleanExtra(isp[2]);
                    var ote = cleanExtra(osp[2]);
                    var boo1 = (ife == ofe && ise == ose && ite == ote);
                    var boo2 = (ife == ofe && ise == ote && ite == ose);
                    var boo3 = (ife == ote && ise == ose && ite == ofe);
                    var boo4 = (ife == ote && ise == ofe && ite == ose);
                    var boo5 = (ife == ose && ise == ofe && ite == ote);
                    var boo6 = (ife == ose && ise == ote && ite == ofe);
                    if (boo1 || boo2 || boo3 || boo4 || boo5 || boo6) {
                        sc = true;
                    }
                }
            }
        }
        if (isFrac) {
            val = value1;
            var den = value2;
            inputValue = (val)
            var outS = expectedValue.split("/");
            var noutput = outS[0];
            var doutput = outS[1];
            //
            if (len == 1) {
                var varStr1 = vars[0]
                var hasVar = inputValue.indexOf(varStr1) > -1 || den.indexOf(varStr1) > -1;
                if (hasVar) {
                    var ieqn = prepareForEval(inputValue);
                    var oeqn = prepareForEval(noutput);
                    x=2
                    var einput = eval(ieqn);
                    var eoutput = eval(oeqn);
                    if ((einput == eoutput) && doutput == den) {
                        sc = true;
                    }
                }
            } else if (len == 2) {
                var varStr1 = vars[0]
                var varStr2 = vars[1]
                var hasVar = (inputValue.indexOf(varStr1) > -1 || den.indexOf(varStr1) > -1) && (inputValue.indexOf(varStr2) > -1 || den.indexOf(varStr2) > -1);
                if (hasVar) {
                    var isp = inputValue.split("+");
                    var osp = noutput.split("+");
                    var fieqn = cleanExtra(isp[0]);
                    var foeqn = cleanExtra(osp[0]);
                    var sieqn = cleanExtra(isp[1]);
                    var soeqn = cleanExtra(osp[1]);
                    var boo1 = (fieqn == foeqn && sieqn == soeqn);
                    var boo2 = (fieqn == soeqn && sieqn == foeqn);
                    if ((boo1 || boo2) && doutput == den) {
                        sc = true;
                    }
                }
            } else if (len == 3) {
                var varStr1 = vars[0];
                var varStr2 = vars[1];
                var varStr3 = vars[2];
                var hasVar = (inputValue.indexOf(varStr1) > -1 || den.indexOf(varStr1) > -1) && (inputValue.indexOf(varStr2) > -1 || den.indexOf(varStr2) > -1) && (inputValue.indexOf(varStr3) > -1 || den.indexOf(varStr3) > -1);
                if (hasVar) {
                    var isp = inputValue.split("+");
                    var osp = noutput.split("+");
                    var ife = cleanExtra(isp[0]);
                    var ofe = cleanExtra(osp[0]);
                    var ise = cleanExtra(isp[1]);
                    var ose = cleanExtra(osp[1]);
                    var ite = cleanExtra(isp[2]);
                    var ote = cleanExtra(osp[2]);
                    var boo1 = (ife == ofe && ise == ose && ite == ote);
                    var boo2 = (ife == ofe && ise == ote && ite == ose);
                    var boo3 = (ife == ote && ise == ose && ite == ofe);
                    var boo4 = (ife == ote && ise == ofe && ite == ose);
                    var boo5 = (ife == ose && ise == ofe && ite == ote);
                    var boo6 = (ife == ose && ise == ote && ite == ofe);
                    if ((boo1 || boo2 || boo3 || boo4 || boo5 || boo6) && doutput == den) {
                        sc = true;
                    }
                }
            }
        }
        if(isFrac2||isFrac3){
            if (isFrac2) {
                var pre_txt = value3;
                var n = value1;
                var d = value2;
                var l = pre_txt.length - 1;
                var cchar = pre_txt.charAt(l);
                var doAdd = cchar == '+' || cchar == '-';
                if (doAdd) {
                    nVal = pre_txt + ("(" + n + "/" + d + ")");
                    dVal = '1';
                } else {
                    if (cchar == "*") {
                        pre_arr = pre_txt.split("");
                        pre_arr.pop();
                        pre_txt = pre_arr.join("");
                    }
                    pre_arr = pre_txt.split("+");
                    for (var i = 0; i < pre_arr.length; i++) {
                        if (String(n) == String(1)) {
                            pre_arr[i] = pre_arr[i];
                        } else if (String(pre_arr[i]) == String(1)) {
                            pre_arr[i] = n;
                            pre_arr[i] = n + "*" + pre_arr[i];
                        }
                    }
                    nVal = pre_arr.join("+");
                    dVal = String(d);
                }
            }
            if (isFrac3) {
                var pre_txt = value3p;
                var n = value1;
                var d = value2;
                var l = 0;
                var cchar = pre_txt.charAt(l);
                var doAdd = cchar == '+' || cchar == '-';
                if (doAdd) {
                    nVal = ("(" + n + "/" + d + ")") + pre_txt;
                    dVal = '1';
                } else {
                    if (cchar == "*") {
                        pre_arr = pre_txt.split("");
                        pre_arr.shift();
                        pre_txt = pre_arr.join("");
                    }
                    pre_arr = pre_txt.split("+");
                    for (var i = 0; i < pre_arr.length; i++) {
                        if (String(n) == String(1)) {
                            pre_arr[i] = pre_arr[i];
                        } else if (String(pre_arr[i]) == String(1)) {
                            pre_arr[i] = n;
                            pre_arr[i] = n + "*" + pre_arr[i];
                        }
                    }
                    nVal = pre_arr.join("+");
                    // input = clean(nVal).toLowerCase();
                    dVal = String(d);
                }
            }
            val = (nVal)
            var den = (dVal)
            inputValue = (val)
            var outS = expectedValue.split("/");
            var noutput = outS[0];
            var doutput = outS[1] ? outS[1] : "1";
            doutput = doutput
            //
            if (len == 1) {
                var varStr1 = vars[0];
                var hasVar = inputValue.indexOf(varStr1) > -1 || den.indexOf(varStr1) > -1;
                if (hasVar) {
                    var ieqn = prepareForEval(inputValue);
                    var oeqn = prepareForEval(noutput);
                    x=2
                    var einput = eval(ieqn);
                    var eoutput = eval(oeqn);
                    if ((einput == eoutput) && doutput == den) {
                        sc = true;
                    }
                }
            } else if (len == 2) {
                var varStr1 = vars[0];
                var varStr2 = vars[1];
                var hasVar = (inputValue.indexOf(varStr1) > -1 || den.indexOf(varStr1) > -1) && (inputValue.indexOf(varStr2) > -1 || den.indexOf(varStr2) > -1);
                if (hasVar) {
                    var isp = inputValue.split("+");
                    var osp = noutput.split("+");
                    var fieqn = cleanExtra(isp[0]);
                    var foeqn = cleanExtra(osp[0]);
                    var sieqn = cleanExtra(isp[1]);
                    var soeqn = cleanExtra(osp[1]);
                    var boo1 = (fieqn == foeqn && sieqn == soeqn);
                    var boo2 = (fieqn == soeqn && sieqn == foeqn);
                    if ((boo1 || boo2) && doutput == den) {
                        sc = true;
                    }
                }
            } else if (len == 3) {
                var varStr1 = vars[0];
                var varStr2 = vars[1];
                var varStr3 = vars[2];
                var hasVar = (inputValue.indexOf(varStr1) > -1 || den.indexOf(varStr1) > -1) && (inputValue.indexOf(varStr2) > -1 || den.indexOf(varStr2) > -1) && (inputValue.indexOf(varStr3) > -1 || den.indexOf(varStr3) > -1);
                if (hasVar) {
                    var isp = inputValue.split("+");
                    var osp = noutput.split("+");
                    var ife = cleanExtra(isp[0]);
                    var ofe = cleanExtra(osp[0]);
                    var ise = cleanExtra(isp[1]);
                    var ose = cleanExtra(osp[1]);
                    var ite = cleanExtra(isp[2]);
                    var ote = cleanExtra(osp[2]);
                    var boo1 = (ife == ofe && ise == ose && ite == ote);
                    var boo2 = (ife == ofe && ise == ote && ite == ose);
                    var boo3 = (ife == ote && ise == ose && ite == ofe);
                    var boo4 = (ife == ote && ise == ofe && ite == ose);
                    var boo5 = (ife == ose && ise == ofe && ite == ote);
                    var boo6 = (ife == ose && ise == ote && ite == ofe);
                    if ((boo1 || boo2 || boo3 || boo4 || boo5 || boo6) && doutput == den) {
                        sc = true;
                    }
                }
            }
        }
        if(sc){
            return true
        }else{
            return false
        } 
    }
    catch(exception) {
        alert('Validation exception: ' + exception);
        return false;
    }
}


/** Prepare explaination for display.
 * 
 *  exp_str is interpolated with vars.
 *  
 *  Replace variables with named variables in explain html
 *  
 *  
 */
function getExplainString(exp_str,vars){
    if(vars) {
        // ?
    }
    return exp_str;
}
