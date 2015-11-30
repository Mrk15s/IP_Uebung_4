/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline;

/**
 * Interface for the polygon finding algorithm (Potrace part 2).
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 30.11.2015
 */
public interface IPolygonFinder {
	
	/**
	 * Find all straight pathes on the given outline.
	 * 
	 * A straigt path is an array of {@link Vertex} that fullfill the straigt path constraints.
	 * 
	 * @param givenOutline
	 * @return
	 */
	Vertex[] findStraightPathes(Outline givenOutline);

	/**
	 * Find 
	 * @param straigtPathes
	 * @return
	 * @throws IllegalArgumentException if the last {@link Vertex} in closedStraightPathes is not equal to the first {@link Vertex} (= path is not closed).
	 */
	Vertex[] findPossibleSegments(Vertex[] closedStraigthPathes);
}
