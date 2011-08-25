#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex

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
					node.set('isBody', 0)
				
		
		

# Create effect instance and apply it.


	

if __name__ == '__main__':
	effect = UnmarkBodies()
	effect.affect()
