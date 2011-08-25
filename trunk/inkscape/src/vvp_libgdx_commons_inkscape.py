#!/usr/bin/env python

def hideAll

def flattenPath(nod, flat):
	if node.tag == inkex.addNS('path','svg'):
		d = node.get('d')
		p = cubicsuperpath.parsePath(d)
		cspsubdiv.cspsubdiv(p, flat)
		np = []
		pnts = ''
		for sp in p:
			first = True
			for csp in sp:
				cmd = 'L'
				if first:
					cmd = 'M'
				first = False
				pnts += str(csp[1][0]) + ',' + str(csp[1][1]) + '|'
				np.append([cmd,[csp[1][0],csp[1][1]]])
				
		node.set('d',simplepath.formatPath(np))
		return pnts