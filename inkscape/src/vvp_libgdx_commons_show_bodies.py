#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, cubicsuperpath, simplepath, cspsubdiv, sys, string
import flatten
# The simplestyle module provides functions for style parsing.
from simplestyle import *

class ShowAllBodies(inkex.Effect):
	"""
	Example Inkscape effect extension.
	Creates a new layer with a "Hello World!" text centered in the middle of the document.
	"""
	def __init__(self):

		# Call the base class constructor.
		inkex.Effect.__init__(self)


	def effect(self):
			
		for node in self.document.xpath('//svg:path[@isBody=\'1\']', namespaces=inkex.NSS):
			node.set('opacity', '1')
		
		

# Create effect instance and apply it.

if __name__ == '__main__':
	effect = ShowAllBodies()
	effect.affect()
