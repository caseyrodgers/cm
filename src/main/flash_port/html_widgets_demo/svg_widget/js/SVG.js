/**
 * @author sathesh
 */
	var SVG = function(doc) {
		// These are SVG-related namespace URLs
		this.document = doc;
		this.ns = "http://www.w3.org/2000/svg";
		this.xlinkns = "http://www.w3.org/1999/xlink";
	}

	SVG.prototype.makeCanvas = function(width, height) {
		var svg = this.document.createElementNS(this.ns, "svg:svg");
		// How big is the canvas in pixels
		svg.setAttribute("width", width);
		svg.setAttribute("height", height);
		// Set the coordinates used by drawings in the canvas
		//svg.setAttribute("viewBox", "0 0 " + width + " " + height);
		// Define the XLink namespace that SVG uses
		svg.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", this.xlinkns);
		return svg;
	};
	SVG.prototype.setLabel=function(lab,x,y,s,f,c){
		var label = this.document.createElementNS(this.ns, "text");
        label.setAttribute("x", x);       
        label.setAttribute("y", y);
        label.setAttribute("font-family", f?f:"sans-serif");
        label.setAttribute("font-size", s?s:"12");
        label.setAttribute("fill", c?c:"black");
        label.appendChild(this.document.createTextNode(lab));
        return label;
	};
	SVG.prototype.setPath=function(d,stroke,s,fill){
		 var path = this.document.createElementNS(this.ns, "path");
        
        path.setAttribute("d", d);
                      // Set this path 
       fill? path.setAttribute("fill", fill):"";  
        stroke?path.setAttribute("stroke", stroke):""; 
        stroke?path.setAttribute("stroke-width", s):"";
        return path;
	};
	SVG.prototype.rect=function(x,y,w,h,stroke,s,fill){
		var rec = this.document.createElementNS(this.ns, "rect");
        rec.setAttribute("x", x);             
        rec.setAttribute("y", y);
        rec.setAttribute("width", w);        
        rec.setAttribute("height", h);
       /* fill?rec.setAttribute("fill", fill):"";   
        stroke?rec.setAttribute("stroke", stroke):"";   
         stroke?rec.setAttribute("stroke-width", s):"";*/
         return rec;
	}
SVG.prototype.circle=function(x,y,r,stroke,s,fill){
		var rec = this.document.createElementNS(this.ns, "circle");
        rec.setAttribute("cx", x);             
        rec.setAttribute("cy", y);
        rec.setAttribute("r", r);
        fill?rec.setAttribute("fill", fill):"";   
        stroke?rec.setAttribute("stroke", stroke):"";   
         stroke?rec.setAttribute("stroke-width", s):"";
         console.log(x+":"+y+":"+r+":"+stroke+":"+s+":"+fill)
         return rec;
}