/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Factory;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.util.VectorUtil;

/**
 * Implementation of potrace part 2 to find polygons based on straight pathes and possible segments.
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
	 * Array of outlineVertex index -> first index that terminates straight path.
	 */
	private int[] pivots;
	

	/* (non-Javadoc)
	 * @see de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.IPolygonFinder#findStraightPathes(de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline)
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
		
		for (int i = startI + 1; i < outlineVertices.length; i++) {
			Vertex currentVertex = outlineVertices[i];
			Vector2D currentVector = Factory.newVector2D(currentVertex, startVertex);
			
			if (abusesConstraint(currentVector))	{
				this.pivots[startI] = i; // save first index of vertex that terminates straight path of given startVertex
				return;
			}
			
			actualizeConstraint(currentVector);
		}
	}
	
	private Vector2D getStartConstraint() {
		return Factory.newVector2D(0, 0);
	}

	private boolean abusesConstraint(Vector2D currentVector) {
		if (VectorUtil.calcCrossProduct(c0, currentVector) < 0 
				|| VectorUtil.calcCrossProduct(c1, currentVector) > 0) {
			return false;
		}
		
		return true;
	}
	
	private void actualizeConstraint(Vector2D currentVector) {
		if (Math.abs(currentVector.getX()) <= 1
				&& Math.abs(currentVector.getY()) <= 1) {
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

	
	/* (non-Javadoc)
	 * @see de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.IPolygonFinder#findPossibleSegments(de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex[])
	 */
	@Override
	public Vertex[] findPossibleSegments(int[] closedStraigthPathes) {
		// TODO select possible segments
		
		Vertex[] segmentVertices = new Vertex[closedStraigthPathes.length];
		for (int i = 0; i < closedStraigthPathes.length; i++) {
			segmentVertices[i] = this.outlineVertices[i];
		}
		
		return segmentVertices;
	}

}
