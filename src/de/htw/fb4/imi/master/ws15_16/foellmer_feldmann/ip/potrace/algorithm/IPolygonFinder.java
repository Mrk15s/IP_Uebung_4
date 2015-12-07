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
 * This interface follows the template method pattern. Therefore, each client should call the methods in the following order:
 * 
 * 1. findStraightPathes()
 * 2. findPossibleSegments()
 * 3. findOptimalPolygon()
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 30.11.2015
 */
public interface IPolygonFinder {
	
	/**
	 * This method should be called first.
	 * 
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
	 * This method should be called after straight pathes were calculated by findStraightPath().
	 * 
	 * Find possible segments within the outline that was set in findStraightPath().
	 * 
	 * A possible segment is an array of integer indicating the maximum allowed segment index-
	 * 
	 * @param closedStraigthPathes
	 * @return
	 * @throws IllegalArgumentException if the last {@link Vertex} in closedStraightPathes is not equal to the first {@link Vertex} (= path is not closed).
	 */
	int[] findPossibleSegments(int[] closedStraigthPathes);
	
	/**
	 * This method should be called after the possible segments were found by findPossibleSegments().
	 * 
	 * Find the optimal polygon out of the given possible segments.
	 * 
	 * E.g.: the optimal polygon can be the one with the smallest number of segments.
	 * 
	 * @param possibleSegments
	 * @return
	 */
	Vector2D[] findOptimalPolygon(int[]possibleSegments);
}
