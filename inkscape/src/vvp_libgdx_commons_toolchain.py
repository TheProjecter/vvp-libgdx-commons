#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, sys
# The simplestyle module provides functions for style parsing.
from vvp_libgdx_commons_inkscape import flattenPath, createDiamondMarker,\
	getPoints
from lxml import etree

class Direction:
	horizontal="horizontal"
	vertical="vertical"

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
		
		self.OptionParser.add_option("--density",
			action="store", type="float", 
			dest="density", default=10.0,
			help="density")
		
		self.OptionParser.add_option("--boned",
            action="store", type="inkbool", 
            dest="boned", default=False,
            help="create boned body")
			
		self.OptionParser.add_option("-H", "--hide",
            action="store", type="inkbool", 
            dest="hide", default=False,
            help="hide the path")
		
		self.OptionParser.add_option("--direction",
            action="store", type="string", 
            dest="direction", default='vertical',
            help="direction")
		
		self.OptionParser.add_option("--markbodies")

	def effect(self):	
		print ""
		
		

# Create effect instance and apply it.


	

if __name__ == '__main__':
	effect = MarkBodies()
	effect.affect()
