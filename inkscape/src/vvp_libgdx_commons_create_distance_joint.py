#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, sys
from lxml import etree
from vvp_libgdx_commons_inkscape import getPoints, avgPoint, JointType

class CreateDistanceJoint(inkex.Effect):
	"""
	Example Inkscape effect extension.
	Creates a new layer with a "Hello World!" text centered in the middle of the document.
	"""
	def __init__(self):

		# Call the base class constructor.
		inkex.Effect.__init__(self)
		
		self.OptionParser.add_option("--distance",
            action="store", type="float", 
            dest="distance", default=0.0,
            help="distance")
		
		self.OptionParser.add_option("--dampingRatio",
            action="store", type="float", 
            dest="dampingRatio", default=0.0,
            help="dampingRatio")
		
		self.OptionParser.add_option("--frequencyHz",
            action="store", type="float", 
            dest="frequencyHz", default=0.0,
            help="frequencyHz")
		
	def createMarker(self):
		
		if(len(self.document.xpath('//svg:marker[@id=\'DiamondMarker\']', namespaces=inkex.NSS))==0):
			svg_uri = u'http://www.w3.org/2000/svg'
			marker = etree.Element('{%s}%s' % (svg_uri,'marker'))
			marker.set('id', 'DiamondMarker')
		
			path = etree.Element('{%s}%s' % (svg_uri,'path'))
			path.set('d', 'M 0,-7.0710768 L -7.0710894,0 L 0,7.0710589 L 7.0710462,0 L 0,-7.0710768 z')
			path.set('style', 'fill-rule:evenodd;stroke:#000000;stroke-width:1.0pt')
			path.set('transform', 'scale(0.4)')
		
			marker.insert(0, path)
		
			defs = self.xpathSingle('//svg:defs')
			
			defs.insert(0, marker)
		
	def createDistanceJointLine(self, id1, id2, point1, point2):
		self.createMarker()
		
		svg_uri = u'http://www.w3.org/2000/svg'
		line = etree.Element('{%s}%s' % (svg_uri,'path'))
		
		self.setParams(line)
			
		
		line.set('d', 'M%f %fL%f %f' %(point1[0], point1[1], point2[0], point2[1]))
		line.set('vvpType', JointType.DistanceJoint)
		line.set('body1', str(id1))
		line.set('body2', str(id2))
		line.set('style', 'fill:none;stroke:#000ff0;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1;marker-start:url(#DiamondMarker);marker-end:url(#DiamondMarker);stroke-miterlimit:4;stroke-dasharray:3,3;stroke-dashoffset:0')
		
		
		return line
			
	def setParams(self, node):
		node.set('distance', '%#.6f' %self.options.distance)
		node.set('dampingRatio', '%#.6f' %self.options.dampingRatio)
		node.set('frequencyHz', '%#.6f' %self.options.frequencyHz)
		
		
		
		

	def effect(self):
		if(len(self.selected) == 2):
			id1, node1 = self.selected.items()[0]
			id2, node2 = self.selected.items()[1]
			
			if node1.get('vvpType') == 'Body' and node2.get('vvpType') == 'Body':
				
				point1 = avgPoint(getPoints(node1.get('d')))
				point2 = avgPoint(getPoints(node2.get('d')))
				
				line = self.createDistanceJointLine(id1, id2, point1, point2)
				
				self.current_layer.append(line)
				return
		elif len(self.selected) == 1:
			id1, node1 = self.selected.items()[0]
			
			if node1.get('vvpType') == JointType.DistanceJoint:
				self.setParams()
				
				return

		sys.stderr.write("You have to select exactly two bodys.")

	

if __name__ == '__main__':
	effect = CreateDistanceJoint()
	effect.affect()
