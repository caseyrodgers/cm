/**
 * @author sathesh
 */

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
	this.canvas = this.document.createElement("canvas");
	this.canvas.width = this.width;
	this.canvas.height = this.height;
	this.board.appendChild(this.canvas);
	this.context = this.canvas.getContext('2d');
	this.canvas.style.position='absolute';
	this.canvas.style.top=0;
	this.canvas.style.left=0;	
	//
	
	this.drawGraph();
}
/**Drawing Methods*/
Graph.prototype.drawGraph = function() {

	/**Draw grid lines*/
	var scope = this.context;
	var alab = 0;
	var i;
	var label;
	var label_dx=this.graph_type=='xy'?0:3;
	var grid_s=this.graph_type=='xy'?0:this.axisXpos-5;
	var grid_len=this.graph_type=='xy'?this.height:this.axisXpos+5;
	scope.lineWidth = 1.0;
	scope.strokeStyle = this.graph_type=='xy'?"rgb(150, 150, 150)":"BLACK";
	scope.beginPath();
	console.log(this.axisYpos + ":" + this.width + ":" + this.scaleX)
	for( i = this.axisYpos; i <= this.width; i += this.scaleX) {
		scope.moveTo(i, grid_s);
		scope.lineTo(i, grid_len);
		if(alab > 0 && (i < this.width)) {
			label = alab * this.xinc;
			scope.fillText(label, i-label_dx, this.axisXpos + 12)
		}
		//console.log(i+":"+label)
		alab++;
	}
	//scope.stroke();
	alab = 0;
	for( i = this.axisYpos; i > 0; i -= this.scaleX) {
		scope.moveTo(i, grid_s);
		scope.lineTo(i, grid_len);
		if(alab > 0 && (i > 0)) {
			label = -alab * this.xinc;
			scope.fillText(label, i-label_dx, this.axisXpos + 12)
		}
		alab++;
	}
	if(this.graph_type=='xy'){
	alab = 0;
	for( i = this.axisXpos; i <= this.height; i += this.scaleY) {
		scope.moveTo(0, i);
		scope.lineTo(this.width, i);
		if(alab > 0 && (i < this.height)) {
			label = -alab * this.yinc;
			scope.fillText(label, this.axisYpos + 3, i)
		}
		alab++;
	}
	alab = 0;
	for( i = this.axisXpos; i > 0; i -= this.scaleY) {
		scope.moveTo(0, i);
		scope.lineTo(this.width, i);
		if(alab > 0 && (i > 0)) {
			label = alab * this.yinc;
			scope.fillText(label, this.axisYpos + 3, i)
		}
		alab++;
	}
	}else{
		scope.fillText('0', this.axisYpos-label_dx, this.axisXpos+12)
	}
	scope.stroke();
	/**Draw Axes Lines*/
	scope.lineWidth = this.graph_type=='xy'?2.0:1.0;
	scope.strokeStyle = "BLACK";
	scope.beginPath();
	scope.moveTo(0, this.axisXpos);
	scope.lineTo(this.width, this.axisXpos);
	scope.moveTo(this.axisYpos, grid_s);
	scope.lineTo(this.axisYpos, grid_len);
	scope.stroke();
	/**Draw Arrows for Axes*/
	//!- arrow at west end
	scope.beginPath();
	scope.moveTo((this.width), this.axisXpos);
	scope.lineTo((this.width) - 10, this.axisXpos + 4);
	scope.lineTo((this.width) - 10, this.axisXpos - 4);
	scope.lineTo((this.width), this.axisXpos);
	scope.fill();
	//!- arrow at east end
	scope.beginPath();
	scope.moveTo((0), this.axisXpos);
	scope.lineTo((0) + 10, this.axisXpos + 4);
	scope.lineTo((0) + 10, this.axisXpos - 4);
	scope.lineTo((0), this.axisXpos);
	scope.fill();
	if(this.graph_type=='xy'){
	//!- arrow at north end
	scope.beginPath();
	scope.moveTo(this.axisYpos, (0));
	scope.lineTo(this.axisYpos + 4, (0) + 10);
	scope.lineTo(this.axisYpos - 4, (0) + 10);
	scope.lineTo(this.axisYpos, (0));
	scope.fill();
	//!- arrow at south end
	scope.beginPath();
	scope.moveTo(this.axisYpos, (this.height));
	scope.lineTo(this.axisYpos + 4, (this.width) - 10);
	scope.lineTo(this.axisYpos - 4, (this.width) - 10);
	scope.lineTo(this.axisYpos, (this.height));
	scope.fill();
	}

	/**Draw Graph Border*/
	scope.strokeRect(0, 0, this.width, this.height);

}
Graph.prototype.clearGraph=function(){
	//this.canvas.clearRect(0, 0, this.width, this.height);
	var cn = this.board.getElementsByTagName("canvas");
  for (var i = 0; i < cn.length; i++) 
  {
     console.log(cn[i]);  
     cn[i].getContext('2d').clearRect(0, 0, this.width, this.height);
  }

}
