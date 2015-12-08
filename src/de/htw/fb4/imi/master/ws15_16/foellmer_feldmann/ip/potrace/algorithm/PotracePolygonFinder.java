/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Factory;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.util.VectorUtil;

/**
 * Implementation of potrace part 2 to find polygons based on straight pathes
 * and possible segments.
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 30.11.2015
 */
public class PotracePolygonFinder implements IPolygonFinder {
	public static final int NO_POSSIBLE_SEGMENT = -1;
	/**
	 * all vertices on the outline (black pixels).
	 */
	private Vertex[] outlineVertices;
	/**
	 * constraint 0
	 */
	private Vector2D c0;
	/**
	 * constraint 1
	 */
	private Vector2D c1;

	/**
	 * Array of outlineVertex index -> first index that terminates straight
	 * path.
	 */
	private int[] pivots;

	private boolean directionTop;
	private boolean directionLeft;
	private boolean directionBottom;
	private boolean directionRight;

	private Vector2D[] currentOptimum;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.
	 * IPolygonFinder#findStraightPathes(de.htw.fb4.imi.master.ws15_16.
	 * foellmer_feldmann.ip.potrace.models.Outline)
	 */
	@Override
	public int[] findStraightPathes(Outline givenOutline) {
		if (doesntContainAnyVertex(givenOutline)) {
			throw new IllegalArgumentException("The given outline is empty.");
		}

		this.outlineVertices = givenOutline.getVertices();
		this.pivots = new int[outlineVertices.length];

		int startI = getStartVertexIndex(givenOutline);

		for (int i = startI; i < outlineVertices.length; i++) {
			findStraightPath(i);
		}

		return this.pivots;
	}

	private boolean doesntContainAnyVertex(Outline givenOutline) {
		return givenOutline.getNumberOfVertices() == 0;
	}

	private int getStartVertexIndex(Outline givenOutline) {
		return 0;
	}

	private void findStraightPath(int startI) {
		Vertex startVertex = this.outlineVertices[startI];
		this.c0 = getStartConstraint();
		this.c1 = getStartConstraint();
		this.directionTop = false;
		this.directionLeft = false;
		this.directionBottom = false;
		this.directionRight = false;

		for (int i = startI + 1; i < outlineVertices.length; i++) {
			Vertex currentVertex = outlineVertices[i];
			storeDirection(currentVertex, this.outlineVertices[i - 1]);

			Vector2D currentVector = Factory.newVector2D(startVertex, currentVertex);

			if (
					moreThanThreeDirections(currentVector) || 
					abusesConstraint(currentVector)) {
				this.pivots[startI] = i; // save first index of vertex that
											// terminates straight path of given
											// startVertex
				return;
			}

			actualizeConstraint(currentVector);
		}
	}

	private void storeDirection(Vertex currentVertex, Vertex lastVertex) {
		if (currentVertex.isAboveOf(lastVertex)) {
			this.directionTop = true;
		}
		if (currentVertex.isLeftOf(lastVertex)) {
			this.directionLeft = true;
		}
		if (currentVertex.isRightOf(lastVertex)) {
			this.directionRight = true;
		}
		if (currentVertex.isBelowOf(lastVertex)) {
			this.directionBottom = true;
		}

	}

	private Vector2D getStartConstraint() {
		return Factory.newVector2D(0, 0);
	}

	private boolean moreThanThreeDirections(Vector2D currentVector) {
		int numberDirections = 0;

		if (directionTop) {
			numberDirections++;
		}

		if (directionLeft) {
			numberDirections++;
		}

		if (directionRight) {
			numberDirections++;
		}

		if (directionBottom) {
			numberDirections++;
		}

		return numberDirections > 3;
	}

	private boolean abusesConstraint(Vector2D currentVector) {
		if (VectorUtil.calcCrossProduct(c0, currentVector) < 0 || VectorUtil.calcCrossProduct(c1, currentVector) > 0) {
			return true;
		}

		return false;
	}

	private void actualizeConstraint(Vector2D currentVector) {
		if (Math.abs(currentVector.getX()) <= 1 && Math.abs(currentVector.getY()) <= 1) {
			return; // do not change constraint
		}

		calcAndSetNewConstraints(currentVector);
	}

	private void calcAndSetNewConstraints(Vector2D currentVector) {
		calcAndSetNewC0(currentVector);
		calcAndSetNewC1(currentVector);
	}

	protected void calcAndSetNewC0(Vector2D currentVector) {
		int dX;
		int dY;

		if (currentVector.getY() >= 0 && (currentVector.getY() > 0 || currentVector.getX() < 0)) {
			dX = currentVector.getX() + 1;
		} else {
			dX = currentVector.getX() - 1;
		}

		if (currentVector.getX() <= 0 && (currentVector.getX() < 0 || currentVector.getY() < 0)) {
			dY = currentVector.getY() + 1;
		} else {
			dY = currentVector.getY() - 1;
		}

		Vector2D d = Factory.newVector2D(dX, dY);

		if (VectorUtil.calcCrossProduct(c0, d) >= 0) {
			c0 = d;
		} // else: do not change constraint c0
	}

	private void calcAndSetNewC1(Vector2D currentVector) {
		int dX;
		int dY;

		if (currentVector.getY() <= 0 && (currentVector.getY() < 0 || currentVector.getX() < 0)) {
			dX = currentVector.getX() + 1;
		} else {
			dX = currentVector.getX() - 1;
		}

		if (currentVector.getX() >= 0 && (currentVector.getX() >= 0 || currentVector.getY() < 0)) {
			dY = currentVector.getY() + 1;
		} else {
			dY = currentVector.getY() - 1;
		}

		Vector2D d = Factory.newVector2D(dX, dY);

		if (VectorUtil.calcCrossProduct(c1, d) <= 0) {
			c1 = d;
		} // else: do not change constraint c1
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.
	 * IPolygonFinder#findPossibleSegments(int[])
	 */
	@Override
	public int[] findPossibleSegments(final int[] closedStraigthPathes) {
		int[] possibleSegments = new int[closedStraigthPathes.length];

		// for (int i = 0; i < closedStraigthPathes.length; i++) {
		// int j = findNextPossibleSegment(i, closedStraigthPathes);
		//
		// possibleSegments[i] = j;
		// }
		 for (int i = 0; i < closedStraigthPathes.length; i++) {
			 int previous = (i <= 0 ? closedStraigthPathes.length - 1 : i - 1);
			 int jPlus = closedStraigthPathes[previous] == 0 ?
			 closedStraigthPathes.length - 1 : previous;
			
			 int j = closedStraigthPathes[jPlus] - 1;
			
			 possibleSegments[i] = j;
		 }

		 // TODO uncomment the following line to activate possible segments
//		return possibleSegments;
		return closedStraigthPathes;
	}

	private int findNextPossibleSegment(int i, int[] closedStraigthPathes) {
		int nextPossibleIndex = NO_POSSIBLE_SEGMENT;

		if (i - 1 <= 0 || !isStraight(i, i - 1, closedStraigthPathes)) {
			nextPossibleIndex = NO_POSSIBLE_SEGMENT;
		} else {
			for (int j = i + 1; j < closedStraigthPathes.length - 3; j++) {
				if (!isStraight(i, j, closedStraigthPathes)) {
					nextPossibleIndex = j - 1;
					break;
				}
			}
		}

		return nextPossibleIndex;
	}

	private boolean isStraight(int currentI, int otherI, int[] closedStraigthPathes) {
		return closedStraigthPathes[currentI] == closedStraigthPathes[otherI]; // both
																				// vertices
																				// on
																				// outline
																				// have
																				// the
																				// same
																				// straight
																				// path
																				// terminator
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.
	 * IPolygonFinder#findOptimalPolygon(int[])
	 */
	public Vector2D[] findOptimalPolygon(int[] possibleSegments) {
		return getOptimalPolygon(possibleSegments);
	}

	private Vector2D[] getOptimalPolygon(int[] possibleSegments) {
		Set<Vector2D> bestPolygon = new HashSet<>();

		for (int j = 0; j < 1; j++) {
			Set<Vector2D> newPolygon = buildPolygon(possibleSegments, j);

			if (better(newPolygon, bestPolygon)) {
				bestPolygon = newPolygon;
			}
		}

		return bestPolygon.toArray(new Vector2D[bestPolygon.size()]);
	}

	private boolean better(Set<Vector2D> newPolygon, Set<Vector2D> bestPolygon) {
		return bestPolygon.size() == 0 || (newPolygon.size() < bestPolygon.size() && newPolygon.size() != 0);
	}

	protected Set<Vector2D> buildPolygon(int[] possibleSegments, int startIndex) {
		Set<Vector2D> polygon;
		polygon = new HashSet<>();
		Vertex startVertex = this.outlineVertices[startIndex];
		Vertex lastVertex = startVertex; // set start vertex
		Set<Integer> processed = new HashSet<>();

		boolean first = true;
		for (int i = startIndex; i < possibleSegments.length; i++) {			
			int nextPossibleVertexIndex = possibleSegments[i];
			
			if ((!first && nextPossibleVertexIndex == 0)) {
				break;
			}
						
			first = false;
			Vertex currentVertex = this.outlineVertices[nextPossibleVertexIndex];
			processed.add(i);

			if (!currentVertex.equals(lastVertex)) {
				addConnection(polygon, lastVertex, currentVertex);

				lastVertex = currentVertex;
				i = nextPossibleVertexIndex;
			}
		}

		// connect lastVertex and startVertex
		if (!startVertex.equals(lastVertex)) {
			addConnection(polygon, lastVertex, startVertex);
		}
		return polygon;
	}

	protected void addConnection(Set<Vector2D> polygon, Vertex lastVertex, Vertex currentVertex) {
		Vector2D vector = Factory.newVector2D(lastVertex, currentVertex);
		polygon.add(vector);
	}

	protected Vector2D[] getOptimalPolygon(List<Vector2D[]> optimalPolygons) {
		int minimumSegmentNumber = Integer.MAX_VALUE;
		int minIndex = 0;
		for (int i = 0; i < optimalPolygons.size(); i++) {
			int polygonSize = optimalPolygons.get(i).length;

			if (polygonSize < minimumSegmentNumber) {
				minimumSegmentNumber = polygonSize;
				minIndex = i;
			}
		}

		return optimalPolygons.get(minIndex);
	}
}
