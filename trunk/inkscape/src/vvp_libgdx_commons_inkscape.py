#!/usr/bin/env python

import inkex, cubicsuperpath, cspsubdiv, simplepath, re
from simplestyle import parseStyle, formatStyle
import xml.dom.minidom as dom


def hideAllBodies(document):
	setOpacityToAll(document, 0)
			
def showAllBodies(document):
	setOpacityToAll(document, 1)
			
def setOpacityToAll(document, opacity):
	for node in document.xpath('//svg:path[@vvpType=\'Body\']', namespaces=inkex.NSS):
		setStyle(node, 'opacity', str(opacity))
			
def setStyle(node, key, value):
	style = parseStyle(node.get('style'))
	style[str(key)] = str(value)
	node.set('style', formatStyle(style))
	
	
def getStyle(node, key):
	return parseStyle(node.get('style'))[str(key)]
			
def flattenPath(node, flat):
	if node.tag == inkex.addNS('path','svg'):
		d = node.get('d')
		
		p = cubicsuperpath.parsePath(d)
		cspsubdiv.cspsubdiv(p, flat)
		np = []
		pnts = ''
		for sp in p:
			first = True
			for csp in sp:
				cmd = 'L'
				if first:
					cmd = 'M'
				first = False
				pnts += str(csp[1][0]) + ',' + str(csp[1][1]) + '|'
				np.append([cmd,[csp[1][0],csp[1][1]]])
				
		node.set('d',simplepath.formatPath(np))
		return pnts
	
	
def getPoints(d):
	p = cubicsuperpath.parsePath(d)
	
	points = []
	lastPoint = []
	
	for sp in p:
		for csp in sp:
			point = [csp[1][0], csp[1][1]]
			if point != lastPoint:
				points.append(point)
				lastPoint = point
				
	lastPoint = points.pop()
	if lastPoint != points[0]:
		points.append(lastPoint)
			
	return points
	
	
def avgPoint(points):
	n = 0
	x = 0
	y = 0
	for point in points:
		x += point[0]
		y += point[1]
		n += 1
		
	return [x/n, y/n]
	
class DefXml:
	rootElement = dom.Element("vvp_libgdx_file")
	
	def toXml(self):
		return self.rootElement.toprettyxml()
	
	def addBody(self, idNum, points):
		bodyElement = dom.Element("body")
		bodyElement.setAttribute("id", idNum)
		
		for point in points:
			bodyElement.appendChild(self.createPoint(point))
			
		self.rootElement.appendChild(bodyElement)
		
	def createPoint(self, point, idBody=None):
		pointElement = dom.Element("point")
			
		pointElement.setAttribute("x", str(point[0]))
		pointElement.setAttribute("y", str(point[1]))
		
		if idBody != None:
			pointElement.setAttribute('body', idBody)
		
		return pointElement
	
	def addRevoluteJoint(self, idBody1, idBody2, point1, point2):
		jointElement = self.createJoint()
		
		jointElement.setAttribute('type', 'Revolute')
		jointElement.appendChild(self.createPoint(point2, idBody2))
		jointElement.appendChild(self.createPoint(point1, idBody1))
		
		self.rootElement.appendChild(jointElement)
	
	def createJoint(self):
		jointElement = dom.Element("joint")
		
		return jointElement
	

class SvgSize:
	inchPerMm = 25.4
	dpi = 90
	pxPerPt = 1.25
	
	def __init__(self, width, height):
		self.width = self.toPx(width)
		self.height = self.toPx(height)
			
	def toPx(self, value):
		reMm = re.compile(r"\d+([.]\d+)?mm")
		rePt = re.compile(r"\d+([.]\d+)?pt")
		
		ret = 1
		
		match = reMm.match(value)
		
		if match != None and match.group() == value:
			value = self.toDec(value)
			
			value = value / self.inchPerMm
			ret = value * self.dpi
		else:
			match = rePt.match(value)
			if match != None and match.group() == value:
				value = self.toDec(value)
			
				ret = value * self.pxPerPt
			else:
				ret = value
				
		ret = int(ret)
		
		return ret
	
	def roundUpToPowerOfTwo(self):
		x = 2
		
		while (True):
			if x >= self.width:
				self.width = x
				break			
			
			x = 2*x
			
		x = 2
		
		while (True):
			if x >= self.height:
				self.height = x
				break			
			
			x = 2*x


	def toDec(self, value):
		reDec = re.compile(r"[-]?\d+([.]\d+)?")
		
		ret = 1.0
		
		match = reDec.match(value)
		if match != None:
			ret = float(match.group())
			
		return ret
	
	def isValid(self):
		if self.width > 20000 or self.height > 20000:
			return False
		else:
			return True