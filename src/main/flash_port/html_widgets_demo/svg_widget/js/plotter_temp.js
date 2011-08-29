/**
 * @author sathesh
 */
var Plot=function (_graphObj, _funcObj) {
	this.pInf = Number.POSITIVE_INFINITY;
	this.nInf = Number.NEGATIVE_INFINITY;
	this.fArr = ["sin", "cos", "tan", "asin", "acos", "atan", "log", "abs", "sqrt", "ln", "cot", "sec", "csc"];
	this.graphObj = _graphObj;		
	this.funcObj = _funcObj;
	this.getPointsFromEq();
}
Plot.prototype.validateFunc =function() {
		var boo = true;
		var veqn = this.funcObj.eqn;
		var pos, len, brac;
		for (var f = 0; f<this.fArr.length; f++) {
			len = this.fArr[f].length;
			pos = veqn.indexOf(fArr[f]);
			if (pos == -1) {
				continue;
			} else {
				brac = veqn.substr(pos+len, 1);
				if (brac != "(") {
					boo = false;
					break;
				}
			}
		}
		return boo;
	}
Plot.prototype.getDval=function(eqO, v, dv) {
		var dval = fixTo(v+dv, 4);
		return evalFor(eqO, dval);
	}
	Plot.prototype.sign=function (val) {
		if (val*1<0) {
			return '-';
		} else {
			return '+';
		}
	}
	 Plot.prototype.setFunctionDatas= function() {
		this.fn = this.funcObj.eqn;
		this.fncol = this.funcObj.col;
		this.fneql = this.funcObj.eql;
		this.fand = this.funcObj.fand;
		this.fof = this.funcObj.fof;
	}
	Plot.prototype.fixTo=function(v,p){
		var p=Math.pow(10,p);
		return Math.round(p10*v)/p10
	}
	Plot.prototype.evalFor=function(ev,fr)
	{
		var x=fr;
		return eval(ev);
	}
	Plot.prototype.getPointsFromEq=function () {
		this.setFunctionDatas();
		this.setAxisDatas();
		if (this.numbLine) {
			return this.plotNumberLine();
		}
		var f = this.fof;
		if (f == "y") {
			return this.getPointsFromEqY();
		}
		this.graph_color = fncol;
		if (fn == "" || fn == undefined) {
			return null;
		}
		var buffrArr = [];
		this.eqnObj = this.fn;
		this.eqSy = fneql;
		var acc = .1;
		var xscale=this.graphObj.scaleX;
		var yscale=this.graphObj.scaleY;
		var xaxis=this.graphObj.axisYpos;
		var yaxis=this.graphObj.axisXpos;
		var ptsArr = [];
		var xLow = this.xMin;
		var xHi = this.xMax;
		var yLow = this.yMin;
		var yHi = this.yMax;
		var y, x0, y0, x1, y1;
		var xPix, yPix, x0Pix, y0Pix, x1Pix, y1Pix, ycomp, dy0, dy1;
		this.doPlot = true;
		for (var x = xLow; x<=xHi; x += acc) {
			x = fixTo(x, 4);
			y = eval(eqnObj);
			x0 = x-acc;
			y0 = this.evalFor(eqnObj, x0);
			x1 = x+acc;
			y1 = this.evalFor(eqnObj, x1);
			if (Number(y)) {
				xPix = x*xscale+xaxis;
				yPix = y*yscale+yaxis;
				x0Pix = x0*xscale+xaxis;
				y0Pix = y0*yscale+yaxis;
				x1Pix = x1*xscale+xaxis;
				y1Pix = y1*yscale+yaxis;
				if (yPix>-2880 && yPix<2880) {
					xPix = fixTo(xPix, 2);
					yPix = fixTo(yPix, 2);
					ptsArr.push({x:xPix, y:yPix, l:"line"});
				} else if (yPix == nInf) {
					if (y0Pix != nInf && y1Pix != nInf && y0Pix != pInf && y1Pix != pInf && !isNaN(y0Pix) && !isNaN(y1Pix)) {
						dy0 = getDval(eqnObj, x0, acc/10);
						if (y0Pix<midY && y0Pix>-midY) {
							if (dy0>y0) {
								ycomp = yh;
							} else {
								ycomp = yl;
							}
						} else {
							ycomp = y0Pix;
						}
						ptsArr.push({x:x0Pix, y:ycomp, l:"line"});
						ptsArr.push({x:xPix, y:ycomp, l:"line"});
						ptsArr.push({x:xPix, y:-ycomp, l:"move"});
						dy1 = getDval(eqnObj, x1, -acc/10);
						if (y1Pix<midY && y1Pix>-midY) {
							if (dy1>y1) {
								ycomp = yh;
							} else {
								ycomp = yl;
							}
						} else {
							ycomp = y1Pix;
						}
						ptsArr.push({x:x1Pix, y:ycomp, l:"move"});
					} else {
						ptsArr.push({x:xPix, y:nInf, l:"move"});
					}
				} else if (yPix == pInf) {
					if (y0Pix != nInf && y1Pix != nInf && y0Pix != pInf && y1Pix != pInf && !isNaN(y0Pix) && !isNaN(y1Pix)) {
						dy0 = getDval(eqnObj, x0, acc/10);
						if (y0Pix<midY && y0Pix>-midY) {
							if (dy0>y0) {
								ycomp = yh;
							} else {
								ycomp = yl;
							}
						} else {
							ycomp = y0Pix;
						}
						ptsArr.push({x:x0Pix, y:ycomp, l:"line"});
						ptsArr.push({x:xPix, y:ycomp, l:"line"});
						ptsArr.push({x:xPix, y:-ycomp, l:"move"});
						dy1 = getDval(eqnObj, x1, -acc/10);
						if (y1Pix<midY && y1Pix>-midY) {
							if (dy1>y1) {
								ycomp = yh;
							} else {
								ycomp = yl;
							}
						} else {
							ycomp = y1Pix;
						}
						ptsArr.push({x:x1Pix, y:ycomp, l:"line"});
					} else {
						ptsArr.push({x:xPix, y:pInf, l:"move"});
					}
				} else {
					buffrArr.push(y);
				}
			}
		}
		ptsArr = this.asymFix(ptsArr);
		var n;
		if (ptsArr.length<2) {
			if (buffrArr.length<2) {
				if (validateFunc()) {
					//alert("Not a valid function!");
				} else {
					alert("Check your input function!");
				}
			} else {
				alert("Cannot plot the given function!");
			}
			return null;
		} else {
			n = this.getNodeDetails(ptsArr);
			return this.drawFunction(n, ptsArr);
		}
	}
	 Plot.prototype.getNodeDetails=function(arr) {
		var sNode = {};
		var eNode = {};
		var nodes = {};
		for (var q = 0; q<arr.length; q++) {
			if (q == 0) {
				if (this.fof == "x") {
					if (arr[q].y == this.pInf) {
						arr[q].y = yh;
						arr[q].l = "line";
					}
					if (arr[q].y == this.nInf) {
						arr[q].y = yl;
						arr[q].l = "line";
					}
				}
				if (this.fof == "y") {
					if (arr[q].x == this.pInf) {
						arr[q].x = xh;
						arr[q].l = "line";
					}
					if (arr[q].x == this.nInf) {
						arr[q].x = xl;
						arr[q].l = "line";
					}
				}
			} else {
				if (this.fof == "x") {
					if (arr[q].y == this.pInf || arr[q].y == this.nInf) {
						arr[q].l = "move";
						arr[q+1].l = "move";
					}
				}
				if (this.fof == "y") {
					if (arr[q].x == this.pInf || arr[q].x == this.nInf) {
						arr[q].l = "move";
						arr[q+1].l = "move";
					}
				}
			}
		}
		sNode.pt = arr[0];
		sNode.dir = this.getDir(sNode.pt);
		eNode.pt = arr[arr.length-1];
		eNode.dir = this.getDir(eNode.pt);
		nodes.s = sNode;
		nodes.e = eNode;
		return nodes;
	}
	Plot.prototype.setAxisDatas=function() {
		var mygraphObj=this.graphObj;
		this.numbLine = mygraphObj.graph_type == "x" ? true : false;
		this.midX = mygraphObj.width/2;
		this.midY = mygraphObj.height/2;
		this.xMin = mygraphObj.xmin-mygraphObj.xinc;
		this.xMax = mygraphObj.xmax+mygraphObj.xinc;
		this.yMin =mygraphObj.ymin-mygraphObj.yinc;
		this.yMax = mygraphObj.ymax+mygraphObj.yinc;
		this.xscale = mygraphObj.scaleX;
		this.yscale = mygraphObj.scaleY;
		this.xaxis = mygraphObj.axisYpos;
		this.yaxis = mygraphObj.axisXpos;
		if (mygraphObj.labType == "pi") {
			this.xMin = Math.round((xMin)*Math.PI);
			this.xMax = Math.round((xMax)*Math.PI);
			this.xscale = xscale/Math.PI;
		}
		if (mygraphObj.ylabType == "pi") {
			this.yMin = Math.round((yMin)*Math.PI);
			this.yMax = Math.round((yMax)*Math.PI);
			this.yscale = yscale/Math.PI;
		}
		this.xl = 0;
		this.xh = this.midX*2;
		this.yl = 0;
		this.yh = this.midY*2;
	}
	Plot.prototype.getDir=function(pt) {
		var xscale = this.xscale;
		var yscale = this.yscale;
		var xaxis = this.xaxis;
		var yaxis = this.yaxis;
		var xh=this.xh;
		var xl=this.xl;
		var yh=this.yh;
		var yl=this.yl;
		var dir = "C";
		if (pt.y>=yh && (pt.x<=xh && pt.x>=xl)) {
			dir = "N";
		}
		if (pt.y<=yl && (pt.x<=xh && pt.x>=xl)) {
			dir = "S";
		}
		if (pt.x>=xh && (pt.y<=yh && pt.y>=yl)) {
			dir = "E";
		}
		if (pt.x<=xl && (pt.y<=yh && pt.y>=yl)) {
			dir = "W";
		}
		if (pt.y>=yh && pt.x>=xh) {
			dir = "NE";
		}
		if (pt.y<=yl && pt.x>=xh) {
			dir = "SE";
		}
		if (pt.y>=yh && pt.x<=xl) {
			dir = "NW";
		}
		if (pt.y<=yl && pt.x<=xl) {
			dir = "SW";
		}
		return dir;
	}
	Plot.prototype.plotPath=function(sN, eN, mc) {
		var pathString, pathArr, rot;
		var eqS = this.eqSy;
		var xh=this.xh;
		var xl=this.xl;
		var yh=this.yh;
		var yl=this.yl;
		if (eqS == "eq" || eN.dir == "C" || sN.dir == "C") {
			return false;
		}
		if (eqS == "le" || eqS == "lt") {
			if (fof == "y") {
				pathString = "S-SE-E-NE-N-NW-W-SW-S";
				rot = "+";
			} else {
				pathString = "N-NE-E-SE-S-SW-W-NW-N";
				rot = "-";
			}
		}
		if (eqS == "ge" || eqS == "gt") {
			if (fof == "y") {
				pathString = "N-NE-E-SE-S-SW-W-NW-N";
				rot = "-";
			} else {
				pathString = "S-SE-E-NE-N-NW-W-SW-S";
				rot = "+";
			}
		}                                     
		pathArr = pathString.split("-");
		var xp, yp;
		var flag = false;
		var dir = "C";
		for (var p = 0; p<pathArr.length; p++) {
			dir = pathArr[p];
			if (eN.dir == dir) {
				xp = eN.pt.x;
				yp = eN.pt.y;
				mc.moveTo(xp, yp);
				flag = true;
				if (eN.dir == sN.dir) {
					if ((rot == "-" && dir == "S") || (rot == "+" && dir == "N")) {
						xp = sN.pt.x;
						yp = sN.pt.y;
						mc.lineTo(xp, yp);
						flag = !true;
						break;
					}
				}
				continue;
			}
			if (flag && dir.length == 2) {
				if (pathArr[p] == sN.dir) {
					//trace("DD:"+dir+":"+dir.length+":"+flag+":"+sN.pt.x+":"+sN.pt.y);
					mc.lineTo(sN.pt.x, sN.pt.y);
					flag = false;
					break;
				} else {
					switch (dir) {
					case "NE" :
						if (rot == "-") {
							xp = xh;
						} else {
							if (sN.dir == "N") {
								yp = sN.pt.y;
							} else {
								yp = yh;
							}
						}
						//trace("DD:"+dir+":"+dir.length+":"+xp+":"+yp);
						mc.lineTo(xp, yp);
						break;
					case "SE" :
						if (rot == "-") {
							yp = yl;
						} else {
							xp = xh;
						}
						mc.lineTo(xp, yp);
						break;
					case "SW" :
						if (rot == "-") {
							xp = xl;
						} else {
							yp = yl;
						}
						mc.lineTo(xp, yp);
						break;
					case "NW" :
						if (rot == "-") {
							yp = yh;
						} else {
							xp = xl;
						}
						mc.lineTo(xp, yp);
						break;
					}
				}
			} else if (flag) {
				if (pathArr[p] == sN.dir) {
					mc.lineTo(sN.pt.x, sN.pt.y);
					flag = false;
					break;
				}
			}
		}
		return true;
	}
	Plot.prototype.drawFunction=function(nodes, arr) {
		var eqSy=this.eqSy;
		var lalpha = 1.0;
		var fHold=this.context;
		var fill;
		var xh=this.xh;
		var xl=this.xl;
		var yh=this.yh;
		var yl=this.yl;
		var pInf=this.pInf;
		var nInf=this.nInf;
		if (eqSy == "lt" || eqSy == "gt" || eqSy == "neq") {
			lalpha = 0;
		}
		if (eqSy != "eq" && eqSy != "neq") {
			fHold.lineWidth=0.0;
			fHold.strokeStyle=this.hex2rgb(this.graph_color,0);
			fHold.fillStyle=this.hex2rgb(this.graph_color,0.15);
			fHold.beginPath();
			fill = this.plotPath(nodes.s, nodes.e, fHold);
			if (!fill) {
				fHold.fill();
				fHold.closePath();
			}
		} else if (eqSy == "neq") {
			fHold.lineWidth=0.0;
			fHold.strokeStyle=this.hex2rgb(this.graph_color,0);
			fHold.fillStyle=this.hex2rgb(this.graph_color,0.15);
			fHold.beginPath();
			fHold.moveTo(xl, yl);
			fHold.lineTo(xh, yl);
			fHold.lineTo(xh, yh);
			fHold.lineTo(xl, yh);
			fHold.lineTo(xl, yl);
			fHold.stroke();
			fHold.closePath();
		}
		var pPt;
		if (eqSy != "neq") {
			fHold.lineWidth=1.0;
			fHold.strokeStyle=this.hex2rgb(this.graph_color,lalpha);
			//fHold.fillStyle=this.hex2rgb(this.graph_color,0.15);
			fHold.beginPath();
			for (var q = 0; q<arr.length; q++) {
				if (q>0) {
					var p1 = {x:arr[q].x, y:arr[q].y};
					var p2 = pPt;
					if (p1.x == pInf || p1.x == nInf || p1.y == pInf || p1.y == nInf || p2.x == pInf || p2.x == nInf || p2.y == pInf || p2.y == nInf) {
						//fHold.lineStyle(1, 0xff0000, lalpha);
						fHold.lineWidth=1.0;
			fHold.strokeStyle=this.hex2rgb('#ff0000',lalpha);
						if (eqSy == "eq") {
							//fHold.lineStyle(1, 0xff0000, 0);
							fHold.lineWidth=1.0;
			fHold.strokeStyle=this.hex2rgb('#ff0000',0);
						}
					} else {
						var dp = (Math2.distance(p1, p2));
						if (fof == "x") {
							if (dp>=(2*midX) && sign(p1.y) != sign(p2.y)) {
								fHold.lineWidth=1.0;
			fHold.strokeStyle=this.hex2rgb('#ff0000',lalpha);
								if (eqSy == "eq") {
									fHold.lineWidth=1.0;
			fHold.strokeStyle=this.hex2rgb('#ff0000',0);
								}
							} else {
								fHold.lineWidth=1.0;
			fHold.strokeStyle=this.hex2rgb(this.graph_color,lalpha);
							}
						}
						if (fof == "y") {
							if (dp>=(2*this.midY) && this.sign(p1.x) != this.sign(p2.x)) {
								fHold.lineWidth=1.0;
			fHold.strokeStyle=this.hex2rgb(this.graph_color,lalpha);
								if (eqSy == "eq") {
									fHold.lineWidth=1.0;
			fHold.strokeStyle=this.hex2rgb(this.graph_color,0);
								}
							} else {
								fHold.lineWidth=1.0;
			fHold.strokeStyle=this.hex2rgb(this.graph_color,lalpha);
							}
						}
						pPt = {x:arr[q].x, y:arr[q].y};
					}
				} else {
					pPt = {x:arr[q].x, y:arr[q].y};
				}
				if (q == 0 && (eqSy == "eq" || !this.fill)) {
					//
					fHold.moveTo(arr[q].x, arr[q].y);
				} else {
					//trace(arr[q].x+" <> "+arr[q].y);
					if (eqSy == "eq") {
						if (arr[q].l == "line") {
							fHold.lineTo(arr[q].x, arr[q].y);
						} else if (arr[q].l == "move") {
							fHold.moveTo(arr[q].x, arr[q].y);
						}
					} else {
						fHold.lineTo(arr[q].x, arr[q].y);
					}
				}
			}
			fHold.stroke();
			
		}
		if (eqSy != "eq") {
			fHold.fill();
		}
		if (lalpha == 0) {
			for (var q = 0; q<arr.length; q++) {
				if (q<arr.length-1) {
					var p1 = {x:arr[q].x, y:arr[q].y};
					var p2 = {x:arr[q+1].x, y:arr[q+1].y};
					if (p1.x == pInf) {
						p1.x = xh;
					} else if (p1.x == nInf) {
						p1.x = xl;
					}
					if (p1.y == pInf) {
						p1.y = yh;
					} else if (p1.y == nInf) {
						p1.y = yl;
					}
					if (p2.x == pInf) {
						p2.x = xh;
					} else if (p2.x == nInf) {
						p2.x = xl;
					}
					if (p2.y == pInf) {
						p2.y = yh;
					} else if (p2.y == nInf) {
						p2.y = yl;
					}
					var dp = (this.distance(p1, p2));
					var sL, gL;
					//fHold.lineStyle(2, graph_color, 100, false);
					fHold.lineWidth=2.0;
			fHold.strokeStyle=this.hex2rgb(this.graph_color,1.0);
					if (dp>=4) {
						sL = 1;
						gL = 3;
						if (fof == "x") {
							if (dp>=(2*this.midX) && this.sign(p1.y) != this.sign(p2.y)) {
								//fHold.lineStyle(2, 0xff0000, 100);
								fHold.lineWidth=2.0;
			fHold.strokeStyle=this.hex2rgb('#ff0000',1.0);
							}
						}
						if (fof == "y") {
							if (dp>=(2*this.midY) && this.sign(p1.x) != this.sign(p2.x)) {
								//fHold.lineStyle(2, 0xff0000, 100);
								fHold.lineWidth=2.0;
			fHold.strokeStyle=this.hex2rgb('#ff0000',1.0);
							}
						}
					} else {
						sL = dp/10;
						gL = 9*dp/10;
					}
					fHold.dashTo(p1.x, p1.y, p2.x, p2.y, sL, gL);
				}
			}
		}
		return fHold;
	}
	function getPointsFromEqY() {
		var gmc = graph_screen;
		setAxisDatas();
		var f = fof;
		var eqn = fn;
		var graph_color = fncol;
		if (eqn == "" || eqn == undefined) {
			return null;
		}
		var buffrArr = [];
		eqnObj = evalObj.parseFunc(eqn);
		eqSy = fneql;
		var acc = .1;
		var ptsArr = [];
		var xLow = xMin;
		var xHi = xMax;
		var yLow = yMin;
		var yHi = yMax;
		var x, x0, y0, x1, y1;
		var xPix, yPix, x0Pix, y0Pix, x1Pix, y1Pix, dx0, dx1, xcomp;
		doPlot = true;
		for (var y = xLow; y<=xHi; y += acc) {
			y = fixTo(y, 4);
			x = evalObj.evalFunc(eqnObj, y);
			y0 = y-acc;
			x0 = evalObj.evalFunc(eqnObj, y0);
			y1 = y+acc;
			x1 = evalObj.evalFunc(eqnObj, y1);
			//trace("Points:"+x+":"+y);
			if (!isNaN(x)) {
				xPix = x*xscale+xaxis;
				yPix = y*yscale+yaxis;
				x0Pix = x0*xscale+xaxis;
				y0Pix = y0*yscale+yaxis;
				x1Pix = x1*xscale+xaxis;
				y1Pix = y1*yscale+yaxis;
				//trace("PTs:"+x+":"+y);
				//trace("PIX:"+xPix+":"+yPix);
				if (xPix>-2880 && xPix<2880) {
					xPix = fixTo(xPix, 2);
					yPix = fixTo(yPix, 2);
					ptsArr.push({x:xPix, y:yPix, l:"line"});
				} else if (xPix == nInf) {
					//trace(x0Pix+":::::"+x1Pix);
					if (x0Pix != nInf && x1Pix != nInf && x0Pix != pInf && x1Pix != pInf && !isNaN(x0Pix) && !isNaN(x1Pix)) {
						dx0 = getDval(eqnObj, y0, acc/10);
						//trace(y0+":::::"+dy0);
						if (x0Pix<midX && x0Pix>-midX) {
							if (dx0>x0) {
								xcomp = xh;
							} else {
								xcomp = xl;
							}
						} else {
							xcomp = x0Pix;
						}
						ptsArr.push({y:y0Pix, x:xcomp, l:"line"});
						ptsArr.push({y:yPix, x:xcomp, l:"line"});
						ptsArr.push({y:yPix, x:-xcomp, l:"move"});
						dx1 = getDval(eqnObj, y1, -acc/10);
						//trace(y1+":::::"+dy1);
						if (x1Pix<midX && x1Pix>-midX) {
							if (dx1>x1) {
								xcomp = xh;
							} else {
								xcomp = xl;
							}
						} else {
							xcomp = x1Pix;
						}
						ptsArr.push({y:y1Pix, x:xcomp, l:"move"});
						//ptsArr.push({x:x1Pix, y:yh, l:"move"});
					} else {
						ptsArr.push({y:yPix, x:nInf, l:"move"});
					}
					//ptsArr.push({x:xPix, y:nInf, l:"move"});
				} else if (xPix == pInf) {
					if (x0Pix != nInf && x1Pix != nInf && x0Pix != pInf && x1Pix != pInf && !isNaN(x0Pix) && !isNaN(x1Pix)) {
						dx0 = getDval(eqnObj, y0, acc/10);
						//trace(y0+":::::"+dy0);
						if (x0Pix<midX && x0Pix>-midX) {
							if (dx0>x0) {
								xcomp = xh;
							} else {
								xcomp = xl;
							}
						} else {
							xcomp = x0Pix;
						}
						//trace("P0:"+y0Pix+":"+xcomp+":"+yPix);
						ptsArr.push({y:y0Pix, x:xcomp, l:"line"});
						ptsArr.push({y:yPix, x:xcomp, l:"line"});
						ptsArr.push({y:yPix, x:-xcomp, l:"move"});
						//ptsArr.push({x:xPix, y:pInf, l:"move"});
						dx1 = getDval(eqnObj, y1, -acc/10);
						trace(x1+":::::"+dx1);
						if (x1Pix<midX && x1Pix>-midX) {
							if (dx1>x1) {
								xcomp = xh;
							} else {
								xcomp = xl;
							}
						} else {
							xcomp = x1Pix;
						}
						//trace("P1:"+y1Pix+":"+xcomp+":"+yPix);
						ptsArr.push({y:y1Pix, x:xcomp, l:"line"});
						//ptsArr.push({x:x1Pix, y:yh, l:"move"});
					} else {
						ptsArr.push({y:yPix, x:pInf, l:"move"});
					}
					//ptsArr.push({x:xPix, y:pInf, l:"move"});
				} else {
					//xPix = fixTo(xPix, 2);
					//yPix = fixTo(yPix, 2);
					buffrArr.push(x);
				}
			}
			//ptsArr.push({x:x, y:y});                                                                                           
		}
		ptsArr = asymFix(ptsArr);
		var n;
		if (ptsArr.length<2) {
			if (buffrArr.length<2) {
				if (validateFunc()) {
					scope.mesg_anim("check");
				} else {
					scope.mesg_anim("fcheck");
				}
			} else {
				scope.mesg_anim("valid");
			}
			return null;
		} else {
			n = getNodeDetails(ptsArr);
			return drawFunction(n, ptsArr, graph_screen);
		}
	}
	function plotNumberLine() {
		var gmc = graph_screen;
		setAxisDatas();
		if (fand) {
			return plotNumberLineAnd();
		}
		var eqn = fn;
		var eql = fneql;
		if (eqn == "" || eqn == undefined) {
			return null;
		}
		var eqnObj = evalObj.parseFunc(eqn);
		var eqSy = eql;
		var x0 = evalObj.evalFunc(eqnObj, 0);
		var y0 = 0;
		var point;
		var dep = gmc.getNextHighestDepth();
		var fHold = gmc.createEmptyMovieClip("fHold_"+dep, dep);
		point = fHold.attachMovie("inEq_line", "inEq_line", 1);
		point._x = x0*xscale+xaxis;
		point._y = y0;
		var xpos = point._x;
		trace(eqSy+":"+point)
		var dx=6
		setColor(point, fncol);
		if (eqSy == "eq") {
			point.lineIE._visible = false;
			point.arrowIE._visible = false;
			point.ptIE.gotoAndStop(1);
		} else if (eqSy == "le") {
			point.lineIE._width = xpos-(-midX)-dx
			point.lineIE._rotation = point.arrowIE._rotation=180;
			point.lineIE._x = -3;
			point.arrowIE._x = -((xpos)-(-midX))+point.arrowIE._width
			point.ptIE.gotoAndStop(1);
		} else if (eqSy == "lt") {
			point.lineIE._width = (xpos)-(-midX)-dx;
			point.lineIE._rotation = point.arrowIE._rotation=180;
			point.lineIE._x = -3;
			point.arrowIE._x = -((xpos)-(-midX))+point.arrowIE._width;
			point.ptIE.gotoAndStop(2);
		} else if (eqSy == "ge") {
			point.lineIE._width = midX-(xpos)-dx;
			point.lineIE._rotation = point.arrowIE._rotation=0;
			point.lineIE._x = 3;
			point.arrowIE._x = (midX-(xpos))-point.arrowIE._width;
			point.ptIE.gotoAndStop(1);
		} else if (eqSy == "gt") {
			point.lineIE._width = midX-(xpos)-dx;
			point.lineIE._rotation = point.arrowIE._rotation=0;
			point.lineIE._x = 3;
			point.arrowIE._x = (midX-(xpos))-point.arrowIE._width;
			point.ptIE.gotoAndStop(2);
		}
		return fHold;
	}
	function plotNumberLineAnd() {
		var gmc = graph_screen;
		this.setAxisDatas();
		this.xscale = fixTo(this.xscale, 8);
		var eqDatas = fn.split("_");
		var leqn = eqDatas[0];
		var reqn = eqDatas[1];
		//color_comp.selColor = ocol == undefined ? color_comp.selColor : ocol;
		if (leqn == "" || leqn == undefined || reqn == "" || reqn == undefined) {
			return null;
		}
		//var eqSy = fneql;  
		this.eqSy = fneql.split("_");
		var leqSy = eqSy[0];
		var reqSy = eqSy[1];
		var x0 = evalFor(leqn, 0);
		var y0 = 0;
		var x1 = evalFor(reqn, 0);
		var y1 = 0;
		var point;
		var dep = gmc.getNextHighestDepth();
		var fHold = gmc.createEmptyMovieClip("fHold_"+dep, dep);
		point = fHold.attachMovie("inEq_line", "inEq_line", 1);
		point._x = x0*xscale+xaxis;
		point._y = y0;
		point.arrowIE._visible = false;
		point.lineIE._visible = false;
		point.ptIE.duplicateMovieClip("ptIE2", 4);
		point.ptIE2._x = (x1-x0)*xscale;
		point.lineIE._width = point.ptIE2._x-point.ptIE._x
		var xpos = point._x;
		point.lineStyle(3, fncol, 75, true, "none");
		point.moveTo(point.ptIE._x+3, 0);
		point.lineTo(point.ptIE2._x-3, 0);
		setColor(point, fncol);
		point.lineIE._x += 3;
		var getB = point.lineIE.getBounds(point);
		if (leqSy == "le") {
			point.ptIE.gotoAndStop(1);
		} else if (leqSy == "lt") {
			point.ptIE.gotoAndStop(2);
		}
		if (reqSy == "le") {
			point.ptIE2.gotoAndStop(1);
		} else if (reqSy == "lt") {
			point.ptIE2.gotoAndStop(2);
		}        
		return fHold;
	};
	function checkAsym(x1, x2) {
		var asYm;
		//trace(x1+":"+x2)
		for (var i = x1+.01; i<x2; i=fixTo(i+.01, 2)) {
			asYm = getDval(eqnObj, i, 0);
			trace("aaa:"+asYm);
			if (Math.abs(asYm)>10000) {
				return i;
			}
		}
		return null;
	}
	function fixAsym(arr, p) {
		var l = arr.length;
		var x0Pix = arr[l-2].x;
		var y0Pix = arr[l-2].y;
		var x1Pix = arr[l-1].x;
		var y1Pix = arr[l-1].y;
		var x0 = (x0Pix-xaxis)/xscale;
		var y0 = (y0Pix-yaxis)/yscale;
		var x1 = (x1Pix-xaxis)/xscale;
		var y1 = (y1Pix-yaxis)/yscale;
		var asymX = p*xscale+xaxis;
		var asymY = p*yscale+yaxis;
		var dy0, dy1, ycomp, dx0, dx1, xcomp;
		if (fof == "x") {
			dy0 = getDval(eqnObj, x0, .01);
			trace(y0+":::::"+dy0+":"+y0Pix);
			if (y0Pix<midY && y0Pix>-midY) {
				if (dy0>y0) {
					ycomp = yh;
				} else {
					ycomp = yl;
				}
			} else {
				ycomp = y0Pix;
			}
			trace("N0:"+x0Pix+":"+y0Pix);
			arr.splice(l-1, 0, {x:x0Pix, y:ycomp, l:"line"});
			arr.splice(l, 0, {x:asymX, y:ycomp, l:"line"});
			arr.splice(l+1, 0, {x:asymX, y:-ycomp, l:"line"});
			dy1 = getDval(eqnObj, x1, -.01);
			trace("!!: "+y1+":::::"+dy1+":"+y1Pix);
			if (y1Pix<midY && y1Pix>-midY) {
				if (dy1>y1) {
					ycomp = yh;
				} else {
					ycomp = yl;
				}
			} else {
				ycomp = y1Pix;
			}
			arr.splice(l+2, 0, {x:x1Pix, y:ycomp, l:"move"});
		}
		if (fof == "y") {
			dx0 = getDval(eqnObj, y0, .01);
			if (x0Pix<midX && x0Pix>-midX) {
				if (dx0>x0) {
					xcomp = xh;
				} else {
					xcomp = xl;
				}
			} else {
				xcomp = x0Pix;
			}
			arr.splice(l-1, 0, {y:y0Pix, x:xcomp, l:"line"});
			arr.splice(l, 0, {y:asymY, x:xcomp, l:"line"});
			arr.splice(l+1, 0, {y:asymY, x:-xcomp, l:"line"});
			dx1 = getDval(eqnObj, y1, -.01);
			if (x1Pix<midX && x1Pix>-midX) {
				if (dx1>x1) {
					xcomp = xh;
				} else {
					xcomp = xl;
				}
			} else {
				xcomp = x1Pix;
			}
			arr.splice(l+2, 0, {y:y1Pix, x:xcomp, l:"move"});
		}
	}
	function asymFix(arr) {
		var aL = arr.length;
		var nArr = [];
		var asymP;
		//var c = funcOf == "x" ? "y" : "x";
		for (var a = 0; a<arr.length; a++) {
			var pt = arr[a];
			var axX = (pt.x-xaxis)/xscale;
			var axY = (pt.y-yaxis)/yscale;
			//trace("aymPt:"+arr[a].x+":"+arr[a].y)
			nArr.push(arr[a]);
			if (a != 0) {
				if (fof == "x") {
					//trace("ASYMP:"+asymP+":"+axX+":"+axPX+":"+pPt.x+":"+pPt.y);
					if (sign(pPt.y) != sign(pt.y)) {
						asymP = checkAsym(axPX, axX);
						trace("ASYMP:"+asymP+":"+axX+":"+axPX+":"+pPt.x+":"+pPt.y);
						if (asymP != null) {
							fixAsym(nArr, asymP);
						}
					}
				}
				if (fof == "y") {
					//trace("ASYMP:"+asymP+":"+axY+":"+axPY+":"+pPt.x+":"+pPt.y);
					if (sign(pPt.x) != sign(pt.x)) {
						asymP = checkAsym(axPY, axY);
						trace("ASYMP:"+asymP+":"+axY+":"+axPY+":"+pPt.x+":"+pPt.y);
						if (asymP != null) {
							fixAsym(nArr, asymP);
						}
					}
				}
			}
			var pPt = arr[a];
			var axPX = axX;
			var axPY = axY;
		}
		return nArr;
	}
	Plot.prototype.hex2rgb =function( col,alp ) {
		var r,g,b;
if ( colour.charAt(0) == '#' ) {
colour = colour.substr(1);
}

r = colour.charAt(0) + '' + colour.charAt(1);
g = colour.charAt(2) + '' + colour.charAt(3);
b = colour.charAt(4) + ''+ colour.charAt(5);

r = parseInt( r,16 );
g = parseInt( g,16 );
b = parseInt( b ,16);
if(alp){
	return "rgba(" + r + "," + g + "," + b + ","+alp+")";
}else{
	return "rgb(" + r + "," + g + "," + b + ")";
}
       
       
}

