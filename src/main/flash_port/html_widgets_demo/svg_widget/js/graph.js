/**
 * @author sathesh
 */
	/** Graph Class*/
var Graph = function(doc, canvas_cont, graph_type, xmin, xmax, ymin, ymax, xinc, yinc, show_axis, show_axis_label, show_grid, show_half_grid, width, height) {
	//console.log(doc + ":" + canvas_cont + ":" + graph_type + ":" + xmin + ":" + xmax + ":" + ymin + ":" + ymax + ":" + xinc + ":" + yinc)
	this.document = doc;
	this.board = canvas_cont;
	this.graph_type = graph_type ? graph_type : 'xy';
	this.width = width ? width : 300;
	this.height = height ? height : (this.graph_type == 'xy' ? 300 : 150);
	this.xmin = xmin ? xmin * 1 : -5;
	this.xmax = xmax ? xmax * 1 : 5;
	this.ymin = ymin ? ymin * 1 : -5;
	this.ymax = ymax ? ymax * 1 : 5;
	this.xinc = xinc ? (xinc == 'pi' ? 1 : xinc * 1) : 1;
	this.yinc = yinc ? (yinc == 'pi' ? 1 : yinc * 1) : 1;
	this.show_axis = show_axis ? show_axis : true;
	this.show_axis_label = show_axis_label ? show_axis_label : true;
	this.show_grid = show_grid ? show_grid : true;
	this.show_half_grid = show_half_grid ? show_half_grid : false;
	//
	this.scaleX = this.width / ((Math.abs((this.xmax + this.xinc) - (this.xmin - this.xinc))) / this.xinc);
	this.scaleY = this.height / ((Math.abs((this.ymax + this.yinc) - (this.ymin - this.yinc))) / this.yinc);
	this.axisYpos = (this.width) - (((this.xmax + this.xinc) / this.xinc) * (this.scaleX));
	this.axisXpos = (this.height) - (((this.ymax + this.yinc) / this.yinc) * (this.scaleY));
	//
this.svg=new SVG(this.document);
	this.canvas = this.svg.makeCanvas(this.width,this.height);
	/**this.canvas.width = this.width;
	this.canvas.height = this.height;
	
	this.context = this.canvas.getContext('2d');*/
	this.board.appendChild(this.canvas);
	this.canvas.style.position='absolute';
	this.canvas.style.top=0;
	this.canvas.style.left=0;	
	//
	
	this.drawGraph();
}
/**Drawing Methods*/
Graph.prototype.drawGraph = function() {

	/**Draw grid lines*/
	var scope_axes = this.document.createElementNS(this.svg.ns,"g");
	 scope_axes.setAttribute('id', 'group_axes');
                    scope_axes.setAttribute('shape-rendering', 'inherit');
                    scope_axes.setAttribute('pointer-events', 'all');

	var scope_label = this.document.createElementNS(this.svg.ns,"g");
	scope_label.setAttribute('id', 'group_label');
                    scope_label.setAttribute('shape-rendering', 'inherit');
                    scope_label.setAttribute('pointer-events', 'all');
	//this.canvas.appendChild(scope);
	this.canvas.appendChild(scope_axes);
this.canvas.appendChild(scope_label);
	console.log(this.canvas);
	var alab = 0;
	var i;
	var label;
	var label_dx=this.graph_type=='xy'?0:3;
	var label_dy=this.graph_type=='xy'?16:16;
	var grid_s=this.graph_type=='xy'?0:this.axisXpos-5;
	var grid_len=this.graph_type=='xy'?this.height:this.axisXpos+5;
	var lineWidth = "1";
	var strokeStyle = this.graph_type=='xy'?"#999999":"BLACK";
	var d="";
	//path.setAttribute("stroke", strokeStyle);   // Outline wedge in black
       // path.setAttribute("stroke-width", lineWidth);
	for( i = this.axisYpos; i <= this.width; i += this.scaleX) {
		//scope.moveTo(i, grid_s);
		//scope.lineTo(i, grid_len);
		d+="M "+i+","+grid_s+" L "+i+","+grid_len+" ";
		if(alab > 0 && (i < this.width)) {
			label = alab * this.xinc;
			//scope.fillText(label, i-label_dx, this.axisXpos + 12);
			scope_label.appendChild(this.svg.setLabel(label,i-label_dx, this.axisXpos + label_dy,12,'sans-serif'));
		}
		//console.log(i+":"+label)
		alab++;
	}
	//scope.stroke();
	alab = 0;
	for( i = this.axisYpos; i > 0; i -= this.scaleX) {
		//scope.moveTo(i, grid_s);
		//scope.lineTo(i, grid_len);
		d+="M "+i+","+grid_s+" L "+i+","+grid_len+" ";
		if(alab > 0 && (i > 0)) {
			label = -alab * this.xinc;
			//scope.fillText(label, i-label_dx, this.axisXpos + 12);
			scope_label.appendChild(this.svg.setLabel(label,i-label_dx, this.axisXpos + label_dy,12,'sans-serif'));
		}
		alab++;
	}
	if(this.graph_type=='xy'){
	alab = 0;
	for( i = this.axisXpos; i <= this.height; i += this.scaleY) {
		//scope.moveTo(0, i);
		//scope.lineTo(this.width, i);
		d+="M "+0+","+i+" L "+this.width+","+i+" ";
		if(alab > 0 && (i < this.height)) {
			label = -alab * this.yinc;
			//scope.fillText(label, this.axisYpos + 3, i);
			scope_label.appendChild(this.svg.setLabel(label,this.axisYpos + 6, i,12,'sans-serif'));
		}
		alab++;
	}
	alab = 0;
	for( i = this.axisXpos; i > 0; i -= this.scaleY) {
		//scope.moveTo(0, i);
		//scope.lineTo(this.width, i);
		d+="M "+0+","+i+" L "+this.width+","+i+" ";
		if(alab > 0 && (i > 0)) {
			label = alab * this.yinc;
			//scope.fillText(label, this.axisYpos + 3, i);
			scope_label.appendChild(this.svg.setLabel(label,this.axisYpos + 6, i,12,'sans-serif'));
		}
		alab++;
	}
	}else{
		//scope.fillText('0', this.axisYpos-label_dx, this.axisXpos+12)
		scope_label.appendChild(this.svg.setLabel("0",this.axisYpos-label_dx, this.axisXpos+label_dy,12,'sans-serif'));
	}
	//scope.stroke();
	var grid=this.svg.setPath(d,strokeStyle,"1");
	scope_axes.appendChild(grid);
	/**Draw Axes Lines*/
	lineWidth = this.graph_type=='xy'?"2":"1";
	strokeStyle = "BLACK";
	//scope.beginPath();
	d="";
	d+="M "+0+","+this.axisXpos+" L "+this.width+","+this.axisXpos+" ";
	d+="M "+this.axisYpos+","+grid_s+" L "+this.axisYpos+","+grid_len+" ";
	var axes=this.svg.setPath(d,strokeStyle,lineWidth);
	scope_axes.appendChild(axes);
	//scope.moveTo(0, this.axisXpos);
	//scope.lineTo(this.width, this.axisXpos);
	//scope.moveTo(this.axisYpos, grid_s);
	//scope.lineTo(this.axisYpos, grid_len);
	//scope.stroke();
	/**Draw Arrows for Axes*/
	d="";
	
	//!- arrow at west end
	
	//scope.moveTo((this.width), this.axisXpos);
	//scope.lineTo((this.width) - 10, this.axisXpos + 4);
	//scope.lineTo((this.width) - 10, this.axisXpos - 4);
	//scope.lineTo((this.width), this.axisXpos);
	d+="M "+this.width+","+this.axisXpos+" L "+(this.width-10)+","+(this.axisXpos+4)+" ";
	d+="L "+(this.width-10)+","+(this.axisXpos-4)+" L "+this.width+","+this.axisXpos+" ";
	//!- arrow at east end
	/*scope.moveTo((0), this.axisXpos);
	scope.lineTo((0) + 10, this.axisXpos + 4);
	scope.lineTo((0) + 10, this.axisXpos - 4);
	scope.lineTo((0), this.axisXpos);*/
	d+="M "+0+","+this.axisXpos+" L "+(0+10)+","+(this.axisXpos+4)+" ";
	d+="L "+(0+10)+","+(this.axisXpos-4)+" L "+0+","+this.axisXpos+" ";
	if(this.graph_type=='xy'){
	//!- arrow at north end
	/*
	scope.moveTo(this.axisYpos, (0));
	scope.lineTo(this.axisYpos + 4, (0) + 10);
	scope.lineTo(this.axisYpos - 4, (0) + 10);
	scope.lineTo(this.axisYpos, (0));
	*/
	d+="M "+this.axisYpos+","+0+" L "+(this.axisYpos+4)+","+10+" ";
	d+="L "+(this.axisYpos-4)+","+10+" L "+this.axisYpos+","+0+" ";
	//!- arrow at south end
	/*
	scope.moveTo(this.axisYpos, (this.height));
	scope.lineTo(this.axisYpos + 4, (this.width) - 10);
	scope.lineTo(this.axisYpos - 4, (this.width) - 10);
	scope.lineTo(this.axisYpos, (this.height));
	*/
	d+="M "+this.axisYpos+","+this.height+" L "+(this.axisYpos+4)+","+(this.height-10)+" ";
	d+="L "+(this.axisYpos-4)+","+(this.height-10)+" L "+this.axisYpos+","+this.height+" ";
	}
var arrows=this.svg.setPath(d,undefined,undefined,"black");
	scope_axes.appendChild(arrows);
	/**Draw Graph Border*/
	//scope.strokeRect(0, 0, this.width, this.height);
	d="M "+0+","+0+" L "+this.width+","+0+" ";
	d+="L "+this.width+","+this.height+" L "+0+","+this.height+" L "+0+","+0;
	var border=this.svg.setPath(d,'black','4',"none");
scope_axes.appendChild(border);

}
Graph.prototype.clearGraph=function(){
	//this.canvas.clearRect(0, 0, this.width, this.height);
	var cn = this.canvas.getElementsByTagName("g");
 
while(this.canvas.getElementsByTagName("g").length){
	 console.log("LOG:"+cn[0]); 
     this.canvas.removeChild(cn[0]); 
}
}
