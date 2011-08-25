#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex
from jessyInk_export import setStyle

class UnmarkBodies(inkex.Effect):
	"""
	Example Inkscape effect extension.
	Creates a new layer with a "Hello World!" text centered in the middle of the document.
	"""
	def __init__(self):

		# Call the base class constructor.
		inkex.Effect.__init__(self)

	def effect(self):
		for id, node in self.selected.iteritems():
			if node.tag == inkex.addNS('path','svg'):
				
				if node.get('isBody') == '1':
					node.set('isBody', '0')
					setStyle(node, 'fill', '#ff522a')
					setStyle(node, 'stroke', '#f72f00')
				
		
		

# Create effect instance and apply it.


	

if __name__ == '__main__':
	effect = UnmarkBodies()
	effect.affect()
