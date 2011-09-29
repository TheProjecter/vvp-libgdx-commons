#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, sys
# The simplestyle module provides functions for style parsing.
from vvp_libgdx_commons_inkscape import flattenPath, createDiamondMarker,\
	getPoints, avgPoint, JointType, createCircleMarker, normalizeAngle,\
	setOpacityToAll, Types, printError
from lxml import etree

class Direction:
	horizontal="Horizontal"
	vertical="Vertical"

class LibGdxToolchain(inkex.Effect):
	"""
	Example Inkscape effect extension.
	Creates a new layer with a "Hello World!" text centered in the middle of the document.
	"""
	def __init__(self):

		# Call the base class constructor.
		inkex.Effect.__init__(self)
		
		self.parseOptions()
		
	def parseOptions(self):
		inx = etree.parse(open('vvp_libgdx_commons_inkscape.inx', 'r'))
		for node in inx.xpath('//xmlns:param', namespaces={u'xmlns':u'http://www.inkscape.org/namespace/inkscape/extension'}):
			name = node.get('name')
			typeAttribute = node.get('type')
			
			if typeAttribute == 'float':
				value = float(node.text)
				self.OptionParser.add_option("--%s" %name, action="store", type="float", dest=name, default=value)
			elif typeAttribute == 'boolean':
				value = node.text.upper()
				if value == 'FALSE':
					value = False
				else:
					value = True	
				self.OptionParser.add_option("--%s" %name, action="store", type="inkbool", dest=name, default=value)
			elif typeAttribute == 'notebook':
				pages = node.xpath('./xmlns:page', namespaces={u'xmlns':u'http://www.inkscape.org/namespace/inkscape/extension'})
				
				value = pages[0].get('name')
				self.OptionParser.add_option("--%s" %name, action="store", type="string", dest=name, default=value)
			elif typeAttribute == 'optiongroup':
				options = node.xpath('./xmlns:_option', namespaces={u'xmlns':u'http://www.inkscape.org/namespace/inkscape/extension'})
				value = options[0].get('name')
				self.OptionParser.add_option("--%s" %name, action="store", type="string", dest=name, default=value)

###############Bodys################################

	def createBoneLine(self, point1, point2, idNr, direction):
		createDiamondMarker(self)
		
		svg_uri = u'http://www.w3.org/2000/svg'
		line = etree.Element('{%s}%s' % (svg_uri,'path'))
		
		line.set('d', 'M%f %fL%f %f' %(point1[0], point1[1], point2[0], point2[1]))
		line.set('vvpType', Types.Bone)
		line.set('body', str(idNr))
		line.set('style', 'fill:none;stroke:#c00044;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:4,2,1,2;stroke-dashoffset:0;marker-start:url(#DiamondMarker);marker-end:url(#DiamondMarker);marker-mid:url(#DiamondMarker)')
		
		line.set('direction', direction)
		
		return line
	
	def getBonePoints(self, direction, points):
		if direction == Direction.vertical:
			return self.getVerticalBonePoints(points)
		elif direction == Direction.horizontal:
			return self.getHoriziontalBonePoints(points)
		else:
			printError(direction)
			sys.stderr.write("Unknown Bone direction")
			
			
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

	def createBody(self, boned):
		for idNr, node in self.selected.iteritems():
			if node.tag == inkex.addNS('path','svg'):
				flattenPath(node, self.options.body_flatness)
				
				color = "#d4ff2a"
				
				if boned:
					direction = self.options.bone_direction
					color = "#00f7ed"
					d = node.get('d')
					points = getPoints(d)
					
					bonePoints = self.getBonePoints(direction, points)
					
					line = self.createBoneLine(bonePoints[0], bonePoints[1], idNr, direction)
					self.current_layer.append(line)
					
			
				node.set('vvpType',  Types.Body)
				node.set('density', str(self.options.body_density))
				if self.options.body_hide:
					node.set('style', 'fill:%s;fill-opacity:0.4;opacity:0;stroke:%s;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:6, 6;stroke-dashoffset:0' % (color,color))
				else:
					node.set('style', 'fill:%s;fill-opacity:0.4;stroke:%s;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:6, 6;stroke-dashoffset:0' % (color, color))
				#sys.stderr.write(pnts)
				
	def createJoint(self, jointType):
		if(len(self.selected) == 2):
			id1, node1 = self.selected.items()[0]
			id2, node2 = self.selected.items()[1]
			
			if node1.get('vvpType') == Types.Body and node2.get('vvpType') == Types.Body:
				
				point1 = avgPoint(getPoints(node1.get('d')))
				point2 = avgPoint(getPoints(node2.get('d')))
				
				line = None
				
				if jointType == JointType.DistanceJoint:
					line = self.createDistanceJointLine(id1, id2, point1, point2)
				elif jointType == JointType.RevoluteJoint:
					line = self.createRevoluteJointLine(id1, id2, point1, point2)
				
				self.current_layer.append(line)
				return
		elif len(self.selected) == 1:
			id1, node1 = self.selected.items()[0]
			
			if node1.get('vvpType') == JointType.DistanceJoint:
				self.setDistanceJointParams(node1)				
				return
			elif node1.get('vvpType') == JointType.RevoluteJoint:
				self.setRevoluteJointParams(node1)				
				return

		sys.stderr.write("You have to select exactly two bodys.")			
	
	def createRevoluteJointLine(self, id1, id2, point1, point2):
		createCircleMarker(self)
		
		svg_uri = u'http://www.w3.org/2000/svg'
		line = etree.Element('{%s}%s' % (svg_uri,'path'))
		
		self.setRevoluteJointParams(line)
			
		
		line.set('d', 'M%f %fL%f %f' %(point1[0], point1[1], point2[0], point2[1]))
		line.set('vvpType', JointType.RevoluteJoint)
		line.set('body1', str(id1))
		line.set('body2', str(id2))
		line.set('style', 'fill:none;stroke:#000ff0;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1;marker-start:url(#CircleMarker);marker-end:url(#CircleMarker);stroke-miterlimit:4;stroke-dasharray:3,3;stroke-dashoffset:0')
		
		
		return line
			
	def setRevoluteJointParams(self, node):
		if self.options.revoluteJoint_limit == '"useLimit"':
			limits = self.getLimitAngles()
			node.set('limit', '%s,%s' %(str(limits[0]), str(limits[1])))
			
		if self.options.revoluteJoint_motor == '"useMotor"':
			node.set('motor', '%s,%s' %(str(self.options.rj_motorSpeed), str(self.options.rj_maxMotorTorque)))
		
	
	def getLimitAngles(self):
		upperAngle = normalizeAngle(self.options.rj_upper)
		lowerAngle = normalizeAngle(self.options.rj_lower)
		
		return [lowerAngle, upperAngle]			
		
		
	def createDistanceJointLine(self, id1, id2, point1, point2):
		createDiamondMarker(self)
		
		svg_uri = u'http://www.w3.org/2000/svg'
		line = etree.Element('{%s}%s' % (svg_uri,'path'))
		
		self.setDistanceJointParams(line)
			
		
		line.set('d', 'M%f %fL%f %f' %(point1[0], point1[1], point2[0], point2[1]))
		line.set('vvpType', JointType.DistanceJoint)
		line.set('body1', str(id1))
		line.set('body2', str(id2))
		line.set('style', 'fill:none;stroke:#000ff0;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1;marker-start:url(#DiamondMarker);marker-end:url(#DiamondMarker);stroke-miterlimit:4;stroke-dasharray:3,3;stroke-dashoffset:0')
		
		
		return line
			
	def setDistanceJointParams(self, node):
		node.set('distance', '%#.6f,%#.6f' %(self.options.dj_distanceX,self.options.dj_distanceX))
		node.set('dampingRatio', '%#.6f' %self.options.dj_dampingRatio)
		node.set('frequencyHz', '%#.6f' %self.options.dj_frequencyHz)
		
		
	def effect(self):	
		if self.options.libgdxToolchain == '"body"':
			boned = False
			if self.options.body_sub == '"body_std"':
				boned = False
			elif self.options.body_sub == '"body_boned"':
				boned = True
		
			self.createBody(boned)
		elif self.options.libgdxToolchain == '"joint"':
			jointType = ""
			if self.options.joint == '"revoluteJoint"':
				jointType = JointType.RevoluteJoint
			elif self.options.joint == '"distanceJoint"':
				jointType = JointType.DistanceJoint
				
			self.createJoint(jointType);
		elif self.options.libgdxToolchain == '"tools"':
			if self.options.tools_sub == '"visibility"':
				visible = 1.0
				if self.options.visibility == '"hideMetaContent"':
					visible = 0.0
				elif self.options.visibility == '"showMetaContent"':
					visible = 1.0
				elif self.options.visibility == '"customVisibility"':
					visible = self.options.customVisibility
					
				setOpacityToAll(self.document, visible)
					
		

# Create effect instance and apply it.


	

if __name__ == '__main__':
	effect = LibGdxToolchain()
	effect.affect()
