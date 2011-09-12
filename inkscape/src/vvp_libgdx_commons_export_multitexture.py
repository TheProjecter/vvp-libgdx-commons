#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, tempfile, os, zipfile, sys, shutil, re

import xml.dom.minidom as dom

from subprocess import Popen, PIPE

class DefXml:
	rootElement = dom.Element("vvp_libgdx_file")
	
	def toXml(self):
		return self.rootElement.toprettyxml()
	
	def addBody(self, points):
		bodyElement = dom.Element("body")
		
		for point in points:
			pointElement = dom.Element("point")
			
			pointElement.setAttribute("x", str(point[0]))
			pointElement.setAttribute("y", str(point[1]))
			
			bodyElement.appendChild(pointElement)
			
		self.rootElement.appendChild(bodyElement)
	

class SvgSize:
	inchPerMm = 25.4
	dpi = 90
	pxPerPt = 1.25
	
	def __init__(self, width, height):
		self.width = self.toPx(width)
		self.height = self.toPx(height)
			
	def toPx(self, value):
		reMm = re.compile(r"\d+([.]\d+)?mm")
		rePt = re.compile(r"\d+([.]\d+)?pt")
		
		ret = 1
		
		match = reMm.match(value)
		
		if match != None and match.group() == value:
			value = self.toDec(value)
			
			value = value / self.inchPerMm
			ret = value * self.dpi
		else:
			match = rePt.match(value)
			if match != None and match.group() == value:
				value = self.toDec(value)
			
				ret = value * self.pxPerPt
			else:
				ret = value
				
		ret = int(ret)
		
		return ret
	
	def roundUpToPowerOfTwo(self):
		x = 2
		
		while (True):
			if x > self.width:
				self.width = x
				break			
			
			x = 2*x
			
		x = 2
		
		while (True):
			if x > self.height:
				self.height = x
				break			
			
			x = 2*x


	def toDec(self, value):
		reDec = re.compile(r"\d+([.]\d+)?")
		
		ret = 1.0
		
		match = reDec.match(value)
		if match != None:
			ret = float(match.group())
			
		return ret
	
	def isValid(self):
		if self.width > 20000 or self.height > 20000:
			return False
		else:
			return True


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
		rePoint = re.compile(r"[\sL]\d+([.]\d+)?[\s,]\d+([.]\d+)?")
		defXml = DefXml()
		
		for result in self.document.xpath('//svg:path[@isBody=\'1\']/@d', namespaces=inkex.NSS):
			
			l = rePoint.finditer(str(result))
			
			points = []
			
			lastPoint = []
			
			for match in l:
				point = self.parsePoint(match.group())
				
				if point != lastPoint:
					points.append(point)
					lastPoint = point
				
			defXml.addBody(points)
			
			
		return defXml.toXml()
	
	def parsePoint(self, pntStr):
		reDec = re.compile(r"\d+([.]\d+)?")
		
		l = reDec.finditer(pntStr)
		
		x = float(l.next().group())
		y = float(l.next().group())
		
		return [x, y]
	
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
