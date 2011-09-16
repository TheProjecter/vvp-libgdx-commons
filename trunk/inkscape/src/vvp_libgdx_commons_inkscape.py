#!/usr/bin/env python

import inkex, cubicsuperpath, cspsubdiv, simplepath, re
from simplestyle import parseStyle, formatStyle
import xml.dom.minidom as dom


def hideAllBodies(document):
	setOpacityToAll(document, 0)
			
def showAllBodies(document):
	setOpacityToAll(document, 1)
			
def setOpacityToAll(document, opacity):
	for node in document.xpath('//svg:path[@isBody=\'1\']', namespaces=inkex.NSS):
		if node.get('isBody') == '1':
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
	
def parsePoint(pntStr):
	reDec = re.compile(r"\d+([.]\d+)?")
		
	l = reDec.finditer(pntStr)	
	x = float(l.next().group())
	y = float(l.next().group())
		
	return [x, y]
	
class DefXml:
	rootElement = dom.Element("vvp_libgdx_file")
	
	def toXml(self):
		return self.rootElement.toprettyxml()
	
	def addBody(self, id, points):
		bodyElement = dom.Element("body")
		#bodyElement.setAttribute("id", id)
		
		for point in points:
			pointElement = dom.Element("point")
			
			pointElement.setAttribute("x", str(point[0]))
			pointElement.setAttribute("y", str(point[1]))
			
			bodyElement.appendChild(pointElement)
			
		self.rootElement.appendChild(bodyElement)
	

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
		reDec = re.compile(r"\d+([.]\d+)?")
		
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