#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex

class ExportBody(inkex.Effect):
	"""
	Example Inkscape effect extension.
	Creates a new layer with a "Hello World!" text centered in the middle of the document.
	"""
	def __init__(self):

		# Call the base class constructor.
		inkex.Effect.__init__(self)


	def effect(self):
		print self.svg_file
# Create effect instance and apply it.

if __name__ == '__main__':
	effect = ExportBody()
	effect.affect()
