/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D;
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
	 * @return an array of pivot elements: Array of outlineVertex index -> first index that terminates straight path.
	 * @throws IllegalArgumentException if the given outline doesn't contain any vertex
	 */
	int[] findStraightPathes(Outline givenOutline);

	/**
	 * Find 
	 * @param closedStraigthPathes
	 * @return
	 * @throws IllegalArgumentException if the last {@link Vertex} in closedStraightPathes is not equal to the first {@link Vertex} (= path is not closed).
	 */
	Vector2D[] findPossibleSegments(int[] closedStraigthPathes);
}
