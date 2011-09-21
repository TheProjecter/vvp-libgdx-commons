#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, tempfile, os, zipfile, sys, shutil, re

from vvp_libgdx_commons_inkscape import SvgSize, DefXml, getPoints, JointType

from subprocess import Popen, PIPE


class ExportBody(inkex.Effect):
	textureFileName = "texture.png"
	defFileName = "def.xml"
	errorFileName = "errors.txt"
	errorStr = ""
	
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
		
		#hideAllBodies(self.document)
		
		self.size = SvgSize(str(self.document.xpath('/svg:svg/@width', namespaces=inkex.NSS)[0]), str(self.document.xpath('/svg:svg/@height', namespaces=inkex.NSS)[0]))
		self.size.roundUpToPowerOfTwo()
		
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
		
		tmpTexture = os.path.join(self.tmp_dir, self.textureFileName) 
		# Create destination zip in same directory as the document
		self.zip_file = os.path.join(self.tmp_dir, docname) + '.zip'
		z = zipfile.ZipFile(self.zip_file, 'w')
		
		
		
		#dst_file = os.path.join(self.tmp_dir, docname)
		#stream = open(dst_file,'w')
		#self.document.write(stream)
		#stream.close()
		
		if(self.size.isValid()):
			self.exportPng(tmpTexture)
			z.write(tmpTexture, self.textureFileName)
		else:
			self.logError("Size is invalid: " + str(self.size.width) + "/" + str(self.size.height))
			
		z.writestr(self.defFileName, self.exportPaths())
		
		if self.errorStr != "":
			z.writestr(self.errorFileName, self.errorStr)

		z.close()
		
	def exportPng(self, filename):
		'''
		Runs inkscape's command line interface and exports the image
        slice from the 4 coordinates in s, and saves as the filename
        given.
        '''
		svg_file = self.args[-1]
		command = "inkscape -a %s:%s:%s:%s -e \"%s\" \"%s\" " % (0, 0, self.size.width, self.size.height, filename, svg_file)

		p = Popen(command, shell=True, stdout=PIPE, stderr=PIPE)
		return_code = p.wait()
		f = p.stdout
		err = p.stderr

		f.close()	
		
		
		
	def exportPaths(self):
		
		defXml = DefXml()
		
		for idNum in self.document.xpath('//svg:path[@vvpType=\'Body\']/@id', namespaces=inkex.NSS):
			
			result = self.xpathSingle('//svg:path[@id=\'' + str(idNum) + '\']/@d')
			
			points = getPoints(result)
				
			defXml.addBody(str(idNum), points)
			
		for node in self.document.xpath('//svg:path[@vvpType=\'%s\']' %JointType.RevoluteJoint, namespaces=inkex.NSS):
			d = node.get('d')
			idNum = node.get('id')
			idBody1 = node.get('body1')
			idBody2 = node.get('body2')
			
			points = getPoints(d)
			
			if len(points) >= 2:
				point1 = points[0]
				point2 = points[len(points) - 1]
				defXml.addRevoluteJoint(idBody1, idBody2, point1, point2)
			
			
		return defXml.toXml()
	
	def logError(self, text):
		self.errorStr += '\n' + text 
			
		
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
