#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, sys, math
from lxml import etree
from vvp_libgdx_commons_inkscape import getPoints, avgPoint, JointType

class CreateRevoluteJoint(inkex.Effect):
	"""
	Example Inkscape effect extension.
	Creates a new layer with a "Hello World!" text centered in the middle of the document.
	"""
	def __init__(self):

		# Call the base class constructor.
		inkex.Effect.__init__(self)
		
		self.OptionParser.add_option("--limit",
            action="store", type="inkbool", 
            dest="limit", default=False,
            help="enable Limit")
		
		self.OptionParser.add_option("--upper",
			action="store", type="inkbool", 
			dest="upper", default=0.0,
			help="Upper Limit angle")
		
		self.OptionParser.add_option("--lower",
			action="store", type="inkbool", 
			dest="lower", default=0.0,
			help="Lower Limit angle")
		
	def createMarker(self):
		
		if(len(self.document.xpath('//svg:marker[@id=\'CircleMarker\']', namespaces=inkex.NSS))==0):
			svg_uri = u'http://www.w3.org/2000/svg'
			marker = etree.Element('{%s}%s' % (svg_uri,'marker'))
			marker.set('id', 'CircleMarker')
		
			path = etree.Element('{%s}%s' % (svg_uri,'path'))
			path.set('d', 'M -2.5,-1.0 C -2.5,1.7600000 -4.7400000,4.0 -7.5,4.0 C -10.260000,4.0 -12.5,1.7600000 -12.5,-1.0 C -12.5,-3.7600000 -10.260000,-6.0 -7.5,-6.0 C -4.7400000,-6.0 -2.5,-3.7600000 -2.5,-1.0 z ')
			path.set('style', 'fill-rule:evenodd;stroke:#000000;stroke-width:1.0pt')
			path.set('transform', 'scale(0.8) translate(7.4, 1)')
		
			marker.insert(0, path)
		
			defs = self.xpathSingle('//svg:defs')
			
			defs.insert(0, marker)
		
	def createRevoluteJointLine(self, id1, id2, point1, point2):
		self.createMarker()
		
		svg_uri = u'http://www.w3.org/2000/svg'
		line = etree.Element('{%s}%s' % (svg_uri,'path'))
		
		if self.options.limit:
			limits = self.getLimitAngles()
			line.set('limit', '%s,%s' %(str(limits[0]), str(limits[1])))
			
		
		line.set('d', 'M%f %fL%f %f' %(point1[0], point1[1], point2[0], point2[1]))
		line.set('vvpType', JointType.RevoluteJoint)
		line.set('body1', str(id1))
		line.set('body2', str(id2))
		line.set('style', 'fill:none;stroke:#000ff0;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1;marker-start:url(#CircleMarker);marker-end:url(#CircleMarker);stroke-miterlimit:4;stroke-dasharray:3,3;stroke-dashoffset:0')
		
		
		return line
	
	def normalizeAngle(self, angle):
		while angle <= -180.0:
			angle += 360.0
			
		return angle / 180.0 * math.pi 
			
		
	
	def getLimitAngles(self):
		upperAngle = self.normalizeAngle(self.options.upper)
		lowerAngle = self.normalizeAngle(self.options.lower)
		
		return [lowerAngle, upperAngle]

	def effect(self):
		if(len(self.selected) == 2):
			id1, node1 = self.selected.items()[0]
			id2, node2 = self.selected.items()[1]
			
			if node1.get('vvpType') == 'Body' and node2.get('vvpType') == 'Body':
				
				point1 = avgPoint(getPoints(node1.get('d')))
				point2 = avgPoint(getPoints(node2.get('d')))
				
				line = self.createRevoluteJointLine(id1, id2, point1, point2)
				
				self.current_layer.append(line)
				return

		sys.stderr.write("You have to select exactly two bodys.")

	

if __name__ == '__main__':
	effect = CreateRevoluteJoint()
	effect.affect()
