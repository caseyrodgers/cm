Math.convertToFrac=function(n) {
		var n0, d0;
		var fracObj = {};
		var itsfrac = String(n).indexOf('/') != -1 ? true : false;
		var itsDeci = String(n).indexOf('.') != -1 ? true : false;
		if (itsfrac) {
			var splitVal = String(n).split("/");
			fracObj.n = splitVal[0];
			fracObj.d = splitVal[1];
			fracObj.val = String(n);
			return fracObj;
		}
		if (!itsDeci) {
			fracObj.n = n;
			fracObj.d = 1;
			fracObj.val = String(n);
		} else {
			var deciNum = String(n).split(".")[1].length;
			var process = true;
			var a = 0;
			var b;
			while (process) {
				a++;
				b = (n*a);
				if (deciNum>=4) {
					b = Math.fixTo(b, 3);
				} else {
					b = Math.fixTo(b, 8);
				}
				if (b == parseInt(b) || a>10000) {
					fracObj.n = b;
					fracObj.d = a;
					fracObj.val = b+"/"+a;
					process = false;
				}
			}
		}
		return fracObj;
}
Math.simpleFrac=function(frac){
		var itsfrac = String(frac).indexOf('/') != -1 ? true : false;
		var itsDeci = String(frac).indexOf('.') != -1 ? true : false;
		var n,d
		var _frac=frac
		var gcd
		if(itsfrac){			
		}else if(itsDeci){
			_frac=Math.convertToFrac(frac).val
		}else{
			return frac
		}
		
		var splitVal = String(_frac).split("/");
			n = splitVal[0];
			d = splitVal[1];
			//alert(frac);
			gcd=Math.getGCD(n,d);
			
			n=n/gcd
			d=d/gcd
			
			return n+"/"+d
}
Math.addFrac=function(f1,f2){
		var num1=f1.split("/")
		var num2=f2.split("/")
		var n,d,lcm,n0,n1,d0,d1
		n0=num1[0]
		n1=num2[0]
		d0=num1[1]
		d1=num2[1]
		lcm=Math.getLCM(d0*1,d1*1)
		d=lcm
		n0=(lcm/d0)*n0
		n1=(lcm/d1)*n1
		n=n0+n1
		var frac=n+"/"+d
		var sfrac=Math.simpleFrac(frac)
		return {frac:frac,val:sfrac}
}
Math.getGCD=function() {
		var argL = arguments.length;
		var gcd;
		//alert(arguments[0]+":"+arguments[1]);
		if (argL<2) {
			var mystr = String(arguments[0]);
			if (!isNaN(mystr)) {
				return arguments[0];
			} else {
				if (mystr.indexOf("/") == -1) {
					return arguments[0];
				} else {
					var myvars = mystr.split("/");
					var n = myvars[0];
					var d = myvars[1];
					return Math.getGCD(n, d);
				}
			}
		} else if (argL>2) {
			return null;
		} else {
			if (arguments[1] == 0) {
				return arguments[0];
			} else {
				return Math.getGCD(arguments[1], arguments[0]%arguments[1]);
			}
		}
	}
Math.getLCM=function(){
		var argL = arguments.length;
		var lcm;
		var gcd;
		//trace(arguments[0]+":"+arguments[1]);
		if (argL<2) {
			var mystr = String(arguments[0]);
			if (!isNaN(mystr)) {
				return arguments[0];
			} else {
				if (mystr.indexOf("/") == -1) {
					return arguments[0];
				} else {
					var myvars = mystr.split("/");
					var n = myvars[0];
					var d = myvars[1];
					return Math.getLCM(n, d);
				}
			}
		} else if (argL>2) {
			return null;
		} else {
			if (arguments[1] == 0) {
				return arguments[0];
			} else {
				gcd= Math.getGCD(arguments[0], arguments[1]);
				lcm=(arguments[0]*arguments[1])/gcd
				return lcm
			}
		}
}
Math.fixTo=function(n,m){
	var m0=m?m:0;
	var n=Number(n)
	return n.toFixed(m0)
}	