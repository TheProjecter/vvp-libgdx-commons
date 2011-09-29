#!/usr/bin/env python


# We will use the inkex module with the predefined Effect base class.
import inkex, tempfile, os, zipfile, sys, shutil

from vvp_libgdx_commons_inkscape import SvgSize, DefXml, getPoints, JointType,\
	getParams, getAllFloats, Types, printError

from subprocess import Popen, PIPE
from vvp_libgdx_commons_toolchain import Direction


class ExportBody(inkex.Effect):
	textureFileName = "texture.png"
	defFileName = "def.xml"
	svgFileName = "texture.svg"
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
		
		#z.write(self.args[-1], self.svgFileName)
		
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
		p.wait()
		f = p.stdout

		f.close()	
		
		
		
	def exportPaths(self):
		
		defXml = DefXml()
		
		for node in self.document.xpath('//svg:path[@vvpType=\'%s\']' %Types.Body, namespaces=inkex.NSS):
			idNum = node.get('id')
			d = node.get('d')
			
			points = getPoints(d)
			
			bones = self.document.xpath('//svg:path[@vvpType=\'%s\' and @body=\'%s\']' %(Types.Bone, idNum), namespaces=inkex.NSS)
			
			bone = None
			if len(bones) >= 1:
				bone = bones[0]
				boneD = bone.get('d')
				params = getParams(bone, ['direction'])
				direction = params['direction']
				
				bonePoints = getPoints(boneD)
				
				bonePoints = self.checkBonePoints(bonePoints, direction)
				
				if len(bonePoints) > 1:
					bone = defXml.createBone(bonePoints, params)
				else:
					bone = None
				
			defXml.addBody(str(idNum), points, getParams(node, ['density']), bone)
			
		for node in self.document.xpath('//svg:path[@vvpType=\'%s\']' %JointType.RevoluteJoint, namespaces=inkex.NSS):
			d = node.get('d')
			idNum = node.get('id')
			idBody1 = node.get('body1')
			idBody2 = node.get('body2')
			limit = node.get('limit')
			motor = node.get('motor')
			
			points = getPoints(d)
			
			if len(points) >= 2:
				point1 = points[0]
				point2 = points[len(points) - 1]
				defXml.addRevoluteJoint(idBody1, idBody2, point1, point2, limit, motor)
				
				
		for node in self.document.xpath('//svg:path[@vvpType=\'%s\']' %JointType.DistanceJoint, namespaces=inkex.NSS):
			d = node.get('d')
			idNum = node.get('id')
			idBody1 = node.get('body1')
			idBody2 = node.get('body2')
			
			points = getPoints(d)
			
			if len(points) >= 2:
				point1 = points[0]
				point2 = points[len(points) - 1]
				distance = getParams(node, ['distance'])['distance']
				distance = getAllFloats(distance)
				params = getParams(node, ['dampingRatio', 'frequencyHz'])
				params['distanceX'] = distance[0]
				params['distanceY'] = distance[1]
				defXml.addDistanceJoint(idBody1, idBody2, point1, point2, params)
			
			
		return defXml.toXml()
	
	def checkBonePoints(self, points, direction):
		indexX = 0
		
		if direction == Direction.horizontal:
			indexX = 0
		elif direction == Direction.vertical:
			indexX = 1
		
		lastValue = None
		first = True
		
		for point in points:
			if lastValue == None:
				lastValue = point[indexX]
				first = True
			else:
				if point[indexX] > lastValue:
					lastValue = point[indexX]
				elif first == True:
					return self.checkBonePoints(reversed(points), direction)
				else:
					printError("Invalid Bone Path.")
					return []
				first = False
				
		return points
				
	
	
	def logError(self, text):
		self.errorStr += '\n' + text
		printError('%s\n' %text)
			
		
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
