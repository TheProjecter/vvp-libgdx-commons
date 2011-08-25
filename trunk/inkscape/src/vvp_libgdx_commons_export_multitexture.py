#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, cubicsuperpath, simplepath, cspsubdiv, sys, string
import flatten
# The simplestyle module provides functions for style parsing.
from simplestyle import *

class ExportBody(inkex.Effect):
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
			
		self.OptionParser.add_option("-H", "--hide",
            action="store", type="inkbool", 
            dest="hide", default=True,
            help="hide the path")

	def effect(self):
		widthH = float(str(self.document.xpath('/svg:svg/@width', namespaces=inkex.NSS)[0]))/2
		heightH = float(str(self.document.xpath('/svg:svg/@height', namespaces=inkex.NSS)[0]))/2
		

		for id, node in self.selected.iteritems():
			if node.tag == inkex.addNS('path','svg'):
				d = node.get('d')
				p = cubicsuperpath.parsePath(d)
				cspsubdiv.cspsubdiv(p, self.options.flat)
				np = []
				pnts = ''
				for sp in p:
					first = True
					for csp in sp:
						cmd = 'L'
						if first:
							cmd = 'M'
						first = False
						pnts += str((csp[1][0]-widthH)/widthH) + ',' + str((csp[1][1]-heightH)/heightH) + ','
						np.append([cmd,[csp[1][0],csp[1][1]]])
						node.set('d',simplepath.formatPath(np))
				
				node.set('body_vertices', pnts)
				node.set('isBody', '1')
				if self.options.hide:
					node.set('opacity', '0')
				#sys.stderr.write(pnts)
		
		

# Create effect instance and apply it.

if __name__ == '__main__':
	effect = ExportBody()
	effect.affect()
