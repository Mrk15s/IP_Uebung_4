/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline;

/**
 * Implementation of potrace part 2 to find polygons based on straight pathes and possible segments.
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 30.11.2015
 */
public class PotracePolygonFinder implements IPolygonFinder {

	/* (non-Javadoc)
	 * @see de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.IPolygonFinder#findStraightPathes(de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline)
	 */
	@Override
	public Vertex[] findStraightPathes(Outline givenOutline) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.IPolygonFinder#findPossibleSegments(de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex[])
	 */
	@Override
	public Vertex[] findPossibleSegments(Vertex[] closedStraigthPathes) {
		// TODO Auto-generated method stub
		return null;
	}

}
