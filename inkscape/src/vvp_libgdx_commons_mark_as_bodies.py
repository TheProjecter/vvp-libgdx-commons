#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, sys
# The simplestyle module provides functions for style parsing.
from vvp_libgdx_commons_inkscape import flattenPath, createDiamondMarker,\
	getPoints
from lxml import etree

class Direction:
	horizontal="horizontal"
	vertical="vertical"

class MarkBodies(inkex.Effect):
	"""
	Example Inkscape effect extension.
	Creates a new layer with a "Hello World!" text centered in the middle of the document.
	"""
	def __init__(self):

		# Call the base class constructor.
		inkex.Effect.__init__(self)

		self.OptionParser.add_option("-f", "--flatness",
			action="store", type="float", 
			dest="flat", default=10.0,
			help="Minimum flatness of the subdivided curves")
		
		self.OptionParser.add_option("--density",
			action="store", type="float", 
			dest="density", default=10.0,
			help="density")
		
		self.OptionParser.add_option("--boned",
            action="store", type="inkbool", 
            dest="boned", default=False,
            help="create boned body")
			
		self.OptionParser.add_option("-H", "--hide",
            action="store", type="inkbool", 
            dest="hide", default=False,
            help="hide the path")
		
		self.OptionParser.add_option("--direction",
            action="store", type="string", 
            dest="direction", default='vertical',
            help="direction")
		
		self.OptionParser.add_option("--markbodies")
		
		
	def createBoneLine(self, point1, point2, id):
		createDiamondMarker(self)
		
		svg_uri = u'http://www.w3.org/2000/svg'
		line = etree.Element('{%s}%s' % (svg_uri,'path'))
		
		line.set('d', 'M%f %fL%f %f' %(point1[0], point1[1], point2[0], point2[1]))
		line.set('vvpType', 'bone')
		line.set('body', str(id))
		line.set('style', 'fill:none;stroke:#c00044;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:4,2,1,2;stroke-dashoffset:0;marker-start:url(#DiamondMarker);marker-end:url(#DiamondMarker);marker-mid:url(#DiamondMarker)')
		
		return line
	
	def getBonePoints(self, direction, points):
		if direction == Direction.vertical:
			return self.getVerticalBonePoints(points)
		elif direction == Direction.horizontal:
			return self.getHoriziontalBonePoints(points)
			
	def getVerticalBonePoints(self, points):
		minY = sys.float_info.max
		maxY = 0
		x = 0
		
		for point in points:
			x = x + point[0]
			y = point[1]
			minY = min(minY, y)
			maxY = max(maxY, y)
			
		x = x / len(points)
		
		return [[x, minY],[x, maxY]]
		
	def getHoriziontalBonePoints(self, points):
		minX = sys.float_info.max
		maxX = 0
		y = 0
		
		for point in points:
			y = y + point[1]
			x = point[0]
			minX = min(minX, x)
			maxX = max(maxX, x)
			
		y = y / len(points)
		
		return [[minX, y],[maxX, y]]
			
			
		

	def effect(self):	
		for id, node in self.selected.iteritems():
			if node.tag == inkex.addNS('path','svg'):
				flattenPath(node, self.options.flat)
				
				color = "#d4ff2a"
				
				if self.options.boned:
					direction = self.options.direction
					color = "#00f7ed"
					d = node.get('d')
					points = getPoints(d)
					
					bonePoints = self.getBonePoints(direction, points)
					
					line = self.createBoneLine(bonePoints[0], bonePoints[1], id)
					self.current_layer.append(line)
					
			
				node.set('vvpType', 'Body')
				node.set('density', str(self.options.density))
				if self.options.hide:
					node.set('style', 'fill:%s;fill-opacity:0.4;opacity:0;stroke:%s;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:6, 6;stroke-dashoffset:0' % (color,color))
				else:
					node.set('style', 'fill:%s;fill-opacity:0.4;stroke:%s;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:6, 6;stroke-dashoffset:0' % (color, color))
				#sys.stderr.write(pnts)
		
		

# Create effect instance and apply it.


	

if __name__ == '__main__':
	effect = MarkBodies()
	effect.affect()
