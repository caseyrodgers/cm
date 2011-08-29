/**
 * @author sathesh
 */
var Plotter=function(graphObj,plot_type,plot_input)
{
	this.graphObj=graphObj;
	this.plot_type=plot_type;
	this.plot_input=plot_input;
	this.graph_type=this.graphObj.graph_type;
	//
	this.canvas = this.graphObj.canvas;
	this.svg=this.graphObj.svg;
	this.scope=this.graphObj.document.createElementNS(this.svg.ns,"g");
	this.canvas.appendChild(this.scope);
	//
	
	//
	this.drawPlot();
}
Plotter.prototype.drawPlot=function()
{
	this.getPlotInputs();
	if(this.plot_type=='point'){
		this.plotPoints();
	}else if(this.plot_type=='function')
	{
		this.plotFunction();
	}
}
Plotter.prototype.getPlotInputs=function(){	
	this.plot_data=this.plot_input.data;	
	this.fn_color=this.plot_input.fn_color;
	this.fn_color=this.fn_color?this.fn_color:'#0000ff';
}
Plotter.prototype.plotPoints=function(){
	var temp1=this.plot_data.split("|");
	for(var i=0;i<temp1.length;i++){
		var pointdata=eval(temp1[i]);
		console.log("i:"+i+"  "+pointdata[0].length)
		if(pointdata[0].length==1){
			this.plotPoint(pointdata[0][0],0,pointdata[1],pointdata[2]);
		}else{
			this.plotPoint(pointdata[0][0],pointdata[0][1],pointdata[1],pointdata[2]);
		}
		
	}
}
Plotter.prototype.plotPoint=function(x,y,label,color){
	var pt=this.coordToCanvasPoint(x,this.graph_type=='xy'?y:0);
	var xp=pt[0];
	var yp=pt[1];
	//this.drawPoint(xp,yp,color?color:this.fn_color);
	var pt=this.svg.circle(xp,yp,4,null,null,color?color:this.fn_color);
	this.scope.appendChild(pt);
	if(label){
		
		//this.context.textBaseline = 'bottom';
		//this.context.font="bold 12px sans-serif";
		if(this.graph_type=='xy'){
			//this.context.fillText("("+x+", "+y+")",xp+3,yp-6);
			this.scope.appendChild(this.svg.setLabel("("+x+", "+y+")",xp+3,yp-6,12,'sans-serif'));
		}else{
			//this.context.fillText("("+x+", "+y+")",xp+3,yp-6);
			this.scope.appendChild(this.svg.setLabel(x,xp-3,yp-12,12,'sans-serif'));
		}
		
	}
}
Plotter.prototype.plotFunctions=function(x,y,color){
	var temp1=this.plot_data.split("|");
	/*var ctx=this.context;
	ctx.lineStyle=color?color:this.fn_color;
	ctx.lineWidth=2.0;*/
	for(var i=0;i<temp1.length;i++){
		var fndata=eval(temp1[i]);
		this.plotFunction(fndata);
	}
	
}
Plotter.prototype.plotFunction=function(data){
	var fn=data[0];
}
//
Plotter.prototype.drawPoint=function (x, y, color) {
	var ctx=this.context;  
	//alert(ctx);  
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(x, y, 4, 0, 2 * Math.PI, false);
    ctx.fill();
   ctx.closePath();
}
Plotter.prototype.coordToCanvasPoint=function(x,y){
	var xp=this.graphObj.axisYpos+x*this.graphObj.scaleX;
	var yp=this.graphObj.axisXpos-y*this.graphObj.scaleY;
	return [xp,yp];
}
