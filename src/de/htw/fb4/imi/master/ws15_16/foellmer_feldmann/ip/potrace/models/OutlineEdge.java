/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Edge;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.IOutlinePathFinder;

/**
 * An OutlineEdge as used in {@link IOutlinePathFinder} algorithms a connection of two {@link Vertex}
 * - which are neighbours
 * - where the connection/edge vw separates both Vertex so that, foreground (black) is left and background (white) is right
 * 
 * So the edge itself is between the white and black vertex.
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 17.11.2015
 */
public class OutlineEdge extends Edge {	
	private Integer directionX;
	private Integer directionY;	
	
	public OutlineEdge(Vertex white, Vertex black) {
		super(white, black);
	}

	public Vertex getWhite() {
		return getVertexA();
	}
	
	public void setWhite(Vertex v) {
		this.setVertexA(v);
	}
	
	public Vertex getBlack() {
		return getVertexB();
	}
	
	public void setBlack(Vertex w) {
		this.setVertexB(w);
	}

	/**
	 * Get the x direction of this edge from v to w.
	 * @return
	 */
	public int getDirectionX() {
		if (null == directionX) {
			this.calculateDirections();
		}
		
		return directionX;
	}
	
	/**
	 * Get the Y direction of this edge from v to w.
	 * @return
	 */
	public int getDirectionY() {
		if (null == directionY) {
			this.calculateDirections();
		}
		
		return directionY;
	}

	/**
	 * S. Foliensatz S. 14
	 */
	private void calculateDirections() {
		if (vertexIsLeftNeighbourOf(getWhite(), getBlack())) {
			this.directionX = 0;
			this.directionY = 1;
		} else if (vertexIsBelowNeighbourOf(getWhite(), getBlack())) {
			this.directionX = 1;
			this.directionY = 0;
		} else if (vertexIsAboveNeighbourOf(getWhite(), getBlack())) {
			this.directionX = -1;
			this.directionY = 0;
		} else if (vertexIsRightNeighbourOf(getWhite(), getBlack())) {
			this.directionX = 0;
			this.directionY = -1;
		}
	}	

	public boolean vertexIsLeftNeighbourOf(Vertex white, Vertex black) {		
		return (black.getX() - 1 == white.getX()
				&& black.getY() == white.getY()
				);
	}
	
	public boolean vertexIsBelowNeighbourOf(Vertex white, Vertex black) {
		return (white.getY() - 1 == black.getY()
				&& white.getX() == black.getX());
	}
	
	public boolean vertexIsAboveNeighbourOf(Vertex white, Vertex black) {
		return (black.getY() - 1 == white.getY()
				&& black.getX() == white.getX());
	}
	
	public boolean vertexIsRightNeighbourOf(Vertex white, Vertex black) {
		return (white.getX() - 1 == black.getX()
				&& white.getY() == black.getY());
	}	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Edge [getV()=");
		builder.append(getWhite());
		builder.append(", getW()=");
		builder.append(getBlack());
		builder.append("]");
		return builder.toString();
	}	
}
