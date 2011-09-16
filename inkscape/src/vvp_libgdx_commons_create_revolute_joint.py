#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, sys

class CreateRevoluteJoint(inkex.Effect):
	"""
	Example Inkscape effect extension.
	Creates a new layer with a "Hello World!" text centered in the middle of the document.
	"""
	def __init__(self):

		# Call the base class constructor.
		inkex.Effect.__init__(self)

	def effect(self):
		if(len(self.selected) == 2):
			id1, node1 = self.selected[0]
			id2, node2 = self.selected[1]
		else:
			sys.stderr.write("You have to select exactly two bodys.");
		
#		for id, node in self.selected.iteritems():
#			if node.tag == inkex.addNS('path','svg'):
#				flattenPath(node, self.options.flat)
#			
#				node.set('vvpType', 'Body')
#				if self.options.hide:
#					node.set('style', 'fill:#d4ff2a;fill-opacity:0.4;opacity:0;stroke:#c4f700;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:6, 6;stroke-dashoffset:0')
#				else:
#					node.set('style', 'fill:#d4ff2a;fill-opacity:0.4;stroke:#c4f700;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:6, 6;stroke-dashoffset:0')


	

if __name__ == '__main__':
	effect = CreateRevoluteJoint()
	effect.affect()
