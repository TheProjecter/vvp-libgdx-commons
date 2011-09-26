#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, sys
from lxml import etree
from vvp_libgdx_commons_inkscape import getPoints, avgPoint, JointType,\
	normalizeAngle, createCircleMarker

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
			action="store", type="float", 
			dest="upper", default=0.0,
			help="Upper Limit angle")
		
		self.OptionParser.add_option("--lower",
			action="store", type="float", 
			dest="lower", default=0.0,
			help="Lower Limit angle")
		
		self.OptionParser.add_option("--motor",
            action="store", type="inkbool", 
            dest="motor", default=False,
            help="enable motor")
		
		self.OptionParser.add_option("--motorSpeed",
			action="store", type="float", 
			dest="motorSpeed", default=0.0,
			help="motorSpeed")
		
		self.OptionParser.add_option("--maxMotorTorque",
			action="store", type="float", 
			dest="maxMotorTorque", default=0.0,
			help="maxMotorTorque")
		
	def createRevoluteJointLine(self, id1, id2, point1, point2):
		createCircleMarker(self)
		
		svg_uri = u'http://www.w3.org/2000/svg'
		line = etree.Element('{%s}%s' % (svg_uri,'path'))
		
		self.setParams(line)
			
		
		line.set('d', 'M%f %fL%f %f' %(point1[0], point1[1], point2[0], point2[1]))
		line.set('vvpType', JointType.RevoluteJoint)
		line.set('body1', str(id1))
		line.set('body2', str(id2))
		line.set('style', 'fill:none;stroke:#000ff0;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1;marker-start:url(#CircleMarker);marker-end:url(#CircleMarker);stroke-miterlimit:4;stroke-dasharray:3,3;stroke-dashoffset:0')
		
		
		return line
			
	def setParams(self, node):
		if self.options.limit:
			limits = self.getLimitAngles()
			node.set('limit', '%s,%s' %(str(limits[0]), str(limits[1])))
			
		if self.options.motor:
			node.set('motor', '%s,%s' %(str(self.options.motorSpeed), str(self.options.maxMotorTorque)))
		
	
	def getLimitAngles(self):
		upperAngle = normalizeAngle(self.options.upper)
		lowerAngle = normalizeAngle(self.options.lower)
		
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
		elif len(self.selected) == 1:
			id1, node1 = self.selected.items()[0]
			
			if node1.get('vvpType') == JointType.RevoluteJoint:
				self.setParams()
				
				return
					
				
			
			

		sys.stderr.write("You have to select exactly two bodys.")

	

if __name__ == '__main__':
	effect = CreateRevoluteJoint()
	effect.affect()
