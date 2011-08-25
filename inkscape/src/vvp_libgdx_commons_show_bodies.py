#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex
from vvp_libgdx_commons_inkscape import showAllBodies


class ShowAllBodies(inkex.Effect):

	def __init__(self):

		# Call the base class constructor.
		inkex.Effect.__init__(self)


	def effect(self):
		showAllBodies(self.document)
		
		

# Create effect instance and apply it.

if __name__ == '__main__':
	effect = ShowAllBodies()
	effect.affect()
