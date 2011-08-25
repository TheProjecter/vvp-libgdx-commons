#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex
# The simplestyle module provides functions for style parsing.
from vvp_libgdx_commons_inkscape import flattenPath

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
			
		self.OptionParser.add_option("-H", "--hide",
            action="store", type="inkbool", 
            dest="hide", default=False,
            help="hide the path")

	def effect(self):
		for id, node in self.selected.iteritems():
			if node.tag == inkex.addNS('path','svg'):
				flattenPath(node, self.options.flat)
			
				node.set('isBody', '1')
				if self.options.hide:
					node.set('style', 'fill:#d4ff2a;fill-opacity:0.06787331;opacity:0;stroke:#c4f700;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:6, 6;stroke-dashoffset:0')
				else:
					node.set('style', 'fill:#d4ff2a;fill-opacity:0.06787331;stroke:#c4f700;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:6, 6;stroke-dashoffset:0')
				#sys.stderr.write(pnts)
		
		

# Create effect instance and apply it.


	

if __name__ == '__main__':
	effect = MarkBodies()
	effect.affect()
