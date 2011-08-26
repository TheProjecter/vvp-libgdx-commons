#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, tempfile, os, zipfile, sys, shutil

class ExportBody(inkex.Effect):
	"""
	Example Inkscape effect extension.
	Creates a new layer with a "Hello World!" text centered in the middle of the document.
	"""
	def __init__(self):

		# Call the base class constructor.
		inkex.Effect.__init__(self)
		
		if os.name == 'nt':
			self.encoding = "cp437"
		else:
			self.encoding = "latin-1"


	def effect(self):
		docroot = self.document.getroot()
		docname = docroot.get(inkex.addNS('docname',u'sodipodi'))
		#inkex.errormsg(_('Locale: %s') % locale.getpreferredencoding())
		if docname is None:
			docname = self.args[-1]
			# TODO: replace whatever extention
		docname = os.path.basename(docname.replace('.zip', ''))
		docname = docname.replace('.svg', '')
		docname = docname.replace('.svgz', '')
		# Create os temp dir
		self.tmp_dir = tempfile.mkdtemp()
		# Create destination zip in same directory as the document
		self.zip_file = os.path.join(self.tmp_dir, docname) + '.zip'
		z = zipfile.ZipFile(self.zip_file, 'w')
		
		dst_file = os.path.join(self.tmp_dir, docname)
		stream = open(dst_file,'w')
		self.document.write(stream)
		stream.close()
		z.write(dst_file,docname.encode(self.encoding)+'.svg')

		z.close()
		
	def output(self):
		'''
		Writes the temporary compressed file to its destination
		and removes the temporary directory.
		'''
		out = open(self.zip_file,'rb')
		if os.name == 'nt':
			try:
				import msvcrt
				msvcrt.setmode(1, os.O_BINARY)
			except:
				pass
		sys.stdout.write(out.read())
		out.close()
		shutil.rmtree(self.tmp_dir)
		
		
# Create effect instance and apply it.

if __name__ == '__main__':
	effect = ExportBody()
	effect.affect()
