#!/usr/bin/env python

import inkex, cubicsuperpath, cspsubdiv, simplepath
from simplestyle import parseStyle, formatStyle


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