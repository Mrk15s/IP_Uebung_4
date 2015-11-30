/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip;

/**
 * An edge connects a {@link Vertex} a and a {@link Vertex} b in the direction from a to b.
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 30.11.2015
 */
public class Edge {
	private Vertex vertexA;
	private Vertex vertexB;	
	
	public Edge(Vertex vertexA, Vertex vertexB) {
		super();
		
		this.vertexA = vertexA;
		this.vertexB = vertexB;
	}
	
	public void setVertexA(Vertex vertexA) {
		this.vertexA = vertexA;
	}
	
	public Vertex getVertexA() {
		return vertexA;
	}
	
	public void setVertexB(Vertex vertexB) {
		this.vertexB = vertexB;
	}
	
	public Vertex getVertexB() {
		return vertexB;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vertexA == null) ? 0 : vertexA.hashCode());
		result = prime * result + ((vertexB == null) ? 0 : vertexB.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (vertexA == null) {
			if (other.vertexA != null)
				return false;
		} else if (!vertexA.equals(other.vertexA))
			return false;
		if (vertexB == null) {
			if (other.vertexB != null)
				return false;
		} else if (!vertexB.equals(other.vertexB))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Edge [getVertexA()=");
		builder.append(getVertexA());
		builder.append(", getVertexB()=");
		builder.append(getVertexB());
		builder.append("]");
		return builder.toString();
	}	
}
