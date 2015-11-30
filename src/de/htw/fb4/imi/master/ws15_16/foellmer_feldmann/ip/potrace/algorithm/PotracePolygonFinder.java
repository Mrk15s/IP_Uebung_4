/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import java.util.HashSet;
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

		for (int i = startI + 1; i < outlineVertices.length; i++) {
			Vertex currentVertex = outlineVertices[i];
			storeDirection(currentVertex, this.outlineVertices[i - 1]);

			Vector2D currentVector = Factory.newVector2D(currentVertex, startVertex);

			if (moreThanThreeDirections(currentVector) || abusesConstraint(currentVector)) {
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

		if (VectorUtil.calcCrossProduct(c0, d) > 0) {
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
	 * IPolygonFinder#findPossibleSegments(de.htw.fb4.imi.master.ws15_16.
	 * foellmer_feldmann.ip.Vertex[])
	 */
	@Override
	public Set<Vector2D[]> findPossibleSegments(final int[] closedStraigthPathes) {
		Set<Vector2D[]> possibleSegments = new HashSet<>();

		// TODO uncomment following code, get findNextPossibleSegment working
//		for (int i = 0; i < closedStraigthPathes.length; i++) {
//			Vector2D[] segmentVertices = findNextPossibleSegment(i, closedStraigthPathes);
//
//			possibleSegments.add(segmentVertices);
//		}
		
		Vector2D[] segmentVertices = new Vector2D[closedStraigthPathes.length - 1];

		for (int i = 1; i < closedStraigthPathes.length; i++) {
			segmentVertices[i - 1] = Factory.newVector2D(this.outlineVertices[i - 1], this.outlineVertices[i]);
		}
		possibleSegments.add(segmentVertices);

		return possibleSegments;
	}

	protected Vector2D[] findNextPossibleSegment(final int startI, final int[] closedStraigthPathes) {
		Set<Integer> reachedIndices = new HashSet<>();
		Set<Vector2D> segmentVertices = new HashSet<>();

		int previousI = startI;
		int currentI = closedStraigthPathes[previousI];

		while (currentI != startI && !reachedIndices.contains(currentI)) {
			reachedIndices.add(currentI);
			segmentVertices.add(Factory.newVector2D(this.outlineVertices[currentI], this.outlineVertices[previousI]));

			previousI = currentI;
			currentI = closedStraigthPathes[currentI];
		}

		return segmentVertices.toArray(new Vector2D[segmentVertices.size()]);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.
	 * IPolygonFinder#findOptimalPolygon(java.util.Set)
	 */
	public Vector2D[] findOptimalPolygon(Set<Vector2D[]> possibleSegments) {
		// TODO implement logic to find best segment for this outline
		return possibleSegments.iterator().next();
	}

}
