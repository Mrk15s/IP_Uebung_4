/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import java.util.ArrayList;
import java.util.List;

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
		int i = startI + 1;

		boolean terminates = false;
		while (!terminates) {
			Vertex currentVertex = outlineVertices[getCyclic(i)];
			storeDirection(currentVertex, this.outlineVertices[getCyclic(i - 1)]);

			Vector2D currentVector = Factory.newVector2D(startVertex, currentVertex);

			if (moreThanThreeDirections(currentVector) || abusesConstraint(currentVector)) {
				this.pivots[startI] = getCyclic(i); // save first index of
													// vertex that
				// terminates straight path of given
				// startVertex
				terminates = true;
				return;
			}

			actualizeConstraint(currentVector);
			i++;
		}
	}

	private int getCyclic(final int i) {
		return i % this.outlineVertices.length;
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

		for (int i = 0; i < closedStraigthPathes.length; i++) {
			int j = closedStraigthPathes[getPreviousIndex(i, closedStraigthPathes)] - 1;

			if (j == -1) {
				j = closedStraigthPathes.length - 1;
			}

			possibleSegments[i] = j;
		}

		return possibleSegments;
		// return closedStraigthPathes;
	}

	protected int getPreviousIndex(int i, int[] closedStraigthPathes) {
		if (i <= 0) {
			return closedStraigthPathes.length - 1;
		}

		return i - 1;
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
		List<Vector2D> bestPolygon = new ArrayList<>();

		for (int startIndex = 0; startIndex < 1; startIndex++) {
			List<Vector2D> newPolygon = buildPolygon(possibleSegments, startIndex);

			if (better(newPolygon, bestPolygon)) {
				bestPolygon = newPolygon;
			}
		}

		return bestPolygon.toArray(new Vector2D[bestPolygon.size()]);
	}

	private boolean better(List<Vector2D> newPolygon, List<Vector2D> bestPolygon) {
		return bestPolygon.size() == 0 || (newPolygon.size() < bestPolygon.size() && newPolygon.size() != 0);
	}

	protected List<Vector2D> buildPolygon(int[] possibleSegments, int startIndex) {
		List<Vector2D> polygon = new ArrayList<>();
		final Vertex startVertex = this.outlineVertices[startIndex];
		Vertex lastVertex = null;
		int i = startIndex;
		boolean isSecoundRound = false;

		boolean terminates = false;
		while (!terminates) {
			int nextPossibleOutlineVertexIndex = possibleSegments[i];
			Vertex nextVertex = this.outlineVertices[nextPossibleOutlineVertexIndex];
			lastVertex = this.outlineVertices[i];

			if (nextPossibleOutlineVertexIndex < i) {
				// check if we reached the end of the array (next outline index will switch to the beginning of the array)
				isSecoundRound = true;
			}
			
			if (isSecoundRound && nextPossibleOutlineVertexIndex > startIndex) {
				// cycle completed (start index was reached again)
				terminates = true;
			} else {
				addConnection(polygon, lastVertex, nextVertex);
				lastVertex = nextVertex;
				i = nextPossibleOutlineVertexIndex;
			}
		}

		// connect lastVertex and startVertex
		if (null != lastVertex && !startVertex.equals(lastVertex)) {
			addConnection(polygon, lastVertex, startVertex);
		}

		return polygon;
	}

	protected void addConnection(List<Vector2D> polygon, Vertex lastVertex, Vertex currentVertex) {
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
